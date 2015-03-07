package com.zhsan.gamecomponents.common;

/**
 * Created by Peter on 7/3/2015.
 */
public class TextElement {

    public static enum Alignment {
        LEFT, MIDDLE, RIGHT
    }

    public static enum Style {
        REGULAR, BOLD
    }

    private final String fontName;
    private final int fontSize;
    private final Style fontStyle;
    private final int fontColor;
    private final Alignment align;

    public TextElement(String fontName, int fontSize, Style fontStyle, int fontColor, Alignment align) {
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.fontStyle = fontStyle;
        this.fontColor = fontColor;
        this.align = align;
    }

    public String getFontName() {
        return fontName;
    }

    public int getFontSize() {
        return fontSize;
    }

    public Style getFontStyle() {
        return fontStyle;
    }

    public int getFontColor() {
        return fontColor;
    }

    public Alignment getAlign() {
        return align;
    }
}
