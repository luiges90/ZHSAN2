package com.zhsan.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.zhsan.common.Paths;
import com.zhsan.common.exception.FileReadException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Peter on 22/3/2015.
 */
public class GlobalVariables {

    private GlobalVariables() {}

    public static float scrollSpeed;
    public static boolean showGrid;

    public static void load() {
        FileHandle f = Gdx.files.external(Paths.DATA + "GlobalVariables.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            NodeList list = dom.getElementsByTagName("GlobalVariables");
            NamedNodeMap attributes = list.item(0).getAttributes();
            scrollSpeed = Float.parseFloat(attributes.getNamedItem("scrollSpeed").getNodeValue());
            showGrid = Boolean.parseBoolean(attributes.getNamedItem("showGrid").getNodeValue());
        } catch (Exception e) {
            throw new FileReadException(Paths.DATA + "GlobalVariables.xml", e);
        }
    }


}
