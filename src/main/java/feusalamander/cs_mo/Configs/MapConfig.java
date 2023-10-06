package feusalamander.cs_mo.Configs;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Set;

import static feusalamander.cs_mo.CS_MO.main;

public class MapConfig {
    private YamlConfiguration config;
    private File f;
    private Set<String> maps;
    public MapConfig(){
        f = new File(main.getDataFolder(), "Maps.yml");
        if(!f.exists()) main.saveResource("Maps.yml", false);
        File f2 = new File(main.getDataFolder(), "pictures");
        if(!f2.exists()) main.saveResource("pictures", false);
        this.config = YamlConfiguration.loadConfiguration(f);
        maps = config.getKeys(false);
    }
    public YamlConfiguration getConfig() {
        return config;
    }
    public Set<String> getMaps() {
        return maps;
    }
}
