package com.zhsan.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.zhsan.common.exception.XmlException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Peter on 7/3/2015.
 */
public class Fonts {

    public static final String SYSTEM = "System";

    private Fonts() {}

    private static Map<String, String> fonts = new HashMap<>();
    private static Map<String, BitmapFont> files = new HashMap<>();

    private static void loadFontDefinition() {
        FileHandle f = Gdx.files.external(Paths.FONTS + "Fonts.xml");

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(f.read());

            NodeList list = dom.getElementsByTagName("Font");
            for (int i = 0; i < list.getLength(); ++i) {
                Node n = list.item(i);
                fonts.put(n.getAttributes().getNamedItem("name").getNodeValue(),
                        n.getAttributes().getNamedItem("file").getNodeValue());
            }
        } catch (Exception e) {
            throw new XmlException(Paths.FONTS + "Fonts.xml", e);
        }
    }

    private static void loadFonts() {
        for (String s : fonts.values()) {
            if (files.containsKey(s)) continue;

            FileHandle f = Gdx.files.external(Paths.FONTS + s);
            BitmapFont font = new BitmapFont(f);
            files.put(s, font);
        }
    }

    public static void init() {
        loadFontDefinition();
        loadFonts();
    }

    public static BitmapFont get(){
        return get(SYSTEM);
    }

    public static BitmapFont get(String name) {
        return files.get(fonts.get(name));
    }

    public static void dispose() {
        for (BitmapFont bf : files.values()) {
            bf.dispose();
        }
    }

}
