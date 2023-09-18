package feusalamander.cs_mo;

import feusalamander.cs_mo.Commands.Command;
import feusalamander.cs_mo.Commands.Completer;
import feusalamander.cs_mo.Gui.GuiTool;
import feusalamander.cs_mo.Gui.PlayGui;
import feusalamander.cs_mo.Listerners.GuitClicks;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class CS_MO extends JavaPlugin {
    public static CS_MO main;
    private GuiTool guitool;
    private PlayGui playgui;

    @Override
    public void onEnable() {
        getLogger().info("CS:MO by FeuSalamander is loading");
        main =this;
        saveDefaultConfig();
        loadClasses();
        Objects.requireNonNull(getCommand("cs")).setExecutor(new Command());
        Objects.requireNonNull(getCommand("cs")).setTabCompleter(new Completer());
        getServer().getPluginManager().registerEvents(new GuitClicks(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("CS:MO by FeuSalamander is unloading");
    }
    private void loadClasses(){
        this.guitool = new GuiTool();
        this.playgui = new PlayGui();
    }
    public GuiTool getGuitool(){
        return guitool;
    }
    public Inventory getPlaygui() {
        return playgui.getMenu();
    }
}
