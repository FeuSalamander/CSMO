package feusalamander.cs_mo.Runnables;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static feusalamander.cs_mo.CS_MO.main;
@SuppressWarnings("deprecation")
public class ActionBarTick extends BukkitRunnable {
    public boolean broke;
    @Override
    public void run() {
        if(broke)return;
        if(main.getQueue().isEmpty())broke = true;
        for(Pair<Integer, List<Player>> pair : main.getQueue()){
            for(Player p : pair.right()){
                p.sendActionBar("§c"+pair.right().size()+"/10 players");
            }
        }
    }
}
