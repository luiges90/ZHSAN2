package com.zhsan.gamecomponents.common;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Peter on 7/3/2015.
 */
public class TextBackgroundElement extends TextElement {

    private final Texture background;
    private final int width;
    private final int height;

    public TextBackgroundElement(String fontName, int fontSize, Style fontStyle, int fontColor, Alignment align,
                                 Texture background, int width, int height) {
        super(fontName, fontSize, fontStyle, fontColor, align);
        this.background = background;
        this.width = width;
        this.height = height;
    }
}
