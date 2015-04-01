package com.zhsan.gamecomponents.common;

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
    public void dispose() {
        super.dispose();
        background.dispose();
    }
}
