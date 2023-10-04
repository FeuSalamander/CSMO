package feusalamander.cs_mo.Runnables;

import feusalamander.cs_mo.Gui.GuiTool;
import feusalamander.cs_mo.Managers.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

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
        time--;
    }
    private String time(){
        if(time>=60)return "1:"+(time-60);
        return "0:"+time;
    }
    private void changeRound(){
        if(rest){
            time = 115;
            rest = false;
            game.giveShop(false);
        }else{
            if(game.getRound() == 13)game.changeSide();
            color = "§f";
            for(Item item : game.getItems())item.remove();
            score();
            time = 15;
            game.addRound();
            game.updateBar();
            rest = true;
            game.chooseSpawns();
            game.giveShop(true);
            for(Player p :game.getPlayers())p.setHealth(20);
            bomb();
        }
        if(game.getRound() > 24)Bukkit.broadcastMessage("game finished");
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
    }
    public void plant(){
        time = 40;
        color = "§4";
    }
    public void defuse(){
        time = 1;
        color = "§f";
    }
}
