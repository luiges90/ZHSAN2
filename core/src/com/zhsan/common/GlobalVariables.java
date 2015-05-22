package com.zhsan.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.zhsan.common.Paths;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
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

    public static float scrollSpeed = 1.0f;
    public static boolean showGrid = true;

    public static int maxRunningDays = 99;

    public static void load() {
        FileHandle f = Gdx.files.external(Paths.DATA + "GlobalVariables.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            Node node = dom.getElementsByTagName("GlobalVariables").item(0);
            scrollSpeed = Float.parseFloat(XmlHelper.loadAttribute(node, "scrollSpeed"));
            showGrid = Boolean.parseBoolean(XmlHelper.loadAttribute(node, "showGrid"));
            maxRunningDays = Integer.parseInt(XmlHelper.loadAttribute(node, "maxRunningDays"));
        } catch (Exception e) {
            throw new FileReadException(Paths.DATA + "GlobalVariables.xml", e);
        }
    }


}
