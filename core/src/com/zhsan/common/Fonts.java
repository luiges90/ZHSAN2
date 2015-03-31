package com.zhsan.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.gamecomponents.common.XmlHelper;
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

    public static final int SIZE = 32;

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
                styles.put(Style.REGULAR, XmlHelper.loadAttribute(n, "regular"));
                styles.put(Style.BOLD, XmlHelper.loadAttribute(n, "bold"));
                fonts.put(XmlHelper.loadAttribute(n, "xmlName"), styles);
            }
        } catch (Exception e) {
            throw new FileReadException(Paths.FONTS + "Fonts.xml", e);
        }
    }

    private static void loadFonts() {
        fonts.forEach((name, styles) -> {
            if (!fontFiles.containsKey(name)) {
                styles.forEach((style, file) -> {
                    FileHandle f = Gdx.files.external(Paths.FONTS + file);
                    fontFiles.put(file, new BitmapFont(f));
                });
            }
        });
    }

    public static void init() {
        loadFontDefinition();
        loadFonts();
    }

    public static BitmapFont get(String name, Style style) {
        return fontFiles.get(fonts.get(name).get(style));
    }

}
