package feusalamander.cs_mo.Listerners;

import feusalamander.cs_mo.Runnables.Starting;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static feusalamander.cs_mo.CS_MO.main;

@SuppressWarnings("deprecation")
public class GuiClicks implements Listener {
    @EventHandler
    private void onClick(InventoryClickEvent e){
        String title = e.getView().getTitle();
        if(title.equalsIgnoreCase("§6Ranked CS:MO")){
            e.setCancelled(true);
            ItemStack item = e.getCurrentItem();
            if(item != null&&item.hasItemMeta()){
                String name = e.getCurrentItem().getItemMeta().getDisplayName();
                if(name.equalsIgnoreCase("§4Play Ranked CS:MO"))clickPlay((Player)e.getWhoClicked());
            }
        }
    }
    private void clickPlay(Player p){
        for(Pair<Integer, List<Player>> pair : main.getQueue()){if(pair.right().contains(p)){p.sendMessage("§cYour are already in a queue");return;}}
        int[] gameElo = whatElo(p);
        int playerElo = main.getPlayerData().getElo(p.getUniqueId());
        int finalElo = playerElo;
        if(gameElo.length == 1){
            main.getQueue().add(Pair.of(
                    playerElo,
                    List.of(p)));
        }else{
            List<Player> list = new ArrayList<>(main.getQueue().get(gameElo[1]).right());
            list.add(p);
            int elo = gameElo[0];
            elo = (elo+playerElo)/2;
            main.getQueue().remove(main.getQueue().get(gameElo[1]));
            main.getQueue().add(Pair.of(elo, list));
            finalElo = elo;
            if(list.size() == main.getConf().getMinPlayer())starting(list);
        }
        p.sendMessage("§dYour are queued to Ranked CS:MO with "+finalElo+" elo");
        main.getActionBarTick().broke = false;
    }
    private int[] whatElo(Player p){
        int elo = main.getPlayerData().getElo(p.getUniqueId());
        for(Pair<Integer, List<Player>> pair: main.getQueue()){
            if((pair.first()-main.getConf().getMatchMaking())<=elo&&
                    elo<=(pair.first()+main.getConf().getMatchMaking())&&
            pair.right().size() < 10){
                return new int[]{pair.first(), main.getQueue().indexOf(pair)};
            }
        }
        return new int[]{-1};
    }
    private void starting(List<Player> players){
        Starting timer = new Starting(players);
        timer.runTaskTimer(main, 20, 40);
        main.getStarting().add(timer);
    }
}
