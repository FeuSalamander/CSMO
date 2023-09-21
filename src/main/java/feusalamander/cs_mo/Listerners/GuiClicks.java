package feusalamander.cs_mo.Listerners;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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
        int[] gameElo = whatElo(p);
        int playerElo = main.getPlayerData().getElo(p.getUniqueId());
        int finalElo = playerElo;
        if(gameElo.length == 1){
            main.getQueue().add(Pair.of(
                    playerElo,
                    List.of(p)));
        }else{
            List<Player> list = main.getQueue().get(gameElo[1]).right();
            list.add(p);
            int elo = gameElo[0];
            elo = (elo+playerElo)/2;
            main.getQueue().remove(main.getQueue().get(gameElo[1]));
            main.getQueue().add(Pair.of(elo, list));
            finalElo = elo;
        }
        p.sendMessage("§dYour are queued to Ranked CS:MO with "+finalElo+" elo");
        main.getActionBarTick().broke = false;
    }
    private int[] whatElo(Player p){
        int elo = main.getPlayerData().getElo(p.getUniqueId());
        for(Pair<Integer, List<Player>> pair: main.getQueue()){
            if((pair.first()-main.getConf().getMatchMaking())<=elo&&
                    elo>=(pair.first()+main.getConf().getMatchMaking())){
                return new int[]{pair.first(), main.getQueue().indexOf(pair)};
            }
        }
        return new int[]{-1};
    }
}
