package feusalamander.cs_mo.Runnables;

import feusalamander.cs_mo.Gui.GuiTool;
import feusalamander.cs_mo.Managers.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

@SuppressWarnings("deprecation")
public class Defusing extends BukkitRunnable {
    private final Player p;
    private final Game game;
    private int time = 0;
    private int kit = 10;
    private final Block loc;

    public Defusing(Player p, Game game){
        this.p = p;
        this.game = game;
        if(Objects.requireNonNull(p.getInventory().getItem(7)).getType().equals(Material.SHEARS))kit = 5;
        loc = p.getLocation().getBlock();
        p.sendActionBar("§7Defusing...");
        game.defusing = true;
    }
    @Override
    public void run() {
        if(!p.getLocation().getBlock().equals(loc)){cancel();p.setExp(0);game.defusing = false;;return;}
        if(time == kit){
            defuse();
            cancel();
            return;
        }
        p.setExp((float) time/kit);
        time++;
    }
    private void defuse(){
        game.defusing = false;
        game.removeBomb();
        p.setExp(0);
        game.setBombPlanted(false, null);
        game.getTick().defuse();
        for(Player p : game.getPlayers())p.sendMessage("§aThe bomb has been defused");
    }
}
