package feusalamander.cs_mo.Listerners;

import feusalamander.cs_mo.Managers.Data;
import feusalamander.cs_mo.Managers.Game;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import static feusalamander.cs_mo.CS_MO.main;

@SuppressWarnings("deprecation")
public class onJoin implements Listener {
    @EventHandler
    private void onJoinn(PlayerJoinEvent e){
        e.getPlayer().setGameMode(GameMode.ADVENTURE);
        e.getPlayer().setHealth(20);
        main.getNone().add(e.getPlayer());
        e.getPlayer().teleport(main.getConf().getSpawn());
        Data.createData(e.getPlayer());
        for(Game game : main.getGames()){
            if(game.getDisconnected().containsKey(e.getPlayer().getUniqueId())){
                game.reconnect(e.getPlayer());
            }
        }
    }
    @EventHandler
    private void onLeave(PlayerQuitEvent e){
        Player p = e.getPlayer();
        p.getInventory().clear();
        Data.saveData();
        main.removeQueue(p);
        if(!main.getNone().contains(p)) for(Game game : main.getGames())if(game.getPlayers().contains(p)){game.remove(p, p.getLocation(), game.getBombDropped().right().equals(p));main.getNone().remove(p);return;}
        main.getNone().remove(p);
    }
    @EventHandler
    private void onHit(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Player p&&e.getEntity() instanceof Player p2){
            if(main.getNone().contains(p)||main.getNone().contains(p2))e.setCancelled(true);
        }
    }
    @EventHandler
    private void onFood(FoodLevelChangeEvent e){
        if(e.getFoodLevel() < 20)e.setFoodLevel(20);
    }
    @EventHandler
    private void onRegen(EntityRegainHealthEvent e){
        e.setCancelled(true);
    }
    @EventHandler
    private void onSwap(PlayerSwapHandItemsEvent e){
        e.setCancelled(true);
    }
}
