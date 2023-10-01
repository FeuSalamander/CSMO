package feusalamander.cs_mo.Gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

@SuppressWarnings("deprecation")

public class PlayGui {
    private final Inventory menu;
    public PlayGui(){
        this.menu = Bukkit.createInventory(null, 27, "§6Ranked CS:MO");
        buildGUi();
    }
    private void buildGUi(){
        for(int i = 0; i<27; i++)menu.setItem(i, GuiTool.pane);
        menu.setItem(18, GuiTool.getSkull("§cYour Stats",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjBkYzY4YWI2M2FkNWJhNjE5NjlmZjBiODk3MDEyYjcyMTM2Yzg2Mjk2MWM4NjA1ZDIyOTQwMDdjMDdlZjU4In19fQ"));
        menu.setItem(13, GuiTool.getSkull("§4Play Ranked CS:MO",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmE4ZjZiMTMxZWY4NDdkOTE2MGU1MTZhNmY0NGJmYTkzMjU1NGQ0MGMxOGE4MTc5NmQ3NjZhNTQ4N2I5ZjcxMCJ9fX0"));
    }
    public Inventory getMenu() {
        return menu;
    }
}
