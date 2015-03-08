package com.zhsan.gamecomponents.common;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.zhsan.common.Fonts;
import org.w3c.dom.Node;

/**
 * Created by Peter on 7/3/2015.
 */
public final class TextWidget extends Widget {

    private final BitmapFont font;
    private final BitmapFont.HAlignment align;

    private String text;

    public TextWidget(String fontName, int fontSize, Fonts.Style fontStyle, int fontColor, BitmapFont.HAlignment align) {
        this.font = new BitmapFont(Fonts.get(fontName, fontStyle));

        Color temp = new Color();
        Color.argb8888ToColor(temp, fontColor);
        font.setColor(temp);
        font.setScale((float) fontSize / Fonts.SIZE);

        this.align = align;
    }

    public static TextWidget fromXml(Node node) {
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

        return new TextWidget(fontName, fontSize, fontStyle, fontColor, align);
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        BitmapFont.TextBounds bounds = font.getWrappedBounds(text, getWidth());
        font.drawWrapped(batch, text, getX(), getY() + getHeight() / 2 + bounds.height / 2, getWidth(), align);
    }
}
