package com.zhsan.start;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.Fonts;
import com.zhsan.gamecomponents.GameFrame;
import com.zhsan.resources.GlobalStrings;

/**
 * Created by Peter on 7/3/2015.
 */
public class NewGameScreen extends WidgetGroup {

    private GameFrame frame;

    public NewGameScreen() {
        frame = new GameFrame(GlobalStrings.getString(GlobalStrings.NEW_GAME), new GameFrame.OnClick() {
            @Override
            public void onOkClicked() {

            }

            @Override
            public void onCancelClicked() {
                NewGameScreen.this.setVisible(false);
            }
        });
        addActor(frame);
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        frame.setWidth(this.getWidth());
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        frame.setHeight(this.getHeight());
    }

    public void draw(Batch batch, float parentAlpha) {
        drawChildren(batch, parentAlpha);
    }

    public void dispose(){
        frame.dispose();
    }

}
