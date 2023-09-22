package feusalamander.cs_mo.Managers;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static feusalamander.cs_mo.CS_MO.main;

public class Map {
    private final String id;
    private final String name;
    private final boolean enabled;
    private final List<Pair<Boolean,Location[][]>> schematics = new ArrayList<>();

    public Map(final String id){
        this.id = id;
        final ConfigurationSection section = main.getMapConf().getConfig().getConfigurationSection(id);
        assert section != null;
        this.name = section.getString("name");
        this.enabled = section.getBoolean("enabled");
        final ConfigurationSection section2 = section.getConfigurationSection("maps");
        assert section2 != null;
        for(String key : section2.getKeys(false)){
            Location[][] map = new Location[2][2];
            map[0][0] = section2.getLocation(key+"spawns.AT");
            map[0][1] = section2.getLocation(key+"spawns.T");
            map[1][0] = section2.getLocation(key+"sites.A");
            map[1][1] = section2.getLocation(key+"sites.B");
            schematics.add(Pair.of(true, map));
        }
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public List<Pair<Boolean,Location[][]>> getSchematics() {
        return schematics;
    }
}
