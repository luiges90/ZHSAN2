package com.zhsan.gamecomponents.common.textwidget;

import com.badlogic.gdx.graphics.Texture;

import java.util.Collection;
import java.util.Set;

/**
 * Created by Peter on 16/5/2015.
 */
public class RadioButtonWidget<ExtraType> extends CheckboxWidget<ExtraType> {

    private Collection<RadioButtonWidget<ExtraType>> radioGroup;

    public RadioButtonWidget(Setting setting, String text, Texture checkedImage, Texture uncheckedImage) {
        super(setting, text, checkedImage, uncheckedImage);
    }

    public void setGroup(Collection<RadioButtonWidget<ExtraType>> radioGroup) {
        this.radioGroup = radioGroup;
    }

    private static boolean clearingChecks = false;

    @Override
    public void setChecked(boolean checked) {
        if (clearingChecks) {
            super.setChecked(checked);
            return;
        }

        clearingChecks = true;
        for (RadioButtonWidget<?> buttons : radioGroup) {
            buttons.setChecked(false);
        }
        clearingChecks = false;

        super.setChecked(checked);
    }
}
