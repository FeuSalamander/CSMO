package feusalamander.cs_mo.Managers;

import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.*;

import java.util.*;

import static feusalamander.cs_mo.CS_MO.main;
@SuppressWarnings("deprecation")
public class Game implements Listener {
    private final Random random;
    private String mapName;
    private final List<Player> players;
    private final List<Player> AT = new ArrayList<>();
    private final List<Player> T = new ArrayList<>();
    private final Location[][] place;
    private final List<Location> atSpawns = new ArrayList<>();
    private final List<Location> tSpawns = new ArrayList<>();
    private final HashMap<Player, Integer> money = new HashMap<>();
    private BossBar bar;
    private final int[] score = new int[2];
    private final int round = 0;
    private Scoreboard sb;
    public Game(List<Player> players){
        this.players = players;
        main.getGames().add(this);
        main.getServer().getPluginManager().registerEvents(this, main);
        random = new Random();
        place = choosePlace();
        createTeams();
        createSpawns();
        chooseSpawns();
        scoreboard();
        bossBar();
    }
    private Location[][] choosePlace(){
        int crash = 0;
        while (place == null&&crash<main.getMaps().size()){
            Map map = main.getMaps().get(random.nextInt(main.getMaps().size()));
            mapName = map.getName();
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
            money.put(p, 800);
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
    private void scoreboard(){
        sb = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = sb.registerNewObjective("§e§lMC:MO", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.getScore("Map: §a"+mapName).setScore(9);
        objective.getScore(" ").setScore(8);
        objective.getScore("§cBomb: ").setScore(7);
        objective.getScore("  ").setScore(6);
        objective.getScore("§9AT: ").setScore(5);
        objective.getScore("§6T: ").setScore(4);
        objective.getScore("   ").setScore(3);
        objective.getScore("    ").setScore(1);
        objective.getScore("§e"+main.getConf().getServerip()).setScore(0);

        Team bombV = sb.registerNewTeam("bombV");
        Team atV = sb.registerNewTeam("atV");
        Team tV = sb.registerNewTeam("tV");
        bombV.addEntry("§cBomb: ");
        atV.addEntry("§9AT: ");
        tV.addEntry("§6T: ");

        Team AT = sb.registerNewTeam("AT");
        Team T = sb.registerNewTeam("T");

        AT.setPrefix("§9AT ");
        T.setPrefix("§6T ");

        AT.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        T.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);

        for(Player p : players){
            p.setScoreboard(sb);
            if(this.AT.contains(p))AT.addPlayer(p);
            if(this.T.contains(p))T.addPlayer(p);
            Objects.requireNonNull(p.getScoreboard().getObjective("§e§lMC:MO")).getScore("§aMoney: §f"+money.get(p)).setScore(2);
        }
        bombV.setSuffix("Not planted");
        atV.setSuffix(String.valueOf(AT.getSize()));
        tV.setSuffix(String.valueOf(T.getSize()));
    }
    private void bossBar(){
        bar = Bukkit.createBossBar("§90 §f1:55 §60", BarColor.WHITE, BarStyle.SEGMENTED_12);
        bar.setProgress(0);
        for(Player p : players)bar.addPlayer(p);
    }
    private void updateBar(){
        bar.setProgress((double) round /20);
        bar.setTitle("§9"+score[0]+" §f1:55 §6"+score[1]);
    }
    private void start(){

    }
    private void unload(){
        HandlerList.unregisterAll(this);
    }
    public void remove(Player p){
        players.remove(p);
        p.setScoreboard(main.getScoreboard());
        bar.removePlayer(p);
    }
    public List<Player> getPlayers() {
        return players;
    }
    public BossBar getBar() {
        return bar;
    }
}
