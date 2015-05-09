package com.zhsan.gamecomponents.common.textwidget;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.zhsan.gamecomponents.common.StateTexture;

/**
 * Created by Peter on 1/4/2015.
 */
public class StateBackgroundTextWidget<ExtraType> extends TextWidget<ExtraType> {

    private StateTexture background;

    public StateBackgroundTextWidget(TextWidget<ExtraType> template, StateTexture background) {
        super(template);
        this.background = new StateTexture(background);
    }

    public StateTexture getBackground() {
        return background;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(background.get(), getX(), getY(), getWidth(), getHeight());
        super.draw(batch, parentAlpha);
    }

    @Override
    public void dispose() {
        super.dispose();
        background.dispose();
    }
}
