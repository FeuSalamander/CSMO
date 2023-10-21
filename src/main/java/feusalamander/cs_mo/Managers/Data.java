package feusalamander.cs_mo.Managers;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map;

import static feusalamander.cs_mo.CS_MO.main;

public class Data {
    public static void saveData(){
        if(!main.getConf().isMysql())main.getPlayerData().save();
    }
    public static void createData(Player p){
        if(main.getConf().isMysql()){
            Bukkit.getScheduler().runTaskAsynchronously(main, () -> main.getMySQL().createUuid(p));
        }else{
            if(!main.getPlayerData().hasJoined(p.getUniqueId()))main.getPlayerData().createUuid(p.getUniqueId());
        }
    }
    private static int getRaw(UUID uuid, String raw){
        Pair<ResultSet, PreparedStatement> resultSet = main.getMySQL().getResult(uuid, "SELECT uuid, "+raw+" FROM player_stats WHERE uuid = ?");
        try {
            int raw2 = 0;
            while(resultSet.left().next()){
                raw2 = resultSet.left().getInt(raw);
            }
            resultSet.left().close();
            resultSet.right().close();
            return raw2;
        } catch (SQLException e) {
            main.getLogger().info(e.toString());
        }
        return 0;
    }
    public static int getElo(UUID uuid){
        if(main.getConf().isMysql()){
            return getRaw(uuid, "elo");
        }else{
            return main.getPlayerData().getElo(uuid);
        }
    }
    public static void addElo(UUID uuid, int i){
        if(main.getConf().isMysql()){
            int elo = getElo(uuid)+i;
            main.getMySQL().setValue(uuid, "UPDATE player_stats SET elo = ? WHERE uuid = ?;", elo);
        }else{
            main.getPlayerData().addElo(uuid, i);
        }
    }
    public static int getKills(UUID uuid){
        if(main.getConf().isMysql()){
            return getRaw(uuid, "kills");
        }else{
            return main.getPlayerData().getKills(uuid);
        }
    }
    public static void addKills(UUID uuid, int i){
        if(main.getConf().isMysql()){
            int kills = getKills(uuid)+i;
            main.getMySQL().setValue(uuid, "UPDATE player_stats SET kills = ? WHERE uuid = ?;", kills);
        }else{
            main.getPlayerData().addKills(uuid, i);
        }
    }
    public static int getDeaths(UUID uuid){
        if(main.getConf().isMysql()){
            return getRaw(uuid, "deaths");
        }else{
            return main.getPlayerData().getDeaths(uuid);
        }
    }
    public static void addDeaths(UUID uuid, int i){
        if(main.getConf().isMysql()){
            int deaths = getDeaths(uuid)+i;
            main.getMySQL().setValue(uuid, "UPDATE player_stats SET deaths = ? WHERE uuid = ?;", deaths);
        }else{
            main.getPlayerData().addDeaths(uuid, i);
        }
    }
    public static int getWins(UUID uuid){
        if(main.getConf().isMysql()){
            return getRaw(uuid, "wins");
        }else{
            return main.getPlayerData().getWins(uuid);
        }
    }
    public static void addWins(UUID uuid, int i){
        if(main.getConf().isMysql()){
            int wins = getWins(uuid)+i;
            main.getMySQL().setValue(uuid, "UPDATE player_stats SET wins = ? WHERE uuid = ?;", wins);
        }else{
            main.getPlayerData().addWins(uuid, i);
        }
    }
    public static int getLooses(UUID uuid){
        if(main.getConf().isMysql()){
            return getRaw(uuid, "looses");
        }else{
            return main.getPlayerData().getLooses(uuid);
        }
    }
    public static void addLooses(UUID uuid, int i){
        if(main.getConf().isMysql()){
            int looses = getLooses(uuid)+i;
            main.getMySQL().setValue(uuid, "UPDATE player_stats SET looses = ? WHERE uuid = ?;", looses);
        }else{
            main.getPlayerData().addLooses(uuid, i);
        }
    }
    public static void importC(CommandSender sender){
        if(main.getConf().isMysql()){
            File f = new File(main.getDataFolder(), "PlayerData.yml");
            if(!f.exists()){sender.sendMessage("§cYou can't import because there is no PlayerData.yml");return;}
            YamlConfiguration config = YamlConfiguration.loadConfiguration(f);

            List<HashMap<String, Object>> list = new ArrayList<>();
            Set<String> keyset = config.getKeys(false);
            for(String uuid : keyset){
                HashMap<String, Object> map = new HashMap<>();
                map.put("uuid", uuid);
                for(String key : Objects.requireNonNull(config.getConfigurationSection(uuid)).getKeys(false)){
                    map.put(key, config.get(uuid+"."+key));
                }
                list.add(map);
            }
            main.getMySQL().importC(list);
            sender.sendMessage("§aThe yml data has been imported");
        }else {
            sender.sendMessage("§cYou can't import because mySQL is not enabled");
        }
    }
    public static void export(CommandSender sender){
        if(main.getConf().isMysql()){
            int k = 1;
            File f = new File(main.getDataFolder(), "PlayerData Export.yml");
            while(f.exists()){
                f = new File(main.getDataFolder(), "PlayerData Export "+k+".yml");
                k++;
            }

            try {
                boolean b = f.createNewFile();
                YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
                Pair<ResultSet, PreparedStatement> resultSet = main.getMySQL().export();
                List<HashMap<String, Object>> data = new ArrayList<>();

                while (resultSet.left().next()) {
                    HashMap<String, Object> row = new LinkedHashMap<>();

                    for (int i = 1; i <= resultSet.left().getMetaData().getColumnCount(); i++) {
                        String columnName = resultSet.left().getMetaData().getColumnName(i);
                        Object value = resultSet.left().getObject(i);
                        row.put(columnName, value);
                    }
                    data.add(row);
                }

                for (Map<String, Object> map : data) {
                    String uuid = (String) map.get("uuid");
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();

                        if (!key.equals("uuid")) {
                            config.set(uuid + "." + key, value);
                        }
                    }
                }

                config.save(f);
                resultSet.left().close();
                resultSet.right().close();
                sender.sendMessage("§aThe mySQL data has been exported");
            } catch (SQLException | IOException e) {
                main.getLogger().info(e.toString());
            }
        }else{
            sender.sendMessage("§cYou can't export because mySQL is not enabled");
        }
    }
}
