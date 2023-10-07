package feusalamander.cs_mo.Runnables;

import feusalamander.cs_mo.Gui.GuiTool;
import feusalamander.cs_mo.Managers.Game;
import feusalamander.cs_mo.Managers.MiniMapRenderer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.map.MapCursor;
import org.bukkit.scheduler.BukkitRunnable;


import java.util.Arrays;

import static feusalamander.cs_mo.CS_MO.main;
@SuppressWarnings("deprecation")
public class GameTick extends BukkitRunnable {
    private final Game game;
    private boolean rest = true;
    private int time = 15;
    private String color = "§f";
    public GameTick(Game game){
        this.game = game;
    }
    @Override
    public void run() {
        if(!main.getGames().contains(game)){cancel();return;}
        if(time == 0)changeRound();
        game.getBar().setTitle("§9"+game.getScore()[0]+" "+color+time()+" §6"+game.getScore()[1]);
        game.getBar2().setTitle("§9"+game.getScore()[0]+" "+color+time()+" §6"+game.getScore()[1]);
        updateCursors();
        checkBomb();
        time--;
    }
    private String time(){
        if(time>=60)return "1:"+(time-60);
        return "0:"+time;
    }
    private void changeRound(){
        if(rest){
            time = 115;//115
            rest = false;
            game.giveShop(false);
        }else{
            color = "§f";
            for(Item item : game.getItems()){
                item.remove();
                game.bombDropped = null;
                game.getRenderers().get(1).changeBombT(null);
                game.getRenderers().get(0).changeBombAT(null);
            }
            score();
            time = 15;//15
            if(game.getRound() == 12)game.changeSide();
            game.addRound();
            game.updateBar();
            rest = true;
            game.chooseSpawns();
            game.giveShop(true);
            for(Player p :game.getPlayers())p.setHealth(20);
            bomb();
        }
        if(game.getRound() > 24)end();
    }
    public boolean isRest() {
        return rest;
    }
    private void score(){
        if(!game.getBombPlanted().first()){
            game.addScore(0);
            for(Player p : game.getPlayers())p.sendMessage("§9The CTs wins");
        }else {
            game.addScore(1);
            game.removeBomb();
            for(Player p : game.getPlayers())p.sendMessage("§6The Ts wins");
        }
    }
    public void bomb(){
        Player p = game.getT().get(main.random.nextInt(game.getT().size()));
        p.getInventory().setItem(7, GuiTool.bomb);
        game.getRenderers().get(1).changeType(MapCursor.Type.MANSION, p);
    }
    public void plant(){
        time = 40;
        color = "§4";
    }
    public void defuse(){
        time = 1;
        color = "§f";
    }
    private void end(){
        String won;
        if(game.getScore()[0]<game.getScore()[1]){
            won = "T";
        }else{
            won = "CT";
        }
        if(game.getScore()[0]==game.getScore()[1]){
            won = "none";
        }
        for(Player p : game.getPlayers()){
            p.teleport(main.getConf().getSpawn());
            setWins(p, won);
            p.getInventory().clear();
            p.setScoreboard(main.getScoreboard());
            main.getNone().add(p);
            game.getBar().removePlayer(p);
            game.getBar2().removePlayer(p);
        }
        main.getPlayerData().save();
        game.getMap().first().setPair(true, game.getMap().right());
        main.getGames().remove(game);
        HandlerList.unregisterAll(game);
        cancel();
    }
    private void setWins(Player p, String won){
        if(won.equalsIgnoreCase("CT")){
            if(game.getCT().contains(p)){
                main.getPlayerData().addWins(p.getUniqueId(), 1);
                main.getPlayerData().addElo(p.getUniqueId(), newElo(p, true));
            }else {
                main.getPlayerData().addLooses(p.getUniqueId(), 1);
                main.getPlayerData().addElo(p.getUniqueId(), newElo(p, false));
            }
            p.sendMessage("§5The §9CTs §5won the game");
            return;
        }
        if(won.equalsIgnoreCase("T")){
            if(game.getT().contains(p)){
                main.getPlayerData().addWins(p.getUniqueId(), 1);
                main.getPlayerData().addElo(p.getUniqueId(), newElo(p, true));
            }else {
                main.getPlayerData().addLooses(p.getUniqueId(), 1);
                main.getPlayerData().addElo(p.getUniqueId(), newElo(p, false));
            }
            p.sendMessage("§5The §9Ts §5won the game");
            return;
        }
        p.sendMessage("§5The game resulted in a tie");
    }
    private int newElo(Player p, boolean won){
        int actualOutcome = won ? 1 : 0;
        int pElo = main.getPlayerData().getElo(p.getUniqueId());
        int gameElo = game.getGameElo();
        int[] rankArray = game.getStats().get(p);
        float rank = (float) ((rankArray[0]+1)/(rankArray[1]+1))*5;
        int finalElo = (int) ((actualOutcome - 1.0 / (1.0 + Math.pow(10, (gameElo - pElo) / 400.0))) + rank);
        if(pElo+finalElo<0)return -pElo;
        return finalElo;
    }
    private void updateCursors(){
        for (MiniMapRenderer miniMapRenderer : game.getRenderers()){
            miniMapRenderer.updateCursors();
        }
    }
    private void checkBomb(){
        if(game.getBombDropped() == null)return;
        for(Entity entity: game.bombDropped.getNearbyEntities(15, 5, 15)){
            if(entity instanceof Player p&&game.getCT().contains(p)){
                game.getRenderers().get(0).changeBombAT(game.getBombDropped().getLocation());
                return;
            }
        }
        game.getRenderers().get(0).changeBombAT(null);
    }
}
