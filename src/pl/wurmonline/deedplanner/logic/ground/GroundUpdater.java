package pl.wurmonline.deedplanner.logic.ground;

import java.util.Stack;
import javax.swing.DefaultComboBoxModel;
import pl.wurmonline.deedplanner.data.*;
import pl.wurmonline.deedplanner.data.storage.Data;
import pl.wurmonline.deedplanner.graphics.UpCamera;
import pl.wurmonline.deedplanner.input.Mouse;

public class GroundUpdater {

    public static GroundMode currentMode;
    public static GroundData currentData = Data.grounds.get("gr");
    
    private static final GroundData defaultData = Data.grounds.get("gr");
    
    public static void update(Mouse mouse, Map map, UpCamera cam) {
        currentMode.update(mouse, map, cam);
    }
    
    public static DefaultComboBoxModel<GroundMode> createComboModel() {
        DefaultComboBoxModel<GroundMode> model = new DefaultComboBoxModel<>();
        
        model.addElement(new GroundMode(java.util.ResourceBundle.getBundle("pl/wurmonline/deedplanner/forms/Bundle").getString("PENCIL")) {

            public void action(Mouse mouse, Map map, Tile tile) {
                if (mouse.hold.left) {
                    if (currentData!=null) {
                        tile.setGround(currentData);
                        map.getSymmetry().mirrorGround(tile, currentData);
                    }
                }
                else if (mouse.hold.right) {
                    if (currentData!=null) {
                        tile.setGround(defaultData);
                        map.getSymmetry().mirrorGround(tile, defaultData);
                    }
                }
                else if (mouse.released.left || mouse.released.right) {
                    map.newAction();
                }
            }
            
        });
        
        model.addElement(new GroundMode(java.util.ResourceBundle.getBundle("pl/wurmonline/deedplanner/forms/Bundle").getString("FILL")) {
            
            public void action(Mouse mouse, Map map, Tile tile) {
                if (mouse.pressed.left) {
                    floodFill(map, tile, currentData, tile.getGround().getData());
                }
                else if (mouse.pressed.right) {
                    floodFill(map, tile, defaultData, tile.getGround().getData());
                }
                else if (mouse.released.left || mouse.released.right) {
                    map.newAction();
                }
            }
            
            private void floodFill(Map map, Tile tile, GroundData data, GroundData toReplace) {
                Stack<Tile> stack = new Stack<>();
                stack.push(tile);
                
                while (!stack.empty()) {
                    Tile anchor = stack.pop();
                    if (anchor.getGround().getData()==toReplace) {
                        anchor.setGround(data);
                        map.getTileAndExecute(anchor, -1, 0, (t) -> {stack.push(t);});
                        map.getTileAndExecute(anchor, 1, 0, (t) -> {stack.push(t);});
                        map.getTileAndExecute(anchor, 0, -1, (t) -> {stack.push(t);});
                        map.getTileAndExecute(anchor, 0, 1, (t) -> {stack.push(t);});
                    }
                }
            }
            
        });
        
        model.addElement(new GroundMode(java.util.ResourceBundle.getBundle("pl/wurmonline/deedplanner/forms/Bundle").getString("ERASER")) {

            public void action(Mouse mouse, Map map, Tile tile) {
                if (mouse.hold.left) {
                    tile.setGround(defaultData);
                    map.getSymmetry().mirrorGround(tile, defaultData);
                }
                else if (mouse.hold.right) {
                    tile.setGround(defaultData);
                    map.getSymmetry().mirrorGround(tile, defaultData);
                }
                else if (mouse.released.left || mouse.released.right) {
                    map.newAction();
                }
            }
            
        });
        
        return model;
    }
    
}
