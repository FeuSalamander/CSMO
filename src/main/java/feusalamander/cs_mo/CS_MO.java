package feusalamander.cs_mo;

import feusalamander.cs_mo.Commands.Command;
import feusalamander.cs_mo.Commands.Completer;
import feusalamander.cs_mo.Gui.AtBuyMenu;
import feusalamander.cs_mo.Gui.TBuyMenu;
import feusalamander.cs_mo.Gui.PlayGui;
import feusalamander.cs_mo.Listerners.GuiClicks;
import feusalamander.cs_mo.Listerners.onJoin;
import feusalamander.cs_mo.Managers.Game;
import feusalamander.cs_mo.Runnables.ActionBarTick;
import feusalamander.cs_mo.Configs.Config;
import feusalamander.cs_mo.Managers.Map;
import feusalamander.cs_mo.Configs.MapConfig;
import feusalamander.cs_mo.Managers.PlayerData;
import feusalamander.cs_mo.Runnables.Starting;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;
@SuppressWarnings("deprecation")
public final class CS_MO extends JavaPlugin {
    public static CS_MO main;
    private PlayGui playgui;
    private Config config;
    private PlayerData playerData;
    private ActionBarTick actionBarTick;
    private final List<Map> maps = new ArrayList<>();
    private MapConfig mapConf;
    private Scoreboard scoreboard;
    private final List<HashMap<Player, Integer>> queue = new ArrayList<>();
    private final List<Player> none = new ArrayList<>();
    private final List<Starting> starting = new ArrayList<>();
    private final List<Game> games = new ArrayList<>();
    public final Random random = new Random();

    @Override
    public void onEnable() {
        getLogger().info("CS:MO by FeuSalamander is loading");
        main =this;
        saveDefaultConfig();
        loadClasses();
        Objects.requireNonNull(getCommand("cs")).setExecutor(new Command());
        Objects.requireNonNull(getCommand("cs")).setTabCompleter(new Completer());
        getServer().getPluginManager().registerEvents(new GuiClicks(), this);
        getServer().getPluginManager().registerEvents(new onJoin(), this);
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        none.addAll(getServer().getOnlinePlayers());
        Objects.requireNonNull(Bukkit.getWorld("world")).setGameRule(GameRule.KEEP_INVENTORY, true);
    }
    @Override
    public void onDisable() {
        playerData.save();
        getLogger().info("CS:MO by FeuSalamander is unloaded");
        for(Game game : games){
            game.getBar().removeAll();
            game.getBar2().removeAll();
            for(Item item : game.getItems())item.remove();
            for(Player p : game.getSpecs().keySet())removeFromSpec(p, game);
            if(game.getBombPlanted().left())for(Entity entity : game.getBombPlanted().right().getNearbyEntities(0.1, 0.1, 0.1))if(entity instanceof ArmorStand){entity.remove();return;}
        }
        for(Player p : getServer().getOnlinePlayers()){
            p.getInventory().clear();
            p.setScoreboard(getScoreboard());
        }
    }
    private void loadClasses(){
        this.config = new Config(this.getConfig());
        this.playgui = new PlayGui();
        this.playerData = new PlayerData();
        this.actionBarTick = new ActionBarTick();
        actionBarTick.runTaskTimerAsynchronously(this, 0, 40);
        mapConf = new MapConfig();
        loadMaps();
        AtBuyMenu.buildGUi();
        TBuyMenu.buildGUi();
    }
    private void loadMaps(){
        for(String mapKey : mapConf.getMaps()){
            final ConfigurationSection section = mapConf.getConfig().getConfigurationSection(mapKey);
            assert section != null;
            String id = section.getName();
            final Map floor = new Map(id);
            this.maps.add(floor);
        }
    }
    public void addToSpec(Player p, Game game){
        Player p2 = null;
        int crash = 0;
        while(p2 == null||p2 == p){
            if(crash >4)break;
            if(game.getCT().contains(p)){
                p2 = game.getCT().get(main.random.nextInt(game.getCT().size()));
            }else {
                p2 = game.getT().get(main.random.nextInt(game.getT().size()));
            }
            crash++;
        }
        game.getSpecs().put(p, p2);
        p.setInvulnerable(true);
        p.setInvisible(true);
        p.setAllowFlight(true);
        p.setFlying(true);
        for(Player all : Bukkit.getOnlinePlayers())all.hidePlayer(p);
        assert p2 != null;
        p.hidePlayer(p2);
        p.sendTitle("§cYou are spectating "+p2.getName(), "");
    }
    public void removeFromSpec(Player p, Game game){
        p.setInvisible(false);
        p.setInvulnerable(false);
        p.showPlayer(game.getSpecs().get(p));
        game.getSpecs().remove(p);
        p.setAllowFlight(false);
        for(Player all : Bukkit.getOnlinePlayers())all.showPlayer(p);
    }
    public Inventory getPlayGui() {
        return playgui.getMenu();
    }
    public Config getConf() {
        return config;
    }
    public PlayerData getPlayerData() {
        return playerData;
    }
    public List<HashMap<Player, Integer>> getQueue() {
        return queue;
    }
    public ActionBarTick getActionBarTick() {
        return actionBarTick;
    }
    public List<Player> getNone() {
        return none;
    }
    public List<Map> getMaps(){
        return maps;
    }
    public MapConfig getMapConf(){
        return mapConf;
    }
    public void removeQueue(Player p){
        for(Starting timers : starting){timers.getPlayers().remove(p);}
        for(HashMap<Player, Integer> map : queue){
            if(map.containsKey(p)){
                map.remove(p);
                if(map.isEmpty())queue.remove(map);
                return;
            }
        }
    }
    public List<Starting> getStarting() {
        return starting;
    }
    public List<Game> getGames() {
        return games;
    }
    public Scoreboard getScoreboard(){
        return scoreboard;
    }
}
