package com.zhsan.gamecomponents.common;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

/**
 * Created by Peter on 8/3/2015.
 */
public class TextWidget extends Widget {

    private TextElement element;
    private String text;

    public TextWidget(TextElement element, String text) {
        this.element = element;
        this.text = text;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        element.applyColorSize();
        BitmapFont.TextBounds bounds = element.getFont().getWrappedBounds(text, getWidth());
        element.getFont().drawWrapped(batch, text, 0, getHeight() / 2 - bounds.height / 2,
                getWidth(), element.getAlign());
    }
}
