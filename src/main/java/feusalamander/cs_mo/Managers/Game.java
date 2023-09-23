package feusalamander.cs_mo.Managers;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static feusalamander.cs_mo.CS_MO.main;
@SuppressWarnings("deprecation")
public class Game implements Listener {
    private final Random random;
    private final List<Player> players;
    private final List<Player> AT = new ArrayList<>();
    private final List<Player> T = new ArrayList<>();
    private final Location[][] place;
    private final List<Location> atSpawns = new ArrayList<>();
    private final List<Location> tSpawns = new ArrayList<>();
    private final BossBar bar;
    private final int[] score = new int[2];
    private final int round = 0;
    public Game(List<Player> players){
        this.players = players;
        main.getGames().add(this);
        main.getServer().getPluginManager().registerEvents(this, main);
        random = new Random();
        place = choosePlace();
        createTeams();
        createSpawns();
        chooseSpawns();
        bar = Bukkit.createBossBar("§9AT: 0-§60 :T", BarColor.BLUE, BarStyle.SEGMENTED_12);
        bar.setProgress((double) round /12);
        for(Player p : players){
            bar.addPlayer(p);
        }
        start();
    }
    private Location[][] choosePlace(){
        int crash = 0;
        while (place == null&&crash<main.getMaps().size()){
            Map map = main.getMaps().get(random.nextInt(main.getMaps().size()));
            for(int i = 0; i<map.getSchematics().size(); i++){
                Pair<Boolean, Location[][]> pair = map.getSchematics().get(i);
                if(pair.left()){
                    Location[][] map1 = pair.right();
                    map.setPair(false, i);
                    return map1;
                }
            }
            crash++;
            for(Player p : players)p.sendMessage("§4No available maps were found");
        }
        return null;
    }
    private void createTeams(){
        List<Player> dump = new ArrayList<>(players);
        for(int i = 0; i<players.size(); i++){
            Player p = dump.get(random.nextInt(dump.size()));
            dump.remove(p);
            if(AT.size() < players.size()/2){
                AT.add(p);
            }else{
                T.add(p);
            }
        }
        for(Player p : AT)p.sendMessage("§9Your are an AT");
        for(Player p : T)p.sendMessage("§6Your are a T");
    }
    private void createSpawns(){
        Location mainAT = place[0][0].clone();
        Location mainT = place[0][1].clone();
        for(int i = 0; i<AT.size(); i++){
            double x = place[0][0].x()+(random.nextInt(6)-2.5);
            double z = place[0][0].z()+(random.nextInt(6)-2.5);
            mainAT.setX(x);
            mainAT.setZ(z);
            atSpawns.add(mainAT);
        }
        for(int i = 0; i<T.size(); i++){
            double x = place[0][1].x()+(random.nextInt(6)-2.5);
            double z = place[0][1].z()+(random.nextInt(6)-2.5);
            mainT.setX(x);
            mainT.setZ(z);
            tSpawns.add(mainT);
        }
    }
    private void chooseSpawns(){
        List<Location> dumpAT = new ArrayList<>(atSpawns);
        List<Location> dumpT = new ArrayList<>(tSpawns);
        for(Player p : AT){
            Location loc = dumpAT.get(random.nextInt(dumpAT.size()));
            dumpAT.remove(loc);
            p.teleport(loc);
        }
        for(Player p : T){
            Location loc = dumpT.get(random.nextInt(dumpT.size()));
            dumpT.remove(loc);
            p.teleport(loc);
        }
    }
    private void start(){

    }
    public BossBar getBar() {
        return bar;
    }
}
