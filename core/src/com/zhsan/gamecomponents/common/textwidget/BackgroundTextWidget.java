package com.zhsan.gamecomponents.common.textwidget;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.zhsan.gamecomponents.common.StateTexture;

/**
 * Created by Peter on 1/4/2015.
 */
public class BackgroundTextWidget<ExtraType> extends TextWidget<ExtraType> {

    private Texture background;

    public BackgroundTextWidget(TextWidget<ExtraType> template, Texture background) {
        super(template);
        this.background = background;
    }

    public Texture getBackground() {
        return background;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(background, getX(), getY(), getWidth(), getHeight());
        super.draw(batch, parentAlpha);
    }

    @Override
    public void dispose() {
        super.dispose();
        background.dispose();
    }
}
