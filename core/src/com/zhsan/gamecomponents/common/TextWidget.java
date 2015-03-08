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
public class TextWidget extends Widget {

    public static final class Setting {
        public final BitmapFont font;
        public final BitmapFont.HAlignment align;

        private Setting(String fontName, int fontSize, Fonts.Style fontStyle, int fontColor, BitmapFont.HAlignment align) {
            this.font = new BitmapFont(Fonts.get(fontName, fontStyle));

            Color temp = new Color();
            Color.argb8888ToColor(temp, fontColor);
            font.setColor(temp);
            font.setScale((float) fontSize / Fonts.SIZE);

            this.align = align;
        }

        public static Setting fromXml(Node node) {
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

            return new Setting(fontName, fontSize, fontStyle, fontColor, align);
        }
    }

    private Setting setting;
    private String text;

    public TextWidget(Setting setting) {
        this(setting, "");
    }

    public TextWidget(Setting setting, String text) {
        this.setting = setting;
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        BitmapFont.TextBounds bounds = setting.font.getWrappedBounds(text, getWidth());

        float y;
        if (getHeight() == 0) {
            y = getY() - bounds.height;
        } else {
            y = getY() + getHeight() / 2 + bounds.height / 2;
        }

        setting.font.drawWrapped(batch, text, getX(), y, getWidth(), setting.align);
    }
}
