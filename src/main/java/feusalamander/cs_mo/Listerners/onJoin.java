package feusalamander.cs_mo.Listerners;

import feusalamander.cs_mo.Managers.Game;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static feusalamander.cs_mo.CS_MO.main;

@SuppressWarnings("deprecation")
public class onJoin implements Listener {
    @EventHandler
    private void onJoinn(PlayerJoinEvent e){
        e.getPlayer().setGameMode(GameMode.ADVENTURE);
        main.getNone().add(e.getPlayer());
        if(!main.getPlayerData().hasJoined(e.getPlayer().getUniqueId()))main.getPlayerData().createUuid(e.getPlayer().getUniqueId());
    }
    @EventHandler
    private void onLeave(PlayerQuitEvent e){
        Player p = e.getPlayer();
        p.getInventory().clear();
        main.getPlayerData().save();
        main.getNone().remove(p);
        main.removeQueue(p);
        for(Game game : main.getGames())if(game.getPlayers().contains(p))game.remove(p);
    }
    @EventHandler
    private void onHit(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Player p&&e.getEntity() instanceof Player p2){
            if(main.getNone().contains(p)||main.getNone().contains(p2))e.setCancelled(true);
        }
    }
}
