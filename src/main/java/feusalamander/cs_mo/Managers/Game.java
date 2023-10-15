package feusalamander.cs_mo.Managers;

import feusalamander.cs_mo.Gui.GuiTool;
import feusalamander.cs_mo.Runnables.Defusing;
import feusalamander.cs_mo.Runnables.GameTick;
import feusalamander.cs_mo.Runnables.Planting;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.*;
import org.bukkit.scoreboard.*;

import java.util.*;

import static feusalamander.cs_mo.CS_MO.main;
@SuppressWarnings("deprecation")
public class Game implements Listener {
    private final int gameElo;
    private String mapName;
    private final List<Player> players;
    private final List<Player> CT = new ArrayList<>(5);
    private final List<Player> T = new ArrayList<>(5);
    private final Location[] place;
    private final List<Location> ctSpawns = new ArrayList<>(5);
    private final List<Location> tSpawns = new ArrayList<>(5);
    private final HashMap<Player, Pair<Integer, int[]>> moneyAndStats = new HashMap<>(10);
    private BossBar bar;
    private BossBar bar2;
    private final int[] score = new int[2];
    private int round = 1;
    private Scoreboard sb;
    private GameTick tick;
    private Pair<Boolean, Location> bombPlanted = Pair.of(false, null);
    private final List<Item> items = new ArrayList<>();
    public boolean planting;
    public boolean defusing;
    private Pair<Map, Integer> map;
    private ItemStack miniMapCT;
    private ItemStack miniMapT;
    public Item bombDropped;
    private final List<MiniMapRenderer> renderers = new ArrayList<>(2);
    private final HashMap<UUID, Boolean> disconnected= new HashMap<>(10);
    private final HashMap<Player, Player> specs = new HashMap<>();
    public Game(List<Player> players, int elo){
        this.players = players;
        this.gameElo = elo;
        main.getGames().add(this);
        main.getServer().getPluginManager().registerEvents(this, main);
        place = choosePlace();
        createTeams();
        createSpawns();
        chooseSpawns();
        scoreboard();
        bossBar();
        createMiniMap();
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
                    this.map = Pair.of(map, i);
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
            if(CT.size() < players.size()/2){
                CT.add(p);
            }else{
                T.add(p);
            }
            moneyAndStats.put(p, Pair.of(800, new int[]{0, 0}));
        }
        for(Player p : CT)p.sendMessage("§9Your are a CT");
        for(Player p : T)p.sendMessage("§6Your are a T");
    }
    private void createSpawns(){
        int size = Math.max(CT.size(), T.size());
        Location mainAT = place[0].clone();
        Location mainT = place[1].clone();
        for(int i = 0; i<size; i++){
            double x = place[0].x()+(main.random.nextInt(6)-2.5);
            double z = place[0].z()+(main.random.nextInt(6)-2.5);
            mainAT.setX(x);
            mainAT.setZ(z);
            ctSpawns.add(mainAT.clone());
        }
        for(int i = 0; i<size; i++){
            double x = place[1].x()+(main.random.nextInt(6)-2.5);
            double z = place[1].z()+(main.random.nextInt(6)-2.5);
            mainT.setX(x);
            mainT.setZ(z);
            tSpawns.add(mainT.clone());
        }
    }
    public void chooseSpawns(){
        List<Location> dumpAT = new ArrayList<>(ctSpawns);
        List<Location> dumpT = new ArrayList<>(tSpawns);
        for(Player p : CT){
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
        objective.getScore("§9CT: ").setScore(5);
        objective.getScore("§6T: ").setScore(4);
        objective.getScore("   ").setScore(3);
        objective.getScore("    ").setScore(1);
        objective.getScore("§e"+main.getConf().getServerip()).setScore(0);

        Team bombV = sb.registerNewTeam("bombV");
        Team atV = sb.registerNewTeam("ctV");
        Team tV = sb.registerNewTeam("tV");
        bombV.addEntry("§cBomb: ");
        atV.addEntry("§9CT: ");
        tV.addEntry("§6T: ");

        Team AT = sb.registerNewTeam("CT");
        Team T = sb.registerNewTeam("T");

        AT.setPrefix("§9CT ");
        T.setPrefix("§6T ");

        AT.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        T.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);

        for(Player p : players){
            p.setScoreboard(sb);
            if(this.CT.contains(p))AT.addPlayer(p);
            if(this.T.contains(p))T.addPlayer(p);
            Objects.requireNonNull(p.getScoreboard().getObjective("§e§lMC:MO")).getScore("§aMoney: §f"+moneyAndStats.get(p).left()).setScore(2);
        }
        bombV.setSuffix("Not planted");
        atV.setSuffix(String.valueOf(AT.getSize()));
        tV.setSuffix(String.valueOf(T.getSize()));
    }
    private void bossBar(){
        bar = Bukkit.createBossBar("§90 §f1:55 §60", BarColor.BLUE, BarStyle.SEGMENTED_12);
        bar2 = Bukkit.createBossBar("§90 §f1:55 §60", BarColor.RED, BarStyle.SEGMENTED_12);
        bar.setProgress(0);
        bar2.setProgress(0);
        for(Player p : CT)bar.addPlayer(p);
        for(Player p : T)bar2.addPlayer(p);
    }
    public void updateBar(){
        if(round>24)return;
        if(round < 13){
            bar.setProgress((double) (round-1) /12);
            bar2.setProgress((double) (round-1) /12);
        }else{
            bar.setProgress((double) (round-13)/12);
            bar2.setProgress((double) (round-13)/12);
        }
    }
    private void start(){
        tick = new GameTick(this);
        tick.runTaskTimer(main, 0, 20);
        Inventory();
        giveShop(true);
        tick.bomb();
        for(Player p : CT){
            p.getInventory().setItem(EquipmentSlot.OFF_HAND, miniMapCT);
            p.getInventory().setItem(8, GuiTool.pane);
        }
        for(Player p : T){
            p.getInventory().setItem(EquipmentSlot.OFF_HAND, miniMapT);
            p.getInventory().setItem(8, GuiTool.pane);
        }
    }
    private void Inventory(){
        for(Player p : players){
            for(int i = 0; i<36; i++)p.getInventory().setItem(i, GuiTool.pane);
            p.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
            p.getInventory().setItem(1, new ItemStack(Material.FEATHER));
        }
    }
    public void changeSide(){
        int dump = score[0];
        score[0] = score[1];
        score[1] = dump;
        List<Player> dumpList = new ArrayList<>(CT);
        CT.clear();
        CT.addAll(T);
        T.clear();
        T.addAll(dumpList);
        bar.removeAll();
        bar2.removeAll();
        for(Player p : CT)bar.addPlayer(p);
        for(Player p : T)bar2.addPlayer(p);
        renderers.get(0).changeSide(CT);
        renderers.get(1).changeSide(T);
        for(Player p : CT)p.getInventory().setItem(EquipmentSlot.OFF_HAND, miniMapCT);
        for(Player p : T)p.getInventory().setItem(EquipmentSlot.OFF_HAND, miniMapT);
        for(UUID p : disconnected.keySet())disconnected.replace(p, !disconnected.get(p));
    }
    public void giveShop(boolean give){
        if(give){
            for(Player p : players){
                p.getInventory().setItem(8, GuiTool.shop);
            }
        }else{
            for(Player p : players){
                p.getInventory().setItem(8, GuiTool.pane);
            }
        }
    }
    public void plant(Player p){
        if(planting)return;
        if(!T.contains(p))return;
        new Planting(p, this).runTaskTimer(main, 0, 4);
    }
    public void defuse(Player p){
        if(defusing)return;
        new Defusing(p, this).runTaskTimer(main, 0, 20);
    }
    private void createMiniMap(){
        //CT
        ItemStack item = GuiTool.getItem(Material.FILLED_MAP, "§aMap");
        MapMeta mapMeta = (MapMeta) item.getItemMeta();
        MapView mapView = Bukkit.createMap(Objects.requireNonNull(Bukkit.getWorld("world")));
        MiniMapRenderer renderer = new MiniMapRenderer(this, mapView, CT);
        renderers.add(renderer);
        mapMeta.setMapView(mapView);
        item.setItemMeta(mapMeta);
        miniMapCT = item;
        //T
        ItemStack item2 = GuiTool.getItem(Material.FILLED_MAP, "§aMap");
        MapMeta mapMeta2 = (MapMeta) item2.getItemMeta();
        MapView mapView2 = Bukkit.createMap(Objects.requireNonNull(Bukkit.getWorld("world")));
        MiniMapRenderer renderer2 = new MiniMapRenderer(this, mapView2, T);
        renderers.add(renderer2);
        mapMeta2.setMapView(mapView2);
        item2.setItemMeta(mapMeta2);
        miniMapT = item2;
    }
    public List<Player> getPlayers() {
        return players;
    }
    public BossBar getBar() {
        return bar;
    }
    public BossBar getBar2() {
        return bar2;
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
    public List<Player> getCT() {
        return CT;
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
    public Location[] getPlace() {
        return place;
    }
    public GameTick getTick() {
        return tick;
    }
    public HashMap<Player, Player> getSpecs() {
        return specs;
    }
    public List<MiniMapRenderer> getRenderers() {
        return renderers;
    }
    public int getGameElo() {
        return gameElo;
    }
    public HashMap<Player, Pair<Integer, int[]>> getMoneyAndStats() {
        return moneyAndStats;
    }
    public Pair<Map, Integer> getMap() {
        return map;
    }
    public Item getBombDropped() {
        return bombDropped;
    }
    public void removeMoney(Player p, int money){
        Objects.requireNonNull(p.getScoreboard().getObjective("§e§lMC:MO")).getScore("§aMoney: §f"+this.moneyAndStats.get(p).left()).resetScore();
        int[] stats = this.moneyAndStats.get(p).right();
        this.moneyAndStats.replace(p, Pair.of(money, stats));
        Objects.requireNonNull(p.getScoreboard().getObjective("§e§lMC:MO")).getScore("§aMoney: §f"+this.moneyAndStats.get(p).left()).setScore(2);
    }
    public void removeBomb(){
        if(getBombPlanted().left())for(Entity entity : getBombPlanted().right().getNearbyEntities(0.5, 0.5, 0.5))if(entity instanceof ArmorStand)entity.remove();
        Objects.requireNonNull(getSb().getTeam("bombV")).setSuffix("Not Planted");
        setBombPlanted(false, null);
    }
    public HashMap<UUID, Boolean> getDisconnected() {
        return disconnected;
    }

    public void remove(Player p){
        p.setScoreboard(main.getScoreboard());
        bar.removePlayer(p);
        bar2.removePlayer(p);
        if(CT.contains(p)){
            disconnected.put(p.getUniqueId(), true);
            CT.remove(p);
        }else {
            disconnected.put(p.getUniqueId(), false);
            T.remove(p);
        }
        players.remove(p);
    }
    public void reconnect(Player p){
        main.getNone().remove(p);
        players.add(p);
        if(disconnected.get(p.getUniqueId())){
            CT.add(p);
            bar.addPlayer(p);
        }else {
            T.add(p);
            bar2.addPlayer(p);
        }
        disconnected.remove(p.getUniqueId());
        p.setScoreboard(sb);
        main.addToSpec(p, this);
    }
    @EventHandler
    private void onMove(PlayerMoveEvent e){
        if(!players.contains(e.getPlayer()))return;
        if(!e.hasExplicitlyChangedPosition())return;
        if(tick.isRest()){e.setCancelled(true);return;}
        if(getSpecs().containsKey(e.getPlayer())){e.setCancelled(true);return;}
        if(getSpecs().containsValue(e.getPlayer())){
            for(Player p : getSpecs().keySet()){
                if(getSpecs().get(p).equals(e.getPlayer())){
                    p.teleport(e.getPlayer());
                }
            }
        }
        if(!getT().contains(e.getPlayer()))return;
        if(!e.getPlayer().getItemInHand().getType().equals(Material.NETHER_STAR))return;
        if(e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.BEDROCK))e.setCancelled(true);
    }
    @EventHandler
    private void onHit(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof ArmorStand s&& Objects.requireNonNull(s.getCustomName()).equalsIgnoreCase("§4Bomb")&&e.getDamager() instanceof Player p&& CT.contains(p)){
            e.setDamage(0);
            defuse(p);
        }
        if(e.getDamager() instanceof Player p&&e.getEntity() instanceof Player p2){
            if(!tick.isRest())return;
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
            getRenderers().get(1).changeType(MapCursor.Type.MANSION, e.getPlayer());
            getRenderers().get(1).changeBombT(null);
            bombDropped = null;
            getRenderers().get(0).changeBombAT(null);
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
        if(item.getType().equals(Material.NETHER_STAR)){
            getRenderers().get(1).changeType(MapCursor.Type.GREEN_POINTER, e.getPlayer());
            getRenderers().get(1).changeBombT(e.getItemDrop().getLocation());
            bombDropped = e.getItemDrop();
        }
    }
}
