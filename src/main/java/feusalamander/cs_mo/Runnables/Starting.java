package feusalamander.cs_mo.Runnables;

import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static feusalamander.cs_mo.CS_MO.main;
@SuppressWarnings("deprecation")
public class Starting extends BukkitRunnable {
    private final List<Player> players;
    private int timer = 5;
    private String[] colors;
    public Starting(List<Player> players){
        this.players = players;
        colors = new String[]{"§4", "§c", "§6", "§2", "§a"};
    }
    @Override
    public void run() {
        if(players.size() != 10)unload();
        if(timer == 0){unload();start();}
        for(Player p  : players){
            p.sendTitle(colors[timer]+timer+"s", "");
            p.setLevel(timer*2);
        }
        timer--;
    }
    public List<Player> getPlayers() {
        return players;
    }
    private void unload(){
        main.getStarting().remove(this);
        cancel();
    }
    private void start(){
        main.getStarting().remove(this);
        for(Pair<Integer, List<Player>> pair : main.getQueue()){
            if(pair.right().contains(players.get(1))){
                main.getQueue().remove(pair);
            }
        }
        for(Player p : players){
            main.getNone().remove(p);
        }

    }
}
