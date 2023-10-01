package feusalamander.cs_mo.Gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("deprecation")
public final class GuiTool {
    public static final ItemStack pane = createPane();
    public static final ItemStack bomb = createBomb();
    public static final ItemStack kit = createKit();
    public static ItemStack getItem(
                           Material material,
                           String name,
                           List<String> lore){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack getItem(Material material,
                        String name){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack getSkull(String name, String skin){
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        Bukkit.getUnsafe().modifyItemStack(item, "{SkullOwner:{Id:\"" + new UUID(skin.hashCode(), skin.hashCode()) + "\",Properties:{textures:[{Value:\"" + skin + "\"}]}}}");
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createPane(){
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createBomb(){
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§4Bomb");
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createKit(){
        ItemStack item = new ItemStack(Material.SHEARS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§9Defuse Kit");
        item.setItemMeta(meta);
        return item;
    }
}
