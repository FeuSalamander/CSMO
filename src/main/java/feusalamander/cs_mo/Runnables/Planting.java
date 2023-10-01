package feusalamander.cs_mo.Runnables;

import feusalamander.cs_mo.Gui.GuiTool;
import feusalamander.cs_mo.Listerners.GuiClicks;
import feusalamander.cs_mo.Managers.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
@SuppressWarnings("deprecation")
public class Planting extends BukkitRunnable {
    private final Player p;
    private final Game game;
    private int time = 17;

    public Planting(Player p, Game game){
        this.p = p;
        this.game = game;
        p.setSneaking(true);
    }
    @Override
    public void run() {
        if(!p.getItemInHand().getType().equals(Material.NETHER_STAR))cancel();
        if(time == 0){
            plant();
            cancel();
            return;
        }
        p.setExp((float) 1 /time);
        time--;
    }
    private void plant(){
        p.getInventory().setItemInMainHand(GuiTool.pane);
        Location loc = p.getLocation();
        loc.setY(loc.getY()-1.5);
        ArmorStand armorStand = (ArmorStand) Objects.requireNonNull(Bukkit.getWorld("world")).spawnEntity(loc, EntityType.ARMOR_STAND);
        armorStand.setInvisible(true);
        armorStand.setInvulnerable(true);
        armorStand.setGravity(false);
        armorStand.setItem(EquipmentSlot.HEAD, GuiTool.getSkull("bomb", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGY4NzRiYjViNmVmN2IzNDM0YTU4YjMxNDk2MjUyMTg2Mjk1YzM3OWJlOTk2OTM0ZDEwM2QxNWVhMjI1Y2JhMyJ9fX0"));
        for(Player p : game.getPlayers())p.sendMessage("§4The bomb has been planted");
        game.setBombPlanted(true, armorStand.getLocation());
        Objects.requireNonNull(game.getSb().getTeam("bombV")).setSuffix("Planted");
        game.getTick().plant();
    }
}
