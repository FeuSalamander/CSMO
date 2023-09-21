package feusalamander.cs_mo.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static feusalamander.cs_mo.CS_MO.main;

public class PlayerData {
    private YamlConfiguration config;
    private File f;
    public PlayerData(){
        f = new File(main.getDataFolder(), "PlayerData.yml");
        if(!f.exists()) main.saveResource("PlayerData.yml", false);
        this.config = YamlConfiguration.loadConfiguration(f);
    }
    public void save(){
        try{
            config.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void createUuid(UUID uuid1){
        String uuid = String.valueOf(uuid1);
        config.set(uuid+".name", Objects.requireNonNull(Bukkit.getPlayer(uuid1)).getName());
        config.set(uuid+".elo", 0);
        config.set(uuid+".kills", 0);
        config.set(uuid+".deaths", 0);
    }
    public int getElo(UUID uuid){
        if(!config.contains(String.valueOf(uuid)))return 0;
        return config.getInt(uuid+".elo");
    }
    public void addElo(UUID uuid, int value){
        if(!config.contains(String.valueOf(uuid)))return;
        config.set(uuid+".elo", getElo(uuid)+value);
    }
    public boolean hasJoined(UUID uuid){
        return config.contains(String.valueOf(uuid));
    }
}
