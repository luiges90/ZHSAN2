package com.zhsan.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.zhsan.common.exception.XmlException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Peter on 7/3/2015.
 */
public class Fonts {

    public static final String SYSTEM = "System";

    public static enum Style {
        REGULAR, BOLD
    }

    private Fonts() {}

    private static Map<String, EnumMap<Style, String>> fonts = new HashMap<>();
    private static Map<String, BitmapFont> fontFiles = new HashMap<>();

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

                EnumMap<Style, String> styles = new EnumMap<>(Style.class);
                styles.put(Style.REGULAR, n.getAttributes().getNamedItem("regular").getNodeValue());
                styles.put(Style.BOLD, n.getAttributes().getNamedItem("bold").getNodeValue());
                fonts.put(n.getAttributes().getNamedItem("name").getNodeValue(), styles);
            }
        } catch (Exception e) {
            throw new XmlException(Paths.FONTS + "Fonts.xml", e);
        }
    }

    private static void loadFonts() {
        fonts.forEach((name, styles) -> {
            if (!fontFiles.containsKey(name)) {
                styles.forEach((style, file) -> {
                    FileHandle f = Gdx.files.external(Paths.FONTS + file);
                    BitmapFont font = new BitmapFont(f);
                    fontFiles.put(file, font);
                });
            }
        });
    }

    public static void init() {
        loadFontDefinition();
        loadFonts();
    }

    public static BitmapFont get(){
        return get(SYSTEM, Style.REGULAR);
    }

    public static BitmapFont get(String name, Style style) {
        return fontFiles.get(fonts.get(name).get(style));
    }

    public static void dispose() {
        fontFiles.values().forEach(BitmapFont::dispose);
    }

}
