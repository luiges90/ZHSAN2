package com.zhsan.gamecomponents.common;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Disposable;
import com.zhsan.common.Fonts;
import com.zhsan.common.Utility;
import org.w3c.dom.Node;

/**
 * Created by Peter on 7/3/2015.
 */
public class TextWidget<ExtraType> extends Widget implements Disposable {

    public static enum VAlignment {
        TOP, CENTER
    }

    public static final class Setting {
        private String fontName;
        private int fontSize;
        private Fonts.Style fontStyle;
        private Color fontColor;
        public final BitmapFont.HAlignment align;

        private Setting(String fontName, int fontSize, Fonts.Style fontStyle, int fontColor, BitmapFont.HAlignment align) {
            this.fontName = fontName;
            this.fontSize = fontSize;
            this.fontStyle = fontStyle;
            this.fontColor = Utility.readColorFromXml(fontColor);
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
    private BitmapFont font;

    private String text;
    private VAlignment valign = VAlignment.CENTER;

    private ShapeRenderer shapeRenderer;
    private boolean selected = false;
    private Color selectedOutlineColor;

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

        font = Fonts.get(setting.fontName, setting.fontStyle);
        font.setColor(setting.fontColor);
        font.setScale((float) setting.fontSize / Fonts.SIZE);

        this.shapeRenderer = new ShapeRenderer();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Color getSelectedOutlineColor() {
        return selectedOutlineColor;
    }

    public void setSelectedOutlineColor(Color selectedOutlineColor) {
        this.selectedOutlineColor = selectedOutlineColor;
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
    public void draw(Batch batch, float parentAlpha) {
        validate();

        BitmapFont.TextBounds bounds = font.getWrappedBounds(text, getWidth());

        float y;
        if (getHeight() == 0 || valign == VAlignment.TOP) {
            y = getY() + getHeight() - bounds.height;
        } else {
            y = getY() + getHeight() / 2 + bounds.height / 2;
        }

        font.drawWrapped(batch, text, getX(), y, getWidth(), setting.align);

        if (selected) {
            batch.end();
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(selectedOutlineColor);
            shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
            shapeRenderer.end();
            batch.begin();
        }
    }

    public void dispose() {
        font.dispose();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}