package feusalamander.cs_mo.Gui;

import feusalamander.cs_mo.Enum.WeaponTypes;
import feusalamander.cs_mo.Enum.Weapons;
import feusalamander.cs_mo.Managers.Game;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Objects;

import static feusalamander.cs_mo.CS_MO.main;

@SuppressWarnings("deprecation")
public final class AtBuyMenu {
    public static final Inventory menu = Bukkit.createInventory(null, 54, "§9AT Shop");
    public static void buildGUi(){
        for(int i = 0; i<54; i++){menu.setItem(i, GuiTool.pane);}
        for(Weapons gun : Weapons.values()){
            if(gun.type.equals(WeaponTypes.AT)||gun.type.equals(WeaponTypes.ALL)){
                menu.setItem(gun.slot, GuiTool.getItem(Material.FEATHER, "§a"+gun.name+" §6"+gun.price+"$", List.of(gun.name())));
            }
        }
    }
    public static void buy(Player p, String name){
        if(name.equalsIgnoreCase("kit")&&buy2(p, name))p.getInventory().setItem(7, GuiTool.kit);
        //p.getInventory().setItem(1, WeaponMechanics.getWeaponHandler().getInfoHandler().generateWeapon(Weapons.valueOf(name).id, 1));
    }
    public static boolean buy2(Player p, String name){
        Weapons weapon = Objects.requireNonNull(buy3(name));
        int price = weapon.price;
        for(Game game : main.getGames()){
            if(game.getPlayers().contains(p)){
                int money = game.getMoneyAndStats().get(p.getName()).first();
                if(money>=price){
                    game.removeMoney(p, price);
                    p.sendMessage("§aYou bought the "+weapon.name+" for §6"+price+"$");
                    return true;
                }
                p.sendMessage("§cYou don't have enough money to buy this");
                return false;
            }
        }
        return false;
    }
    public static Weapons buy3(String name){
        for(Weapons gun : Weapons.values()){
            if(gun.id.equalsIgnoreCase(name))return gun;
        }
        return Weapons.AK47;
    }
}
