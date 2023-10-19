package feusalamander.cs_mo.Configs;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import static feusalamander.cs_mo.CS_MO.main;

public class Config {
    private final boolean mysql;
    private final int matchMaking;
    private final int minPlayer;
    private final String serverip;
    private final Location spawn;

    private String host;
    private String user;
    private String pass;
    private String dbName;
    private int port;
    public Config(FileConfiguration config){
        this.mysql = config.getBoolean("mysql.enabled");
        this.matchMaking = config.getInt("matchmaking");
        this.minPlayer = config.getInt("minPlayers");
        this.serverip = config.getString("serverip");
        this.spawn = config.getLocation("spawn");

        this.host = config.getString("mysql.host");
        this.user = config.getString("mysql.user");
        this.pass = config.getString("mysql.pass");
        this.dbName = config.getString("mysql.dbName");
        this.port = config.getInt("mysql.port");
    }
    public boolean isMysql() {
        return mysql;
    }
    public int getMatchMaking(){
        return matchMaking;
    }
    public int getMinPlayer(){
        return minPlayer;
    }
    public String getServerip() {
        return serverip;
    }
    public Location getSpawn(){
        return spawn;
    }

    public String getUser() {
        return user;
    }
    public String getPass() {
        return pass;
    }
    public String toURL(){
        return "jdbc:mysql://" +
                host +
                ":" +
                port +
                "/" +
                dbName;
    }
}
