package feusalamander.cs_mo.Gui;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
@SuppressWarnings("deprecation")
public class GuiTool {
    private ItemStack pane;
    public GuiTool(){
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        pane = item;
    }
    public void addItem(Inventory menu,
                           Material material,
                           String name,
                           List<String> lore,
                        int slot){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        menu.setItem(slot, item);
    }
    public void addItem(Inventory menu,
                        Material material,
                        String name,
                        int slot){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        menu.setItem(slot, item);
    }
    public ItemStack pane(){
        return pane;
    }
}
