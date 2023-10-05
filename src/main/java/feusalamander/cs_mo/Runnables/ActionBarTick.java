package feusalamander.cs_mo.Runnables;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

import static feusalamander.cs_mo.CS_MO.main;
@SuppressWarnings("deprecation")
public class ActionBarTick extends BukkitRunnable {
    public boolean broke;
    @Override
    public void run() {
        if(broke)return;
        if(main.getQueue().isEmpty())broke = true;
        for(HashMap<Player, Integer> map : main.getQueue()){
            for(Player p : map.keySet()){
                p.sendActionBar("§c"+map.size()+"/10 players");
            }
        }
    }
}
