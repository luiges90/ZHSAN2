package com.zhsan.resources;

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
 * Created by Peter on 6/3/2015.
 */
public class GlobalStrings {

    public static final String TITLE = "title";
    public static final String GAME_SURVEY_SAVE_HEADER = "gameSurveySaveHeader";
    public static final String TERRAIN_DETAIL_SAVE_HEADER = "terrainDetailSaveHeader";
    public static final String MAP_SAVE_HEADER = "mapSaveHeader";
    public static final String ARCHITECURE_KIND_SAVE_HEADER = "architectureKindSaveHeader";

    private GlobalStrings() {}

    private static Map<String, String> strings = null;

    private static void load() {
        FileHandle f = Gdx.files.external(Paths.RESOURCES + "GlobalStrings.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        strings = new HashMap<>();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            NodeList list = dom.getElementsByTagName("GlobalStrings");
            NamedNodeMap attributes = list.item(0).getAttributes();
            for (int i = 0; i < attributes.getLength(); ++i) {
                strings.put(attributes.item(i).getNodeName(), attributes.item(i).getNodeValue());
            }
        } catch (Exception e) {
            throw new FileReadException(Paths.RESOURCES + "GlobalStrings.xml", e);
        }
    }

    public static String getString(String key) {
        if (strings == null) load();
        return strings.get(key);
    }

}
