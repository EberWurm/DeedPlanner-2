package pl.wurmonline.deedplanner.util.jogl;

import javax.media.opengl.GL2;
import org.w3c.dom.*;

public class Model implements Renderable {
    
    public final String tag;
    private final Mesh[] meshes;
    
    public Model(Mesh... meshes) {
        this.tag = "";
        this.meshes = meshes;
    }
    
    public Model(Element node) {
        this.tag = node.getAttribute("tag");
        String modelLoc = node.getAttribute("location");
        String scaleStr = node.getAttribute("scale");        
        float scale;
        try {
            scale = Float.parseFloat(scaleStr);
        }
        catch (NumberFormatException ex) {
            scale = 1;
        }
        NodeList list = node.getElementsByTagName("mesh");
        meshes = new Mesh[list.getLength()];        
        for (int i=0; i<list.getLength(); i++) {
            Element e = (Element) list.item(i);
            if(e == null)
                break;
            String name = e.getAttribute("name");
            String texStr = e.getAttribute("tex");
            String matStr = e.getAttribute("material");
         
            Tex tex = null;
            if (!texStr.isEmpty()) {
                tex = Tex.getTexture(texStr);
            }
            meshes[i] = new Mesh(modelLoc, name, tex, matStr, scale);
        }
    }
    
    public void render(GL2 g) {
        for (Mesh mesh : meshes) {
            mesh.render(g);
        }
    }
    
}
