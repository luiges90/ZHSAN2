package com.zhsan.gamecomponents.common.textwidget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Disposable;
import com.zhsan.common.Fonts;
import com.zhsan.gamecomponents.common.XmlHelper;
import org.w3c.dom.Node;

/**
 * Created by Peter on 7/3/2015.
 */
public class TextWidget<ExtraType> extends Widget implements Disposable {

    public static enum VAlignment {
        TOP, CENTER
    }

    public static final class Setting {
        private final String fontName;
        private final int fontSize;
        private final Fonts.Style fontStyle;
        private final Color fontColor;
        public final BitmapFont.HAlignment align;

        private Setting(String fontName, int fontSize, Fonts.Style fontStyle, int fontColor, BitmapFont.HAlignment align) {
            this.fontName = fontName;
            this.fontSize = fontSize;
            this.fontStyle = fontStyle;
            this.fontColor = XmlHelper.loadColorFromXml(fontColor);
            this.align = align;
        }

        public static Setting fromXml(Node node) {
            String fontName = XmlHelper.loadAttribute(node, "FontName");
            int fontSize = Integer.parseInt(XmlHelper.loadAttribute(node, "FontSize"));
            String fontStyleStr = XmlHelper.loadAttribute(node, "FontStyle");
            int fontColor = Integer.parseUnsignedInt(XmlHelper.loadAttribute(node, "FontColor"));

            Fonts.Style fontStyle = Fonts.Style.valueOf(fontStyleStr.trim().toUpperCase());

            BitmapFont.HAlignment align = XmlHelper.loadHAlignmentFromXml(node);

            return new Setting(fontName, fontSize, fontStyle, fontColor, align);
        }
    }

    private Setting setting;
    private BitmapFont font;
    private int padding = 0;

    private String text;
    private VAlignment valign = VAlignment.CENTER;

    private ExtraType extra;

    public TextWidget(Setting setting) {
        this(setting, "");
    }

    public TextWidget(Setting setting, String text) {
        this(setting, VAlignment.CENTER, text);
    }

    public TextWidget(Setting setting, VAlignment valign, String text) {
        this.setting = setting;
        this.valign = valign;
        this.text = text;

        BitmapFont ref = Fonts.get(setting.fontName, setting.fontStyle);
        font = new BitmapFont(ref.getData(), ref.getRegions(), ref.usesIntegerPositions());
        font.setColor(setting.fontColor);
        font.setScale((float) setting.fontSize / Fonts.SIZE);
    }

    public TextWidget(TextWidget<ExtraType> old) {
        this.setting = old.setting;
        this.padding = old.padding;
        this.text = old.text;
        this.valign = old.valign;
        this.extra = old.extra;

        BitmapFont ref = Fonts.get(setting.fontName, setting.fontStyle);
        font = new BitmapFont(ref.getData(), ref.getRegions(), ref.usesIntegerPositions());
        font.setColor(setting.fontColor);
        font.setScale((float) setting.fontSize / Fonts.SIZE);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public ExtraType getExtra() {
        return extra;
    }

    public void setExtra(ExtraType extra) {
        this.extra = extra;
    }

    /**
     * Compute minimum height to show the text completely
     * @param width
     */
    public float computeNeededHeight(float width) {
        BitmapFont.TextBounds bounds = font.getWrappedBounds(text, width);
        return bounds.height;
    }

    @Override
    public float getPrefWidth() {
        return getWidth();
    }

    @Override
    public float getPrefHeight() {
        return computeNeededHeight(getWidth()) + padding;
    }

    protected float getTextX() {
        return getX();
    }

    protected float getTextY() {
        BitmapFont.TextBounds bounds = font.getWrappedBounds(text, getWidth());
        float y;
        if (getHeight() == 0) {
            y = getY() + bounds.height;
        } else if (valign == VAlignment.TOP) {
            y = getY() + getHeight() - bounds.height;
        } else {
            y = getY() + getHeight() / 2 + bounds.height / 2;
        }
        return y;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        validate();

        font.drawWrapped(batch, text, getTextX(), getTextY(), getWidth(), setting.align);
    }

    public void dispose() {
        font.dispose();
    }
}
