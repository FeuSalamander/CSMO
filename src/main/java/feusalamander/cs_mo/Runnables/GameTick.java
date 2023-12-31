package feusalamander.cs_mo.Runnables;

import feusalamander.cs_mo.Gui.GuiTool;
import feusalamander.cs_mo.Managers.Data;
import feusalamander.cs_mo.Managers.Game;
import feusalamander.cs_mo.Managers.MiniMapRenderer;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.map.MapCursor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.UUID;

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
        if(isRest())
            for(Player p : game.getPlayers())
                p.sendActionBar("§a"+game.getMoneyAndStats().get(p.getName()).left()+"$");
        game.getBar().setTitle("§9"+game.getScore()[0]+" "+color+time()+" §6"+game.getScore()[1]);
        game.getBar2().setTitle("§9"+game.getScore()[0]+" "+color+time()+" §6"+game.getScore()[1]);
        updateCursors();
        checkBomb();
        time--;
    }
    private String time(){
        if(time>=60)return "1:"+(time-60);
        if(time<0)return "0:00";
        return "0:"+time;
    }
    private void changeRound(){
        if(rest){
            time = 115;//115
            rest = false;
            game.giveShop(false);
        }else{
            bombExplode();
            color = "§f";
            score();
            setRest();
        }
        if(game.getRound() > 24)end();
    }
    public boolean isRest() {
        return rest;
    }
    private void score(){
        boolean empty = true;
        for(Player p : game.getCT()){
            if(!game.getSpecs().containsKey(p.getName())){
                empty = false;
                break;
            }
        }
        if(empty){
            game.addScore(1);
            game.removeBomb();
            for(Player p : game.getPlayers())p.sendTitle("§6The Ts wins", "");
            updateComp(true);
            for(Player p : game.getT()){
                game.getMoneyAndStats().replace(p.getName(), Pair.of(game.getMoneyAndStats().get(p.getName()).left()+3250, game.getMoneyAndStats().get(p.getName()).right()));
                p.sendMessage("§a6+3250$ §afor eliminating the enemy team");
            }
            return;
        }
        if(!game.getBombPlanted().first()){
            boolean empty2 = true;
            for(Player p : game.getT()){
                if(!game.getSpecs().containsKey(p.getName())){
                    empty2 = false;
                    break;
                }
            }
            if(empty2){
                game.addScore(1);
                game.removeBomb();
                for(Player p : game.getPlayers())p.sendTitle("§9The CTs wins", "");
                updateComp(false);
                for(Player p : game.getCT()){
                    game.getMoneyAndStats().replace(p.getName(), Pair.of(game.getMoneyAndStats().get(p.getName()).left()+3250, game.getMoneyAndStats().get(p.getName()).right()));
                    p.sendMessage("§6+3250$ §afor eliminating the enemy team");
                }
                return;
            }
            game.addScore(0);
            for(Player p : game.getPlayers())p.sendTitle("§9The CTs wins", "");
            updateComp(false);
            if(game.getBombDropped().left() != null||game.getBombDropped().right() != null){
                for(Player p : game.getCT()){
                    game.getMoneyAndStats().replace(p.getName(), Pair.of(game.getMoneyAndStats().get(p.getName()).left()+3250, game.getMoneyAndStats().get(p.getName()).right()));
                    p.sendMessage("§6+3250$ §afor not letting the Ts plant");
                }
            }else{
                for(Player p : game.getCT()){
                    game.getMoneyAndStats().replace(p.getName(), Pair.of(game.getMoneyAndStats().get(p.getName()).left()+3500, game.getMoneyAndStats().get(p.getName()).right()));
                    p.sendMessage("§6+3500$ §afor defusing the bomb");
                }
                for(Player p : game.getT()){
                    game.getMoneyAndStats().replace(p.getName(), Pair.of(game.getMoneyAndStats().get(p.getName()).left()+800, game.getMoneyAndStats().get(p.getName()).right()));
                    p.sendMessage("§6+800$ §afor planting the bomb");
                }
            }
        }else {
            game.addScore(1);
            game.removeBomb();
            for(Player p : game.getPlayers())p.sendTitle("§6The Ts wins", "");
            updateComp(true);
            for(Player p : game.getT()){
                game.getMoneyAndStats().replace(p.getName(), Pair.of(game.getMoneyAndStats().get(p.getName()).left()+3500, game.getMoneyAndStats().get(p.getName()).right()));
                p.sendMessage("§6+3500$ §afor completing the objective");
            }
        }

    }
    private void updateComp(boolean ct){
        if(ct){
            int money = main.getLooseMoney()[game.getCompensation()[0]];
            for(Player p : game.getCT()){
                game.getMoneyAndStats().replace(p.getName(), Pair.of(game.getMoneyAndStats().get(p.getName()).left()+money, game.getMoneyAndStats().get(p.getName()).right()));
                p.sendMessage("§6+"+money+"$ §afor loosing");
            }
            if(game.getCompensation()[0] < 4) game.getCompensation()[0]++;
            if(game.getCompensation()[1] > 0) game.getCompensation()[1]--;
        }else{
            int money = main.getLooseMoney()[game.getCompensation()[1]];
            for(Player p : game.getT()){
                game.getMoneyAndStats().replace(p.getName(), Pair.of(game.getMoneyAndStats().get(p.getName()).left()+money, game.getMoneyAndStats().get(p.getName()).right()));
                p.sendMessage("§6+"+money+"$ §afor loosing");
            }
            if(game.getCompensation()[1] < 4) game.getCompensation()[1]++;
            if(game.getCompensation()[0] > 0) game.getCompensation()[0]--;
        }
        String base = Objects.requireNonNull(game.getSb().getTeam("ctV")).
                getSuffix().substring(0, Objects.requireNonNull(game.getSb().getTeam("ctV")).getSuffix().length()-game.getCompensation()[0]);
        String base2 = Objects.requireNonNull(game.getSb().getTeam("tV")).
                getSuffix().substring(0, Objects.requireNonNull(game.getSb().getTeam("tV")).getSuffix().length()-game.getCompensation()[1]);
        String comp = "■".repeat(Math.max(0, game.getCompensation()[0]));
        Objects.requireNonNull(game.getSb().getTeam("ctV")).setSuffix(base+ comp);
        String comp2 = "■".repeat(Math.max(0, game.getCompensation()[1]));
        Objects.requireNonNull(game.getSb().getTeam("tV")).setSuffix(base2+ comp2);
    }
    public void bomb(){
        Player p = null;
        int crash = 0;
        while(p == null||!p.isOnline()||p.isInvisible()){
            if(crash >4)break;
            p = game.getT().get(main.random.nextInt(game.getT().size()));
            crash++;
        }
        assert p != null;
        p.getInventory().setItem(7, GuiTool.bomb);
        game.setBombDropped(Pair.of(null, p));
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
    public void end(){
        String won;
        if(game.getScore()[0]<game.getScore()[1]){
            won = "T";
        }else{
            won = "CT";
        }
        if(game.getScore()[0]==game.getScore()[1]){
            won = "none";
        }
        if(game.getCT().isEmpty())won = "T";
        if(game.getT().isEmpty())won = "CT";
        for(Player p : game.getPlayers()){
            setWins(p, won);
            p.getInventory().clear();
            p.setScoreboard(main.getScoreboard());
            game.getBar().removePlayer(p);
            game.getBar2().removePlayer(p);
            main.getNone().add(p);
            Data.addKills(p.getUniqueId(), game.getMoneyAndStats().get(p.getName()).right()[0]);
            Data.addDeaths(p.getUniqueId(), game.getMoneyAndStats().get(p.getName()).right()[1]);
        }
        for(UUID p : game.getDisconnected().keySet())Bukkit.getScheduler().runTaskAsynchronously(main, () -> Data.addElo(p, -10));
        game.getDisconnected().clear();
        Data.saveData();
        new WinScreen(game, won).runTaskTimer(main, 0, 100);
        cancel();
    }
    private void setWins(Player p, String won){
        if(won.equalsIgnoreCase("CT")){
            if(game.getCT().contains(p)){
                main.getPlayerData().addWins(p.getUniqueId(), 1);
                Bukkit.getScheduler().runTaskAsynchronously(main, () -> Data.addElo(p.getUniqueId(), newElo(p, true)));
                Bukkit.getScheduler().runTaskAsynchronously(main, () -> Data.addWins(p.getUniqueId(), 1));
            }else {
                main.getPlayerData().addLooses(p.getUniqueId(), 1);
                Bukkit.getScheduler().runTaskAsynchronously(main, () -> Data.addElo(p.getUniqueId(), newElo(p, false)));
                Bukkit.getScheduler().runTaskAsynchronously(main, () -> Data.addLooses(p.getUniqueId(), 1));
            }
            p.sendMessage("§5The §9CTs §5won the game");
            return;
        }
        if(won.equalsIgnoreCase("T")){
            if(game.getT().contains(p)){
                main.getPlayerData().addWins(p.getUniqueId(), 1);
                Bukkit.getScheduler().runTaskAsynchronously(main, () -> Data.addElo(p.getUniqueId(), newElo(p, true)));
                Bukkit.getScheduler().runTaskAsynchronously(main, () -> Data.addWins(p.getUniqueId(), 1));
            }else {
                main.getPlayerData().addLooses(p.getUniqueId(), 1);
                Bukkit.getScheduler().runTaskAsynchronously(main, () -> Data.addElo(p.getUniqueId(), newElo(p, false)));
                Bukkit.getScheduler().runTaskAsynchronously(main, () -> Data.addLooses(p.getUniqueId(), 1));
            }
            p.sendMessage("§5The §9Ts §5won the game");
            return;
        }
        p.sendMessage("§5The game resulted in a tie");
    }
    private int newElo(Player p, boolean won){
        int actualOutcome = won ? 1 : 0;
        int pElo = Data.getElo(p.getUniqueId());
        int gameElo = game.getGameElo();
        int[] rankArray = game.getMoneyAndStats().get(p.getName()).right();
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
        if(game.getBombDropped().left() == null)return;
        for(Player p : game.bombDropped.left().getLocation().getNearbyPlayers(15, 5, 15)){
            if(game.getCT().contains(p)){
                game.getRenderers().get(0).changeBombAT(game.getBombDropped().left().getLocation());
                return;
            }
        }
        game.getRenderers().get(0).changeBombAT(null);
    }
    private void setRest(){
        Bukkit.getScheduler().runTaskLater(main, () -> {
            for(Item item : game.getItems())item.remove();
            if(game.bombDropped.right() != null) game.bombDropped.right().getInventory().setItem(7, GuiTool.pane);
            game.bombDropped = null;
            game.getRenderers().get(1).changeBombT(null);
            game.getRenderers().get(0).changeBombAT(null);
            time = 15;//15
            if(game.getRound() == 12)game.changeSide();
            game.addRound();
            game.updateBar();
            rest = true;
            rejoin();
            game.chooseSpawns();
            game.giveShop(true);
            for(Player p :game.getPlayers())p.setHealth(20);
            bomb();
            Objects.requireNonNull(game.getSb().getTeam("ctV")).setSuffix(game.getCT().size()+ Objects.requireNonNull(game.getSb().getTeam("ctV")).getSuffix().substring(1));
            Objects.requireNonNull(game.getSb().getTeam("tV")).setSuffix(game.getT().size()+ Objects.requireNonNull(game.getSb().getTeam("tV")).getSuffix().substring(1));
        }, 140);//140
    }
    private void rejoin(){
        for(String p2 : game.getSpecs().keySet()){
            Player p = Bukkit.getPlayer(p2);
            if(game.getCT().contains(p)){
                assert p != null;
                p.getInventory().setItem(EquipmentSlot.OFF_HAND, game.miniMapCT);
            }else {
                assert p != null;
                p.getInventory().setItem(EquipmentSlot.OFF_HAND, game.miniMapT);
            }
            main.removeFromSpec(p2, game);
            game.Inventory(p);
        }
        game.getSpecs().clear();
    }
    private void bombExplode(){
        if(!game.getBombPlanted().left())return;
        Location loc = game.getBombPlanted().right();
        Objects.requireNonNull(Bukkit.getWorld("world")).spawnParticle(Particle.EXPLOSION_HUGE, loc, 5);
        for(Player p : loc.getNearbyPlayers(60, 20, 60)){
            double dmg = (80/p.getLocation().distance(loc))*8;
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);
            if(dmg>=p.getHealth()){
                game.kill(p);
            }else{
                p.damage((80/p.getLocation().distance(loc))*8);
            }

        }
    }
    public void setTime(int i){
        time = i;
    }
}
