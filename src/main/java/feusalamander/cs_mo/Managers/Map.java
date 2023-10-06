package feusalamander.cs_mo.Managers;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static feusalamander.cs_mo.CS_MO.main;

public class Map {
    private final String id;
    private final String name;
    private final boolean enabled;
    private final List<Pair<Boolean,Location[]>> schematics = new ArrayList<>();
    private Image img = null;

    public Map(final String id){
        this.id = id;
        final ConfigurationSection section = main.getMapConf().getConfig().getConfigurationSection(id);
        assert section != null;
        this.name = section.getString("name");
        this.enabled = section.getBoolean("enabled");
        final ConfigurationSection section2 = section.getConfigurationSection("maps");
        assert section2 != null;
        for(String key : section2.getKeys(false)){
            Location[] map = new Location[3];
            map[0] = section2.getLocation(key+".spawns.AT");
            map[1] = section2.getLocation(key+".spawns.T");
            map[2] = section2.getLocation(key+".corner");
            schematics.add(Pair.of(true, map));
        }
        try{
            File file = new File(main.getDataFolder()+"/pictures", id+".jpg");
            img = ImageIO.read(file);
        } catch (Exception e) {
            main.getLogger().info("There is a problem with the picture of a map");
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
    public List<Pair<Boolean,Location[]>> getSchematics() {
        return schematics;
    }
    public void setPair(boolean state, int number){
        Location[] map = schematics.get(number).right();
        schematics.remove(number);
        schematics.add(Pair.of(state, map));
    }
    public Image getImg() {
        return img;
    }
}
