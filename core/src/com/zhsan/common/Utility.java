package com.zhsan.common;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 7/3/2015.
 */
public class Utility {
    private Utility() {}

    public static Rectangle loadRectangleFromXml(Node node) {
        Rectangle rect = new Rectangle();
        rect.setX(Integer.parseInt(node.getAttributes().getNamedItem("X").getNodeValue()));
        rect.setY(Integer.parseInt(node.getAttributes().getNamedItem("Y").getNodeValue()));
        rect.setWidth(Integer.parseInt(node.getAttributes().getNamedItem("Width").getNodeValue()));
        rect.setHeight(Integer.parseInt(node.getAttributes().getNamedItem("Height").getNodeValue()));
        return rect;
    }

    public static Color loadColorFromXml(int colorCode) {
        Color temp = new Color();
        Color.argb8888ToColor(temp, colorCode);
        return temp;
    }

    public static List<Integer> loadIntegerListFromXml(String s) {
        String[] split = s.split("\\s");
        List<Integer> result = new ArrayList<>();
        for (String i : split) {
            result.add(Integer.parseInt(i));
        }
        return result;
    }

    public static BitmapFont.HAlignment loadHAlignmentFromXML(Node node) {
        String alignStr = node.getAttributes().getNamedItem("Align").getNodeValue();
        BitmapFont.HAlignment align;
        if (alignStr.trim().equalsIgnoreCase("Middle")) {
            align = BitmapFont.HAlignment.CENTER;
        } else {
            align = BitmapFont.HAlignment.valueOf(alignStr.trim().toUpperCase());
        }
        return align;
    }

    public static Rectangle adjustRectangleByHAlignment(Rectangle in, BitmapFont.HAlignment align, float containerWidth) {
        Rectangle out = new Rectangle(in);
        switch (align) {
            case LEFT:
                break;
            case RIGHT:
                out.x = containerWidth - out.width - out.x;
                break;
            case CENTER:
                out.x = containerWidth / 2 - out.width / 2 + out.x;
                break;
        }
        return out;
    }

}
