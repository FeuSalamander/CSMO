package feusalamander.cs_mo.Runnables;

import feusalamander.cs_mo.Managers.Game;
import org.bukkit.scheduler.BukkitRunnable;

import static feusalamander.cs_mo.CS_MO.main;

public class GameTick extends BukkitRunnable {
    private final Game game;
    private boolean rest = true;
    private int time = 15;
    public GameTick(Game game){
        this.game = game;
    }
    @Override
    public void run() {
        if(!main.getGames().contains(game)){cancel();return;}
        if(time == 0)changeRound();
        game.getBar().setTitle("§9"+game.getScore()[0]+" §f"+time()+" §6"+game.getScore()[1]);
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
        }else{
            time = 15;
            game.addRound();
            game.updateBar();
            rest = true;
        }
        if(game.getRound() == 13)game.changeSide();
    }
    public boolean isRest() {
        return rest;
    }
}
