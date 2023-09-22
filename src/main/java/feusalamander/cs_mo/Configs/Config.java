package feusalamander.cs_mo.Configs;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    private final boolean mysql;
    private final int matchMaking;
    public Config(FileConfiguration config){
        this.mysql = config.getBoolean("mysql.enabled");
        this.matchMaking = config.getInt("matchmaking");
    }
    public boolean isMysql() {
        return mysql;
    }
    public int getMatchMaking(){
        return matchMaking;
    }

}
