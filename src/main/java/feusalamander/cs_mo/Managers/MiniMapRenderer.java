package feusalamander.cs_mo.Managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.map.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class MiniMapRenderer extends MapRenderer {
    private final Game game;
    private final MapView mapView;
    private final MapCursorCollection mapCursorCollection;
    private final HashMap<Player, MapCursor> mapCursorHashMap = new HashMap<>();
    public MiniMapRenderer(Game game, MapView mapView, List<Player> players){
        this.game = game;
        this.mapView = mapView;
        this.mapCursorCollection = new MapCursorCollection();
        mapView.getRenderers().clear();
        mapView.setLocked(true);
        addCursors(players);
        mapView.addRenderer(this);
    }
    @Override
    public void render(@NotNull MapView mapView, @NotNull MapCanvas mapCanvas, @NotNull Player player) {
        mapCanvas.setCursors(mapCursorCollection);
        if(game.getMap().first().getImg() != null) mapCanvas.drawImage(0, 0, game.getMap().first().getImg());
    }
    private void addCursors(List<Player> players){
        for(Player p : players){
            MapCursor cursor = new MapCursor((byte) 0, (byte) 0, (byte) 0, MapCursor.Type.GREEN_POINTER, true);
            mapCursorHashMap.put(p, cursor);
            this.mapCursorCollection.addCursor(cursor);
        }
    }
    public void updateCursors(){
        Location corner = game.getPlace()[2];
        int x = corner.getBlockX();
        int y = corner.getBlockZ();
        for(Player p : mapCursorHashMap.keySet()){
            MapCursor cursor = mapCursorHashMap.get(p);
            cursor.setX((byte) ((((p.getX()-x)/104)*128)-64));
            cursor.setY((byte) ((((p.getZ()-y)/130)*128)-64));
            float yaw = p.getYaw();
            if(yaw>=0){
                cursor.setDirection((byte) ((p.getYaw()/360)*16));
            }else{
                cursor.setDirection( (byte) ( ((p.getYaw()/360)*16)+16 ) );
            }
        }
    }
}
