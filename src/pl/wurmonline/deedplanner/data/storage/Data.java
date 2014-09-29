package pl.wurmonline.deedplanner.data.storage;

import java.util.HashMap;
import javax.swing.DefaultListModel;
import javax.swing.tree.*;
import pl.wurmonline.deedplanner.data.*;
import pl.wurmonline.deedplanner.util.jogl.Tex;

public class Data {

    public static final HashMap<String, GroundData> grounds = new HashMap<>();
    public static final DefaultMutableTreeNode groundsTree = new DefaultMutableTreeNode("Grounds");
    
    public static final HashMap<String, FloorData> floors = new HashMap<>();
    public static final DefaultMutableTreeNode floorsTree = new DefaultMutableTreeNode("Floors");
    
    public static final HashMap<String, WallData> walls = new HashMap<>();
    public static final DefaultMutableTreeNode wallsTree = new DefaultMutableTreeNode("Walls");
    
    public static final HashMap<String, RoofData> roofs = new HashMap<>();
    public static final DefaultListModel<RoofData> roofsList = new DefaultListModel<>();
    
    public static Tex water = Tex.getTexture("Data/Special/water.png");
    
}