package feusalamander.cs_mo.Configs;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    private final boolean mysql;
    private final int matchMaking;
    private final int minPlayer;
    private final String serverip;
    public Config(FileConfiguration config){
        this.mysql = config.getBoolean("mysql.enabled");
        this.matchMaking = config.getInt("matchmaking");
        this.minPlayer = config.getInt("minPlayers");
        this.serverip = config.getString("serverip");
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
}
