package pl.wurmonline.deedplanner.logic.height;

import javax.swing.*;
import pl.wurmonline.deedplanner.data.*;
import pl.wurmonline.deedplanner.graphics.UpCamera;
import pl.wurmonline.deedplanner.input.Mouse;
import pl.wurmonline.deedplanner.logic.TileFragment;
import pl.wurmonline.deedplanner.util.DeedPlannerRuntimeException;

public class HeightUpdater {

    public static HeightMode currentMode;
    
    public static JSpinner setRMB;
    public static JSpinner setLMB;
    
    public static int setLeft = 5;
    public static int setRight = -5;
    public static int add = 1;
    
    public static void update(Mouse mouse, Map map, UpCamera cam) {
        currentMode.update(mouse, map, cam);
    }
    
    public static ListModel<HeightMode> createListModel() {
        DefaultListModel<HeightMode> model = new DefaultListModel<>();
        
        model.addElement(new HeightMode("Set height") {

            public void action(Mouse mouse, Map map, Tile tile, TileFragment frag) {
                for (Tile t : getAffectedTiles(tile, frag)) {
                    if (mouse.hold.left) {
                        t.setHeight(setLeft);
                    }
                    else if (mouse.hold.right) {
                        t.setHeight(setRight);
                    }
                    else if (mouse.released.left || mouse.released.right) {
                        map.newAction();
                    }
                }
            }
            
        });
        
        
        
        model.addElement(new HeightMode("Add height") {

            public void action(Mouse mouse, Map map, Tile tile, TileFragment frag) {
                for (Tile t : getAffectedTiles(tile, frag)) {
                    if (mouse.pressed.left) {
                        t.setHeight(t.getHeight()+add);
                    }
                    else if (mouse.pressed.right) {
                        t.setHeight(t.getHeight()-add);
                    }
                    else if (mouse.released.left || mouse.released.right) {
                        map.newAction();
                    }
                }
            }
            
        });
        
        
        
        model.addElement(new HeightMode("Remove height") {

            public void action(Mouse mouse, Map map, Tile tile, TileFragment frag) {
                for (Tile t : getAffectedTiles(tile, frag)) {
                    if (mouse.pressed.left) {
                        t.setHeight(t.getHeight()-add);
                    }
                    else if (mouse.pressed.right) {
                        t.setHeight(t.getHeight()+add);
                    }
                    else if (mouse.released.left || mouse.released.right) {
                        map.newAction();
                    }
                }
            }
            
        });
        
        model.addElement(new HeightMode("Select height") {

            public void action(Mouse mouse, Map map, Tile tile, TileFragment frag) {
                if (frag.isCorner()) {
                    if (mouse.pressed.left) {
                        setLMB.getModel().setValue(tile.getHeight());
                    }
                    else if (mouse.pressed.right) {
                        setRMB.getModel().setValue(tile.getHeight());
                    }
                }
            }
            
        });
        
        model.addElement(new HeightMode("Reset height") {

            public void action(Mouse mouse, Map map, Tile tile, TileFragment frag) {
                for (Tile t : getAffectedTiles(tile, frag)) {
                    if (mouse.hold.left || mouse.hold.right) {
                        t.setHeight(5);
                    }
                    else if (mouse.released.left || mouse.released.right) {
                        map.newAction();
                    }
                }
            }
            
        });
        
        model.addElement(new HeightMode("Smooth height") {

            private Tile tile2;
            
            public void action(Mouse mouse, Map map, Tile tile, TileFragment frag) {
                if (mouse.pressed.right) {
                    tile2 = null;
                }
                if (mouse.pressed.left) {
                    if (tile2==null) {
                        if (frag.isCorner()) {
                            tile2 = frag.getTileByCorner(tile);
                        }
                    }
                    else if (frag.isCorner()) {
                        tile = frag.getTileByCorner(tile);
                        if (tile.getX()==tile2.getX() ^ tile.getY()==tile2.getY()) {
                            int min = Math.min(tile.getHeight(), tile2.getHeight());
                            int max = Math.max(tile.getHeight(), tile2.getHeight());
                            int diff = max-min;
                            Tile higher = tile.getHeight()==max ? tile : tile2;
                            Tile lower = tile.getHeight()==min ? tile : tile2;

                            int mult = 0;

                            if (tile.getX()==tile2.getX()) {
                                diff/=Math.abs(higher.getY()-lower.getY());
                                if (higher.getY()>lower.getY()) {
                                    for (int i=lower.getY(); i<higher.getY(); i++) {
                                        map.getTile(tile.getX(), i).setHeight(min + diff*mult);
                                        mult++;
                                    }
                                }
                                else if (lower.getY()>higher.getY()) {
                                    for (int i=lower.getY(); i>higher.getY(); i--) {
                                        map.getTile(tile.getX(), i).setHeight(min + diff*mult);
                                        mult++;
                                    }
                                }
                            }
                            else if (tile.getY()==tile2.getY()) {
                                diff/=Math.abs(higher.getX()-lower.getX());
                                if (higher.getX()>lower.getX()) {
                                    for (int i=lower.getX(); i<higher.getX(); i++) {
                                        map.getTile(i, tile.getY()).setHeight(min + diff*mult);
                                        mult++;
                                    }
                                }
                                else if (lower.getX()>higher.getX()) {
                                    for (int i=lower.getX(); i>higher.getX(); i--) {
                                        map.getTile(i, tile.getY()).setHeight(min + diff*mult);
                                        mult++;
                                    }
                                }
                            }
                            map.newAction();
                        }
                        tile2=null;
                    }
                }
            }
            
        });
        
        model.addElement(new HeightMode("Level area") {

            private Tile tile2;
            
            public void action(Mouse mouse, Map map, Tile tile, TileFragment frag) {
                if (mouse.pressed.left || mouse.pressed.right) {
                    if (tile2==null) {
                        if (frag.isCorner()) {
                            tile2 = frag.getTileByCorner(tile);
                        }
                    }
                    else if (frag.isCorner()) {
                        tile = frag.getTileByCorner(tile);
                        
                        int minX = Math.min(tile.getX(), tile2.getX());
                        int maxX = Math.max(tile.getX(), tile2.getX());
                        int minY = Math.min(tile.getY(), tile2.getY());
                        int maxY = Math.max(tile.getY(), tile2.getY());

                        int set;
                        if (mouse.pressed.left) {
                            set = setLeft;
                        }
                        else if (mouse.pressed.right) {
                            set = setRight;
                        }
                        else {
                            throw new IllegalArgumentException("Impossible, but...");
                        }

                        for (int i=minX; i<=maxX; i++) {
                            for (int i2=minY; i2<=maxY; i2++) {
                                map.getTile(i, i2).setHeight(set);
                            }
                        }
                        
                        map.newAction();
                        tile2=null;
                    }
                }
            }
            
        });
        
        model.addElement(new HeightMode("Lift area") {

            private Tile tile2;
            
            public void action(Mouse mouse, Map map, Tile tile, TileFragment frag) {
                if (mouse.pressed.left || mouse.pressed.right) {
                    if (tile2==null) {
                        if (frag.isCorner()) {
                            tile2 = frag.getTileByCorner(tile);
                        }
                    }
                    else if (frag.isCorner()) {
                        tile = frag.getTileByCorner(tile);
                        
                        int minX = Math.min(tile.getX(), tile2.getX());
                        int maxX = Math.max(tile.getX(), tile2.getX());
                        int minY = Math.min(tile.getY(), tile2.getY());
                        int maxY = Math.max(tile.getY(), tile2.getY());

                        int add;
                        if (mouse.pressed.left) {
                            add = HeightUpdater.add;
                        }
                        else if (mouse.pressed.right) {
                            add = -HeightUpdater.add;
                        }
                        else {
                            throw new IllegalArgumentException("Impossible, but...");
                        }

                        for (int i=minX; i<=maxX; i++) {
                            for (int i2=minY; i2<=maxY; i2++) {
                                Tile t = map.getTile(i, i2);
                                t.setHeight(t.getHeight()+add);
                            }
                        }
                        
                        map.newAction();
                        tile2=null;
                    }
                }
            }
            
        });
        
        return model;
    }
    
    private static Tile[] getAffectedTiles(Tile tile, TileFragment frag) {
        final Tile[] tiles;
        if (frag==TileFragment.CENTER) {
            tiles = new Tile[4];
            tiles[0] = tile;
            tiles[1] = tile.getMap().getTile(tile, 1, 0);
            tiles[2] = tile.getMap().getTile(tile, 1, 1);
            tiles[3] = tile.getMap().getTile(tile, 0, 1);
        }
        else if (frag==TileFragment.S) {
            tiles = new Tile[2];
            tiles[0] = tile;
            tiles[1] = tile.getMap().getTile(tile, 1, 0);
        }
        else if (frag==TileFragment.N) {
            tiles = new Tile[2];
            tiles[0] = tile.getMap().getTile(tile, 0, 1);
            tiles[1] = tile.getMap().getTile(tile, 1, 1);
        }
        else if (frag==TileFragment.W) {
            tiles = new Tile[2];
            tiles[0] = tile;
            tiles[1] = tile.getMap().getTile(tile, 0, 1);
        }
        else if (frag==TileFragment.E) {
            tiles = new Tile[2];
            tiles[0] = tile.getMap().getTile(tile, 1, 0);
            tiles[1] = tile.getMap().getTile(tile, 1, 1);
        }
        else if (frag==TileFragment.SW) {
            tiles = new Tile[] {tile};
        }
        else if (frag==TileFragment.SE) {
            tiles = new Tile[] {tile.getMap().getTile(tile, 1, 0)};
        }
        else if (frag==TileFragment.NW) {
            tiles = new Tile[] {tile.getMap().getTile(tile, 0, 1)};
        }
        else if (frag==TileFragment.NE) {
            tiles = new Tile[] {tile.getMap().getTile(tile, 1, 1)};
        }
        else {
            throw new DeedPlannerRuntimeException("Illegal argument");
        }
        return tiles;
    }
    
}