package feusalamander.cs_mo.Listerners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
@SuppressWarnings("deprecation")
public class GuitClicks implements Listener {
    @EventHandler
    private void onClick(InventoryClickEvent e){
        String title = e.getView().getTitle();
        if(title.equalsIgnoreCase("§6Ranked CS:MO")){
            e.setCancelled(true);
            clickPlay();
        }
    }
    private void clickPlay(){

    }
}
