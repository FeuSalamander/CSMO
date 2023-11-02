package feusalamander.cs_mo.Gui;

import feusalamander.cs_mo.Enum.WeaponTypes;
import feusalamander.cs_mo.Enum.Weapons;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.List;

@SuppressWarnings("deprecation")
public final class TBuyMenu {
    public static final Inventory menu = Bukkit.createInventory(null, 54, "§6T Shop");
    public static void buildGUi(){
        for(int i = 0; i<54; i++){menu.setItem(i, GuiTool.pane);}
        for(Weapons gun : Weapons.values()){
            if(gun.type.equals(WeaponTypes.T)||gun.type.equals(WeaponTypes.ALL)){
                menu.setItem(gun.slot, GuiTool.getItem(Material.FEATHER, "§a"+gun.name+" §6"+gun.price+"$", List.of(gun.id)));
            }
        }
    }
}
