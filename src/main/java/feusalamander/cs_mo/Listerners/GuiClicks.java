package feusalamander.cs_mo.Listerners;

import feusalamander.cs_mo.Gui.AtBuyMenu;
import feusalamander.cs_mo.Gui.GuiTool;
import feusalamander.cs_mo.Gui.TBuyMenu;
import feusalamander.cs_mo.Managers.Data;
import feusalamander.cs_mo.Managers.Game;
import feusalamander.cs_mo.Runnables.Starting;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static feusalamander.cs_mo.CS_MO.main;

@SuppressWarnings("deprecation")
public class GuiClicks implements Listener {
    private final ItemStack barrier;
    public GuiClicks(){
        barrier = GuiTool.getItem(Material.BARRIER, "§4Leave", List.of("§7Click to leave the queue"));
    }
    @EventHandler
    private void onClick(InventoryClickEvent e){
        String title = e.getView().getTitle();
        if(e.getClickedInventory() != null&&e.getClickedInventory().getType().equals(InventoryType.PLAYER)){
            if(e.getWhoClicked().getGameMode().equals(GameMode.CREATIVE))return;
            if(e.getCurrentItem() == null){e.setCancelled(true);return;}
            if(e.getCurrentItem().getType().equals(Material.BLACK_STAINED_GLASS_PANE)){e.setCancelled(true);return;}
            if(e.getSlot() == 7&&e.getCurrentItem().getType().equals(Material.SHEARS)){e.setCancelled(true);return;}
            if(e.getSlot() == 1||e.getSlot() == 2||e.getSlot() == 7)return;
            e.setCancelled(true);
            return;
        }
        if(title.equalsIgnoreCase("§6Ranked CS:MO")){
            e.setCancelled(true);
            ItemStack item = e.getCurrentItem();
            if(item != null&&item.hasItemMeta()){
                String name = e.getCurrentItem().getItemMeta().getDisplayName();
                if(name.equalsIgnoreCase("§4Play Ranked CS:MO")){clickPlay((Player)e.getWhoClicked());return;}
            }
        }
        if(title.equalsIgnoreCase("§9AT Shop")||title.equalsIgnoreCase("§6T Shop")){
            e.setCancelled(true);
            ItemStack item = e.getCurrentItem();
            if(item != null&&item.hasItemMeta()&&item.getItemMeta().hasLore()){
                String name = Objects.requireNonNull(item.getItemMeta().getLore()).get(0);
                if(name.equalsIgnoreCase(" "))return;
                AtBuyMenu.buy((Player) e.getWhoClicked(), name);
            }
        }
    }
    private void clickPlay(Player p){
        if(p.getInventory().getItem(8) != null){p.sendMessage("§cYour are already in a queue");return;}
        if(!main.getNone().contains(p)){p.sendMessage("§cYou can't do that");return;}
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            int playerElo = Data.getElo(p.getUniqueId());
            int[] gameElo = whatElo(playerElo);
            int finalElo = playerElo;
            if(gameElo.length == 1){
                HashMap<Player, Integer> map = new HashMap<>();
                map.put(p, finalElo);
                main.getQueue().add(map);
            }else{
                HashMap<Player, Integer> map = main.getQueue().get(gameElo[1]);
                map.put(p, playerElo);
                finalElo = gameElo[0];
                if(map.size() == main.getConf().getMinPlayer())starting(new ArrayList<>(map.keySet()), finalElo);
            }
            p.sendMessage("§dYour are queued to Ranked CS:MO with "+finalElo+" elo");
            main.getActionBarTick().broke = false;
            p.getInventory().setItem(8, barrier);
        });
    }
    private int[] whatElo(int elo){
        for(HashMap<Player, Integer> map : main.getQueue()){
            int moyenne = (int) map.values().stream().mapToInt(d -> d).average().orElse(0);
            if((moyenne-main.getConf().getMatchMaking())<=elo&&
                    elo<=(moyenne+main.getConf().getMatchMaking())&&
            map.size() < 10){
                return new int[]{moyenne, main.getQueue().indexOf(map)};
            }
        }
        return new int[]{-1};
    }
    private void starting(List<Player> players, int elo){
        Starting timer = new Starting(players, elo);
        timer.runTaskTimer(main, 20, 20);
        main.getStarting().add(timer);
    }
    @EventHandler
    private void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        ItemStack item = e.getItem();
        if(item == null||!item.hasItemMeta())return;
        String name = item.getItemMeta().getDisplayName();
        if(name.equalsIgnoreCase("§4Leave")){
            p.getInventory().clear();
            main.removeQueue(p);
        }
        if(name.equalsIgnoreCase("§aShop")){
            for(Game game : main.getGames()){
                if(game.getCT().contains(p)){p.openInventory(AtBuyMenu.menu);return;}
                if(game.getT().contains(p)){p.openInventory(TBuyMenu.menu);return;}
            }
        }
        if(name.equalsIgnoreCase("§4Bomb")) {
            if (!p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.BEDROCK)) return;
            for (Game game : main.getGames()) {
                if (game.getPlayers().contains(p)) {
                    game.plant(p);
                    return;
                }
            }
        }
    }
    @EventHandler
    private void onPlace(BlockPlaceEvent e){
        if(e.getPlayer().isOp())return;
        e.setCancelled(true);
    }
    @EventHandler
    private void onBreak(BlockBreakEvent e){
        if(e.getPlayer().isOp())return;
        e.setCancelled(true);
    }
}
