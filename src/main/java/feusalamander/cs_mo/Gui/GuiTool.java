package feusalamander.cs_mo.Gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.*;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static feusalamander.cs_mo.CS_MO.main;

@SuppressWarnings("deprecation")
public final class GuiTool {
    public static final ItemStack pane = getItem(Material.BLACK_STAINED_GLASS_PANE, " ");
    public static final ItemStack bomb = getItem(Material.NETHER_STAR, "§4Bomb");
    public static final ItemStack kit = getItem(Material.SHEARS, "§9Defuse Kit");
    public static final ItemStack shop = getItem(Material.CHEST, "§aShop");
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
    public static ItemStack getSkull(String name, Player p){
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setDisplayName(name);
        meta.setOwningPlayer(p);
        item.setItemMeta(meta);
        return item;
    }
}
