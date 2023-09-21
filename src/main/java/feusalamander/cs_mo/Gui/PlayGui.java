package feusalamander.cs_mo.Gui;

import feusalamander.cs_mo.CS_MO;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

@SuppressWarnings("deprecation")

public class PlayGui {
    private final CS_MO main = CS_MO.main;
    private final GuiTool tool = main.getGuitool();
    private Inventory menu;
    public PlayGui(){
        this.menu = Bukkit.createInventory(null, 27, "§6Ranked CS:MO");
        buildGUi();
    }
    private void buildGUi(){
        for(int i = 0; i<27; i++){menu.setItem(i, tool.pane());}
        tool.addSkull(menu,
                18,
                "§cYour Stats",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjBkYzY4YWI2M2FkNWJhNjE5NjlmZjBiODk3MDEyYjcyMTM2Yzg2Mjk2MWM4NjA1ZDIyOTQwMDdjMDdlZjU4In19fQ");
        tool.addSkull(menu,
                13,
                "§4Play Ranked CS:MO",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmE4ZjZiMTMxZWY4NDdkOTE2MGU1MTZhNmY0NGJmYTkzMjU1NGQ0MGMxOGE4MTc5NmQ3NjZhNTQ4N2I5ZjcxMCJ9fX0");
    }
    public Inventory getMenu() {
        return menu;
    }
}
