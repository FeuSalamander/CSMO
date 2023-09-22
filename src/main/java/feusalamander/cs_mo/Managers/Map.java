package feusalamander.cs_mo.Managers;

import org.bukkit.configuration.ConfigurationSection;

import static feusalamander.cs_mo.CS_MO.main;

public class Map {
    private final String id;
    private final String name;
    private final boolean enabled;
    public Map(final String id){
        this.id = id;
        final ConfigurationSection section = main.getMapConf().getConfig().getConfigurationSection(id);
        this.name = section.getString("name");
        this.enabled = section.getBoolean("enabled");
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
}
