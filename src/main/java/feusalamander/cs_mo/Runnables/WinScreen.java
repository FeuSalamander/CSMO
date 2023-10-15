package feusalamander.cs_mo.Runnables;

import feusalamander.cs_mo.Gui.GuiTool;
import feusalamander.cs_mo.Managers.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static feusalamander.cs_mo.CS_MO.main;
@SuppressWarnings("deprecation")
public class WinScreen extends BukkitRunnable {
    private final Game game;
    private int time = 5;
    private final Location loc;
    private final String won;
    private final List<ArmorStand> armorStands = new ArrayList<>();
    public WinScreen(Game game, String won){
        this.game = game;
        this.loc = game.getPlace()[3].clone();
        this.won = won;
        start();
    }

    @Override
    public void run() {
        if(time == 0) end();
        time--;
    }
    private void spawnStands(){
        Vector dir = loc.getDirection().normalize().multiply(5);
        loc.add(dir);
        loc.setYaw(0);
        if(won.equalsIgnoreCase("T")){
            loc.setX(loc.getX()-game.getT().size()+1);
            for(int i = 0; i<game.getT().size(); i++){
                Player p = game.getT().get(i);
                spawnStand(p);
            }
        } else if (won.equalsIgnoreCase("AT")) {
            loc.setX(loc.getX()-game.getCT().size()+1);
            for(int i = 0; i<game.getCT().size(); i++){
                Player p = game.getT().get(i);
                spawnStand(p);
            }
        }else {
            loc.setX(loc.getX()-game.getPlayers().size()+1);
            for(int i = 0; i<game.getPlayers().size(); i++){
                Player p = game.getPlayers().get(i);
                spawnStand(p);
            }
        }
    }
    private void spawnStand(Player p){
        ArmorStand armorStand = (ArmorStand) Objects.requireNonNull(Bukkit.getWorld("world")).spawnEntity(loc, EntityType.ARMOR_STAND);
        armorStands.add(armorStand);
        armorStand.setInvulnerable(true);
        armorStand.setArms(true);
        armorStand.setCustomName("§a"+p.getName());
        armorStand.setCustomNameVisible(true);
        armorStand.setItem(EquipmentSlot.HEAD, GuiTool.getSkull(" ", p));
        loc.setX(loc.getX()+2);
    }
    private void start(){
        for(Player p : game.getPlayers()){
            p.teleport(game.getPlace()[3]);
        }
        spawnStands();
    }
    private void end(){
        for(Player p : game.getPlayers()){
            p.teleport(main.getConf().getSpawn());
        }
        game.getMap().first().setPair(true, game.getMap().right());
        main.getGames().remove(game);
        HandlerList.unregisterAll(game);

        for(ArmorStand armorStand : armorStands)armorStand.remove();

        cancel();
    }
}
