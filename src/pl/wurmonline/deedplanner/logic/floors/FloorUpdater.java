package pl.wurmonline.deedplanner.logic.floors;

import javax.swing.DefaultComboBoxModel;
import pl.wurmonline.deedplanner.Globals;
import pl.wurmonline.deedplanner.data.*;
import pl.wurmonline.deedplanner.graphics.UpCamera;
import pl.wurmonline.deedplanner.input.Mouse;

public class FloorUpdater {

    public static FloorMode currentMode;
    public static FloorData currentData = null;
    
    public static void update(Mouse mouse, Map map, UpCamera cam) {
        currentMode.update(mouse, map, cam);
    }
    
    public static DefaultComboBoxModel<FloorMode> createComboModel() {
        DefaultComboBoxModel<FloorMode> model = new DefaultComboBoxModel<>();
        
        model.addElement(new FloorMode("Pencil") {

            public void action(Mouse mouse, Map map, Tile tile) {
                if (mouse.hold.left) {
                    if (currentData!=null && !(tile.getTileContent(Globals.floor) instanceof Roof)) {
                        if (currentData.opening && Globals.floor==0) {
                            return;
                        }
                        tile.setTileContent(currentData, Globals.floor);
                    }
                }
                else if (mouse.hold.right) {
                    if (!(tile.getTileContent(Globals.floor) instanceof Roof)) {
                        tile.setTileContent(null, Globals.floor);
                    }
                }
                else if (mouse.released.left || mouse.released.right) {
                    map.newAction();
                }
            }
            
        });
        
        model.addElement(new FloorMode("Eraser") {

            public void action(Mouse mouse, Map map, Tile tile) {
                if (mouse.hold.left  && !(tile.getTileContent(Globals.floor) instanceof Roof)) {
                    tile.setTileContent(null, Globals.floor);
                }
                else if (mouse.hold.right  && !(tile.getTileContent(Globals.floor) instanceof Roof)) {
                    tile.setTileContent(null, Globals.floor);
                }
                else if (mouse.released.left || mouse.released.right) {
                    map.newAction();
                }
            }
            
        });
        
        return model;
    }
    
}