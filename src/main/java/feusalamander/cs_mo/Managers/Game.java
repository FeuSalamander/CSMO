package feusalamander.cs_mo.Managers;

import feusalamander.cs_mo.Enum.Weapons;
import feusalamander.cs_mo.Gui.GuiTool;
import feusalamander.cs_mo.Runnables.GameTick;
import feusalamander.cs_mo.Runnables.Planting;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.*;

import java.util.*;

import static feusalamander.cs_mo.CS_MO.main;
@SuppressWarnings("deprecation")
public class Game implements Listener {
    private String mapName;
    private final List<Player> players;
    private final List<Player> AT = new ArrayList<>();
    private final List<Player> T = new ArrayList<>();
    private final Location[] place;
    private final List<Location> atSpawns = new ArrayList<>();
    private final List<Location> tSpawns = new ArrayList<>();
    private final HashMap<Player, Integer> money = new HashMap<>();
    private BossBar bar;
    private final int[] score = new int[2];
    private int round = 1;
    private Scoreboard sb;
    private GameTick tick;
    private Pair<Boolean, Location> bombPlanted = Pair.of(false, null);
    private final List<Item> items = new ArrayList<>();
    public Game(List<Player> players){
        this.players = players;
        main.getGames().add(this);
        main.getServer().getPluginManager().registerEvents(this, main);
        place = choosePlace();
        createTeams();
        createSpawns();
        chooseSpawns();
        scoreboard();
        bossBar();

        start();
    }
    private Location[] choosePlace(){
        int crash = 0;
        while (place == null&&crash<main.getMaps().size()){
            Map map = main.getMaps().get(main.random.nextInt(main.getMaps().size()));
            mapName = map.getName();
            for(int i = 0; i<map.getSchematics().size(); i++){
                Pair<Boolean, Location[]> pair = map.getSchematics().get(i);
                if(pair.left()){
                    Location[] map1 = pair.right();
                    map.setPair(false, i);
                    return map1;
                }
            }
            crash++;
            for(Player p : players)p.sendMessage("§4No available maps were found");
        }
        return null;
    }
    private void createTeams(){
        List<Player> dump = new ArrayList<>(players);
        for(int i = 0; i<players.size(); i++){
            Player p = dump.get(main.random.nextInt(dump.size()));
            dump.remove(p);
            if(AT.size() < players.size()/2){
                AT.add(p);
            }else{
                T.add(p);
            }
            money.put(p, 800);
        }
        for(Player p : AT)p.sendMessage("§9Your are an AT");
        for(Player p : T)p.sendMessage("§6Your are a T");
    }
    private void createSpawns(){
        int size = Math.max(AT.size(), T.size());
        Location mainAT = place[0].clone();
        Location mainT = place[1].clone();
        for(int i = 0; i<size; i++){
            double x = place[0].x()+(main.random.nextInt(6)-2.5);
            double z = place[0].z()+(main.random.nextInt(6)-2.5);
            mainAT.setX(x);
            mainAT.setZ(z);
            atSpawns.add(mainAT);
        }
        for(int i = 0; i<size; i++){
            double x = place[1].x()+(main.random.nextInt(6)-2.5);
            double z = place[1].z()+(main.random.nextInt(6)-2.5);
            mainT.setX(x);
            mainT.setZ(z);
            tSpawns.add(mainT);
        }
    }
    public void chooseSpawns(){
        List<Location> dumpAT = new ArrayList<>(atSpawns);
        List<Location> dumpT = new ArrayList<>(tSpawns);
        for(Player p : AT){
            Location loc = dumpAT.get(main.random.nextInt(dumpAT.size()));
            dumpAT.remove(loc);
            p.teleport(loc);
        }
        for(Player p : T){
            Location loc = dumpT.get(main.random.nextInt(dumpT.size()));
            dumpT.remove(loc);
            p.teleport(loc);
        }
    }
    private void scoreboard(){
        sb = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = sb.registerNewObjective("§e§lMC:MO", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.getScore("Map: §a"+mapName).setScore(9);
        objective.getScore(" ").setScore(8);
        objective.getScore("§cBomb: ").setScore(7);
        objective.getScore("  ").setScore(6);
        objective.getScore("§9AT: ").setScore(5);
        objective.getScore("§6T: ").setScore(4);
        objective.getScore("   ").setScore(3);
        objective.getScore("    ").setScore(1);
        objective.getScore("§e"+main.getConf().getServerip()).setScore(0);

        Team bombV = sb.registerNewTeam("bombV");
        Team atV = sb.registerNewTeam("atV");
        Team tV = sb.registerNewTeam("tV");
        bombV.addEntry("§cBomb: ");
        atV.addEntry("§9AT: ");
        tV.addEntry("§6T: ");

        Team AT = sb.registerNewTeam("AT");
        Team T = sb.registerNewTeam("T");

        AT.setPrefix("§9AT ");
        T.setPrefix("§6T ");

        AT.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        T.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);

        for(Player p : players){
            p.setScoreboard(sb);
            if(this.AT.contains(p))AT.addPlayer(p);
            if(this.T.contains(p))T.addPlayer(p);
            Objects.requireNonNull(p.getScoreboard().getObjective("§e§lMC:MO")).getScore("§aMoney: §f"+money.get(p)).setScore(2);
        }
        bombV.setSuffix("Not planted");
        atV.setSuffix(String.valueOf(AT.getSize()));
        tV.setSuffix(String.valueOf(T.getSize()));
    }
    private void bossBar(){
        bar = Bukkit.createBossBar("§90 §f1:55 §60", BarColor.WHITE, BarStyle.SEGMENTED_12);
        bar.setProgress(0);
        for(Player p : players)bar.addPlayer(p);
    }
    public void updateBar(){
        bar.setProgress((double) round /20);
    }
    private void start(){
        tick = new GameTick(this);
        tick.runTaskTimer(main, 0, 20);
        Inventory();
        giveShop(true);
        tick.bomb();
    }
    private void unload(){
        HandlerList.unregisterAll(this);
    }
    private void Inventory(){
        for(Player p : players){
            for(int i = 0; i<36; i++)p.getInventory().setItem(i, GuiTool.pane);
            p.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
            p.getInventory().setItem(1, new ItemStack(Material.FEATHER));
        }
    }
    public void remove(Player p){
        players.remove(p);
        p.setScoreboard(main.getScoreboard());
        bar.removePlayer(p);
    }
    public void changeSide(){
    }
    public void giveShop(boolean give){
        if(give){
            for(Player p : players){
                p.getInventory().setItem(8, main.getShopItem());
            }
        }else{
            for(Player p : players){
                p.getInventory().setItem(8, GuiTool.pane);
            }
        }
    }
    public void plant(Player p){
        if(!T.contains(p))return;
        if(!p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.BEDROCK))return;
        new Planting(p, this).runTaskTimer(main, 0, 4);
    }
    public List<Player> getPlayers() {
        return players;
    }
    public BossBar getBar() {
        return bar;
    }
    public int[] getScore() {
        return score;
    }
    public void addScore(int i){
        score[i]++;
    }
    public void addRound(){
        round++;
    }
    public int getRound() {
        return round;
    }
    public List<Player> getAT() {
        return AT;
    }
    public List<Player> getT() {
        return T;
    }
    public Pair<Boolean, Location> getBombPlanted(){
        return bombPlanted;
    }
    public void setBombPlanted(boolean bool, Location loc){
        bombPlanted = Pair.of(bool, loc);
    }
    public List<Item> getItems() {
        return items;
    }
    public Scoreboard getSb() {
        return sb;
    }
    public GameTick getTick() {
        return tick;
    }
    public HashMap<Player, Integer> getMoney() {
        return money;
    }
    public void removeMoney(Player p, int money){
        Objects.requireNonNull(p.getScoreboard().getObjective("§e§lMC:MO")).getScore("§aMoney: §f"+this.money.get(p)).resetScore();
        this.money.replace(p, money);
        Objects.requireNonNull(p.getScoreboard().getObjective("§e§lMC:MO")).getScore("§aMoney: §f"+this.money.get(p)).setScore(2);
    }

    @EventHandler
    private void onMove(PlayerMoveEvent e){
        if(!players.contains(e.getPlayer()))return;
        if(!e.hasExplicitlyChangedPosition())return;
        if(tick.isRest()){e.setCancelled(true);return;}
        if(!getT().contains(e.getPlayer()))return;
        if(e.getPlayer().getItemInHand().getType().equals(Material.NETHER_STAR))e.setCancelled(true);
    }
    @EventHandler
    private void onHit(EntityDamageByEntityEvent e){
        if(!tick.isRest())return;
        if(e.getDamager() instanceof Player p&&e.getEntity() instanceof Player p2){
            if(players.contains(p)||players.contains(p2)){
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    private void onPickUp(PlayerAttemptPickupItemEvent e){
        if(!e.getItem().getItemStack().hasItemMeta())return;
        ItemMeta item = e.getItem().getItemStack().getItemMeta();
        if(item.getDisplayName().equalsIgnoreCase("§4Bomb")&&T.contains(e.getPlayer())){
            e.getPlayer().getInventory().setItem(7, GuiTool.bomb);
            e.getItem().remove();
        }
    }
    @EventHandler
    private void onDrop(PlayerDropItemEvent e){
        if(!players.contains(e.getPlayer()))return;
        ItemStack item = e.getItemDrop().getItemStack();
        if(item.getType().equals(Material.IRON_SWORD)||item.getType().equals(Material.CHEST)||item.getType().equals(Material.SHEARS)){e.setCancelled(true);return;}
        if(!e.getItemDrop().getItemStack().hasItemMeta())return;
        ItemMeta meta = item.getItemMeta();
        if(meta.getDisplayName().equalsIgnoreCase(" ")){e.setCancelled(true);return;}
        e.getPlayer().getInventory().setItem(e.getPlayer().getInventory().firstEmpty(), GuiTool.pane);
        e.getItemDrop().setUnlimitedLifetime(true);
        items.add(e.getItemDrop());
    }
}
