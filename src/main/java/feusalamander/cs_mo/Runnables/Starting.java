package feusalamander.cs_mo.Runnables;

import feusalamander.cs_mo.Managers.Game;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

import static feusalamander.cs_mo.CS_MO.main;
@SuppressWarnings("deprecation")
public class Starting extends BukkitRunnable {
    private final List<Player> players;
    private final int gameElo;
    private int timer = 5;
    private final String[] colors;
    public Starting(List<Player> players, int elo){
        this.players = players;
        this.gameElo = elo;
        colors = new String[]{"§4", "§c", "§6", "§2", "§a"};
    }
    @Override
    public void run() {
        if(players.size() != main.getConf().getMinPlayer())unload();
        if(timer == 0){start();unload();return;}
        for(Player p  : players){
            p.sendTitle(colors[timer-1]+"§l"+timer+"s", "");
            p.setLevel(timer);
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
        main.getQueue().removeIf(map -> map.containsKey(players.get(0)));
        for(Player p : players){
            main.getNone().remove(p);
            p.getInventory().clear();
            p.setLevel(0);
        }
        new Game(players, gameElo);
    }
}
