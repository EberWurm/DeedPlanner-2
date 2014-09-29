package pl.wurmonline.deedplanner.util.jogl;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;
import javax.media.opengl.GL2;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import pl.wurmonline.deedplanner.util.DeedPlannerException;
import pl.wurmonline.deedplanner.util.FileUtils;
import pl.wurmonline.deedplanner.util.Log;

public final class Mesh {

    private final String name;
    private final String vertLoc;
    private int listID = 0;
    private final Tex tex;
    private final boolean ladder;
    
    public Mesh(String vertLoc, String name, Tex tex, boolean ladder) {
        this.name = name.toUpperCase();
        this.vertLoc = vertLoc;
        this.tex = tex;
        this.ladder = ladder;
    }
    
    public void render(GL2 g) {
        if (tex!=null) {
            tex.bind(g);
        }
        if (listID==0) {
            try {
                listID = loadMesh(g, new File(vertLoc)).createModel(g, ladder);
            } catch (ParserConfigurationException | IOException | SAXException | DeedPlannerException ex) {
                Log.err(ex);
            }
        }
        g.glCallList(listID);
    }
    
    private MeshData loadMesh(GL2 g, File location) throws ParserConfigurationException, IOException, SAXException, DeedPlannerException {
        MeshData data = new MeshData();
        
        Document doc = FileUtils.fileToXMLDoc(location);
        
        NodeList geometries = doc.getElementsByTagName("geometry");
        Node mesh = null;
        for (int i=0; i<geometries.getLength(); i++) {
            Element e = (Element) geometries.item(i);
            if (e.getAttribute("name").toUpperCase().contains(name)) {
                mesh = e.getElementsByTagName("mesh").item(0);
            }
        }
        if (mesh==null) {
            throw new DeedPlannerException("Invalid mesh!");
        }
        NodeList sources = mesh.getChildNodes();
        for (int i=0; i<sources.getLength(); i++) {
            if (sources.item(i).getNodeType()==Node.ELEMENT_NODE) {
                Element element = (Element) sources.item(i);
                switch (element.getNodeName()) {
                    case "source":
                        processSource(data, element);
                        break;
                    case "triangles":
                        processTriangles(data, element);
                        break;
                }
            }
        }
        
        return data;
    }
    
    private void processSource(MeshData data, Element source) {
        Element floatContainer = (Element) source.getElementsByTagName("float_array").item(0);
        int count = Integer.parseInt(floatContainer.getAttribute("count"));
        String floatsString = floatContainer.getTextContent();
        float[] floats = new float[count];
        Scanner scan = new Scanner(floatsString);
        scan.useLocale(Locale.US);
        int current = 0;
        while (scan.hasNextFloat()) {
            floats[current] = scan.nextFloat();
            current++;
        }
        String attribute = source.getAttribute("id").toUpperCase();
        if (attribute.contains("POSITION")) {
            data.setVertices(floats);
        }
        else if (attribute.contains("NORMAL")) {
            data.setNormals(floats);
        }
        else if (attribute.contains("UV")) {
            data.setTexcoords(floats);
        }
    }
    
    private void processTriangles(MeshData data, Element triangles) {
        Element intContainer = (Element) triangles.getElementsByTagName("p").item(0);
        int count = Integer.parseInt(triangles.getAttribute("count"));
        int[] ints = new int[count*9];
        String intsString = intContainer.getTextContent();
        Scanner scan = new Scanner(intsString);
        int current = 0;
        while (scan.hasNextFloat()) {
            ints[current] = scan.nextInt();
            current++;
        }
        data.setTriangles(ints);
    }
    
}