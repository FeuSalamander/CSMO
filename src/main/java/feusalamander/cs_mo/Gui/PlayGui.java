package feusalamander.cs_mo.Gui;

import feusalamander.cs_mo.CS_MO;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
        tool.addItem(menu, Material.PLAYER_HEAD, "§4Play Ranked CS:MO", 13);
    }
    public Inventory getMenu() {
        return menu;
    }
}
