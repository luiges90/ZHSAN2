package com.zhsan.gamecomponents.common;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.zhsan.common.exception.FileReadException;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Peter on 31/3/2015.
 */
public class XmlHelper {

    public static Rectangle loadRectangleFromXml(Node node) {
        Rectangle rect = new Rectangle();
        rect.setX(Integer.parseInt(XmlHelper.loadAttribute(node, "X")));
        rect.setY(Integer.parseInt(XmlHelper.loadAttribute(node, "Y")));
        rect.setWidth(Integer.parseInt(XmlHelper.loadAttribute(node, "Width")));
        rect.setHeight(Integer.parseInt(XmlHelper.loadAttribute(node, "Height")));
        return rect;
    }

    public static Rectangle loadRectangleFromXmlOptSize(Node node) {
        Rectangle rect = new Rectangle();
        rect.setX(Integer.parseInt(XmlHelper.loadAttribute(node, "X")));
        rect.setY(Integer.parseInt(XmlHelper.loadAttribute(node, "Y")));
        rect.setWidth(Integer.parseInt(XmlHelper.loadAttribute(node, "Width", "0")));
        rect.setHeight(Integer.parseInt(XmlHelper.loadAttribute(node, "Height", "0")));
        return rect;
    }

    public static Color loadColorFromXml(int colorCode) {
        Color temp = new Color();
        Color.argb8888ToColor(temp, colorCode);
        return temp;
    }

    public static String saveColorToXml(Color color) {
        return Integer.toUnsignedString(Color.argb8888(color));
    }

    public static List<Integer> loadIntegerListFromXml(String s) {
        String[] split = s.split("\\s");
        List<Integer> result = new ArrayList<>();
        for (String i : split) {
            if (i.isEmpty()) continue;
            result.add(Integer.parseInt(i));
        }
        return result;
    }

    public static String saveIntegerListToXml(Collection<Integer> list) {
        return list.stream().map(String::valueOf).collect(Collectors.joining(" "));
    }

    public static BitmapFont.HAlignment loadHAlignmentFromXml(Node node, BitmapFont.HAlignment def) {
        Node node1 = node.getAttributes().getNamedItem("Align");
        if (node1 == null) {
            return def;
        }
        String alignStr = node1.getNodeValue();
        BitmapFont.HAlignment align;
        if (alignStr.trim().equalsIgnoreCase("Middle")) {
            align = BitmapFont.HAlignment.CENTER;
        } else {
            align = BitmapFont.HAlignment.valueOf(alignStr.trim().toUpperCase());
        }
        return align;
    }

    public static BitmapFont.HAlignment loadHAlignmentFromXml(Node node) {
        String alignStr = node.getAttributes().getNamedItem("Align").getNodeValue();
        BitmapFont.HAlignment align;
        if (alignStr.trim().equalsIgnoreCase("Middle")) {
            align = BitmapFont.HAlignment.CENTER;
        } else {
            align = BitmapFont.HAlignment.valueOf(alignStr.trim().toUpperCase());
        }
        return align;
    }

    public static String loadAttribute(Node node, String attribute) {
        Node n = node.getAttributes().getNamedItem(attribute);
        if (n == null) {
            throw new FileReadException("Node " + node + " has no attribute " + attribute);
        }
        return n.getNodeValue();
    }

    public static String loadAttribute(Node node, String attribute, String defaultValue) {
        Node n = node.getAttributes().getNamedItem(attribute);
        if (n == null) {
            return defaultValue;
        }
        return n.getNodeValue();
    }

}
