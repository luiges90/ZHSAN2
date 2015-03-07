package com.zhsan.gamecomponents.common;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.zhsan.common.Fonts;
import org.w3c.dom.Node;

/**
 * Created by Peter on 7/3/2015.
 */
public final class TextElement {

    private final BitmapFont font;
    private final int fontSize;
    private final Color fontColor;
    private final BitmapFont.HAlignment align;

    public TextElement(String fontName, int fontSize, Fonts.Style fontStyle, int fontColor, BitmapFont.HAlignment align) {
        this.font = Fonts.get(fontName, fontStyle);
        this.fontSize = fontSize;

        Color temp = new Color();
        Color.argb8888ToColor(temp, fontColor);
        this.fontColor = temp;

        this.align = align;
    }

    public static TextElement fromXml(Node node) {
        String fontName = node.getAttributes().getNamedItem("FontName").getNodeValue();
        int fontSize = Integer.parseInt(node.getAttributes().getNamedItem("FontSize").getNodeValue());
        String fontStyleStr = node.getAttributes().getNamedItem("FontStyle").getNodeValue();
        int fontColor = Integer.parseUnsignedInt(node.getAttributes().getNamedItem("FontColor").getNodeValue());
        String alignStr = node.getAttributes().getNamedItem("Align").getNodeValue();

        Fonts.Style fontStyle = Fonts.Style.valueOf(fontStyleStr.trim().toUpperCase());
        BitmapFont.HAlignment align;
        if (alignStr.trim().equalsIgnoreCase("Middle")) {
            align = BitmapFont.HAlignment.CENTER;
        } else {
            align = BitmapFont.HAlignment.valueOf(alignStr.trim().toUpperCase());
        }

        return new TextElement(fontName, fontSize, fontStyle, fontColor, align);
    }

    public BitmapFont getFont() {
        return font;
    }

    public void applyColorSize() {
        font.setColor(fontColor);
        font.setScale((float) fontSize / Fonts.SIZE);
    }

    public BitmapFont.HAlignment getAlign() {
        return align;
    }
}
