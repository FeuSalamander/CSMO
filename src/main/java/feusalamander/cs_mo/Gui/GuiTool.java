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

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("deprecation")
public final class GuiTool {
    public static final ItemStack pane = getItem(Material.BLACK_STAINED_GLASS_PANE, " ");
    public static final ItemStack bomb = getItem(Material.NETHER_STAR, "§4Bomb");
    public static final ItemStack kit = getItem(Material.SHEARS, "§9Defuse Kit");
    public static final ItemStack shop = getItem(Material.CHEST, "§aShop");
    public static final ItemStack map = getMap();
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
    public static ItemStack getMap(){
        ItemStack item = getItem(Material.FILLED_MAP, "§aMap");
        MapMeta mapMeta = (MapMeta) item.getItemMeta();
        MapView mapView = Bukkit.createMap(Objects.requireNonNull(Bukkit.getWorld("world")));
        mapView.addRenderer(new MapRenderer() {
            @Override
            public void render(@NotNull MapView mapView, @NotNull MapCanvas mapCanvas, @NotNull Player player) {
                MapCursorCollection mapCursorCollection = new MapCursorCollection();
                mapCursorCollection.addCursor(new MapCursor((byte) 70, (byte) 70, (byte) 5, MapCursor.Type.RED_POINTER, true));
                mapCanvas.setCursors(mapCursorCollection);
            }
        });

        mapMeta.setMapView(mapView);
        item.setItemMeta(mapMeta);
        return item;
    }
}
