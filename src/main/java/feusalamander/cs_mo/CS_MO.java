package feusalamander.cs_mo;

import feusalamander.cs_mo.Commands.Command;
import feusalamander.cs_mo.Commands.Completer;
import feusalamander.cs_mo.Gui.GuiTool;
import feusalamander.cs_mo.Gui.PlayGui;
import feusalamander.cs_mo.Listerners.GuiClicks;
import feusalamander.cs_mo.Listerners.onJoin;
import feusalamander.cs_mo.Runnables.ActionBarTick;
import feusalamander.cs_mo.Configs.Config;
import feusalamander.cs_mo.Managers.Map;
import feusalamander.cs_mo.Configs.MapConfig;
import feusalamander.cs_mo.Managers.PlayerData;
import feusalamander.cs_mo.Runnables.Starting;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CS_MO extends JavaPlugin {
    public static CS_MO main;
    private GuiTool guitool;
    private PlayGui playgui;
    private Config config;
    private PlayerData playerData;
    private ActionBarTick actionBarTick;
    private final List<Map> maps = new ArrayList<>();
    private MapConfig mapConf;
    private final List<Pair<Integer, List<Player>>> queue = new ArrayList<>();
    private final List<Player> none = new ArrayList<>();
    private final List<Starting> starting = new ArrayList<>();

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
    }
    @Override
    public void onDisable() {
        playerData.save();
        getLogger().info("CS:MO by FeuSalamander is unloaded");
    }
    private void loadClasses(){
        this.config = new Config(this.getConfig());
        this.guitool = new GuiTool();
        this.playgui = new PlayGui();
        this.playerData = new PlayerData();
        this.actionBarTick = new ActionBarTick();
        actionBarTick.runTaskTimerAsynchronously(this, 0, 40);
        mapConf = new MapConfig();
        loadMaps();
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
    public GuiTool getGuitool(){
        return guitool;
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
    public List<Pair<Integer, List<Player>>> getQueue() {
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
        for(Pair<Integer, List<Player>> pair : queue){
            if(pair.right().contains(p)){
                pair.right().remove(p);
                if(pair.right().isEmpty()){
                    queue.remove(pair);
                }
                return;
            }
        }
        for(Starting timers : starting){timers.getPlayers().remove(p);}
    }
    public List<Starting> getStarting() {
        return starting;
    }
}
