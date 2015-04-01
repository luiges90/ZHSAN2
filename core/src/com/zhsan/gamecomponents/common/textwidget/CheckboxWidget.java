package com.zhsan.gamecomponents.common.textwidget;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.zhsan.gamecomponents.common.textwidget.TextWidget;

/**
 * Created by Peter on 15/3/2015.
 */
public class CheckboxWidget<ExtraType> extends TextWidget<ExtraType> {

    private Texture checkedImage, uncheckedImage;
    private boolean checked = false;

    public CheckboxWidget(Setting setting, String text, Texture checkedImage, Texture uncheckedImage) {
        super(setting, VAlignment.CENTER, text);
        this.checkedImage = checkedImage;
        this.uncheckedImage = uncheckedImage;
        this.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                checked = !checked;
                return false;
            }
        });
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    protected float getTextX() {
        return super.getTextX() + checkedImage.getWidth();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        float checkboxWidth = checkedImage.getWidth() * (getHeight() / checkedImage.getHeight());
        if (checked) {
            batch.draw(checkedImage, getX(), getY(), checkboxWidth, getHeight());
        } else {
            batch.draw(uncheckedImage, getX(), getY(), checkboxWidth, getHeight());
        }
    }

}
