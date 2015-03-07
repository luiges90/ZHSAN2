package com.zhsan.gamecomponents.common;

import com.zhsan.common.Fonts;

/**
 * Created by Peter on 7/3/2015.
 */
public class TextElement {

    public static enum Alignment {
        LEFT, MIDDLE, RIGHT
    }

    private final String fontName;
    private final int fontSize;
    private final Fonts.Style fontStyle;
    private final int fontColor;
    private final Alignment align;

    public TextElement(String fontName, int fontSize, Fonts.Style fontStyle, int fontColor, Alignment align) {
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

    public Fonts.Style getFontStyle() {
        return fontStyle;
    }

    public int getFontColor() {
        return fontColor;
    }

    public Alignment getAlign() {
        return align;
    }
}
