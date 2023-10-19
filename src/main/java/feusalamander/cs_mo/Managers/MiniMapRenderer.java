package feusalamander.cs_mo.Managers;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.map.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class MiniMapRenderer extends MapRenderer {
    private final Game game;
    private final MapCursorCollection mapCursorCollection;
    private final HashMap<String, MapCursor> mapCursorHashMap = new HashMap<>();
    private final int x;
    private final int y;
    private boolean first = true;
    private Pair<MapCursor, Location> bombCursorT;
    private Pair<MapCursor, Location> bombCursorAT;
    public MiniMapRenderer(Game game, MapView mapView, List<Player> players){
        this.game = game;
        this.mapCursorCollection = new MapCursorCollection();
        mapView.getRenderers().clear();
        mapView.setLocked(true);
        addCursors(players);
        mapView.addRenderer(this);
        this.x = game.getPlace()[2].getBlockX();
        this.y = game.getPlace()[2].getBlockZ();
    }
    @Override
    public void render(@NotNull MapView mapView, @NotNull MapCanvas mapCanvas, @NotNull Player player) {
        if(first){
            first = false;
            if(game.getMap().first().getImg() != null) mapCanvas.drawImage(0, 0, game.getMap().first().getImg());
            mapCanvas.setCursors(mapCursorCollection);
        }
    }
    private void addCursors(List<Player> players){
        for(Player p : players){
            MapCursor cursor = new MapCursor((byte) 0, (byte) 0, (byte) 0, MapCursor.Type.GREEN_POINTER, true);
            mapCursorHashMap.put(p.getName(), cursor);
            this.mapCursorCollection.addCursor(cursor);
        }
        bombCursorT = Pair.of(new MapCursor((byte) 0, (byte) 0, (byte) 0, MapCursor.Type.TEMPLE, false), null);
        this.mapCursorCollection.addCursor(bombCursorT.left());
        bombCursorAT = Pair.of(new MapCursor((byte) 0, (byte) 0, (byte) 0, MapCursor.Type.RED_X, false), null);
        this.mapCursorCollection.addCursor(bombCursorAT.left());
    }
    public void updateCursors(){
        for(String name : mapCursorHashMap.keySet()){
            Player p = Bukkit.getPlayer(name);
            if(p != null&&p.isOnline()){
                MapCursor cursor = mapCursorHashMap.get(name);
                if(!game.getSpecs().containsKey(p.getName())){
                    if(!cursor.isVisible())cursor.setVisible(true);
                }else{
                    cursor.setVisible(false);
                }
                cursor.setX((byte) ((((p.getX()-x)/game.getMap().first().getSize()[0])*256)-128));
                cursor.setY((byte) ((((p.getZ()-y)/game.getMap().first().getSize()[1])*256)-128));
                float yaw = p.getYaw();
                if(yaw>=0){
                    cursor.setDirection((byte) ((p.getYaw()/360)*16));
                }else{
                    cursor.setDirection( (byte) ( ((p.getYaw()/360)*16)+16 ) );
                }
            }
        }
        updateTBomb();
        updateATBomb();
    }
    private void updateTBomb(){
        if(bombCursorT.right() == null){
            bombCursorT.left().setVisible(false);
            return;
        }
        bombCursorT.left().setVisible(true);
        bombCursorT.left().setX((byte) ((((bombCursorT.right().getX()-x)/game.getMap().first().getSize()[0])*256)-128));
        bombCursorT.left().setY((byte) ((((bombCursorT.right().getZ()-y)/game.getMap().first().getSize()[1])*256)-128));
    }
    private void updateATBomb(){
        if(bombCursorAT.right() == null){
            bombCursorAT.left().setVisible(false);
            return;
        }
        bombCursorAT.left().setVisible(true);
        bombCursorAT.left().setX((byte) ((((bombCursorAT.right().getX()-x)/game.getMap().first().getSize()[0])*256)-128));
        bombCursorAT.left().setY((byte) ((((bombCursorAT.right().getZ()-y)/game.getMap().first().getSize()[1])*256)-128));
    }
    public void changeType(MapCursor.Type type, Player p){
        MapCursor cursor = mapCursorHashMap.get(p.getName());
        if(cursor != null)cursor.setType(type);

    }
    public void changeBombT(Location loc){
        bombCursorT = Pair.of(bombCursorT.first(), loc);
    }
    public void changeBombAT(Location loc){
        bombCursorAT = Pair.of(bombCursorAT.first(), loc);
    }
    public void changeSide(List<Player> players){
        for(int i = 0; i<mapCursorCollection.size(); i++)mapCursorCollection.removeCursor(mapCursorCollection.getCursor(i));
        mapCursorHashMap.clear();
        addCursors(players);
    }
}
