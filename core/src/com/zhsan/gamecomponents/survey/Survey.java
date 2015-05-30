package com.zhsan.gamecomponents.survey;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.Paths;
import com.zhsan.common.Point;
import com.zhsan.screen.GameScreen;

import java.io.File;

/**
 * Created by Peter on 30/5/2015.
 */
public abstract class Survey extends WidgetGroup {

    public static final String RES_PATH = Paths.RESOURCES + "Survey" + File.separator;

    private boolean visible;

    protected abstract void drawContent(Batch batch, float parentAlpha);

    @Override
    public final void draw(Batch batch, float parentAlpha) {
        if (visible) {
            super.draw(batch, parentAlpha);

            drawContent(batch, parentAlpha);
        }
    }

    public void show(Point location) {
        if (location.x + this.getWidth() < Gdx.graphics.getWidth() && location.y + this.getHeight() < Gdx.graphics.getHeight()) {
            this.setPosition(location.x, location.y);
        } else if (location.x + this.getWidth() < Gdx.graphics.getWidth() && location.y + this.getHeight() >= Gdx.graphics.getHeight()) {
            this.setPosition(location.x, location.y - this.getHeight());
        } else if (location.x + this.getWidth() >= Gdx.graphics.getWidth() && location.y + this.getHeight() < Gdx.graphics.getHeight()) {
            this.setPosition(location.x - this.getWidth(), location.y);
        } else if (location.x + this.getWidth() >= Gdx.graphics.getWidth() && location.y + this.getHeight() >= Gdx.graphics.getHeight()) {
            this.setPosition(location.x - this.getWidth(), location.y - this.getHeight());
        }

        this.visible = true;
    }

    public void hide() {
        this.visible = false;
    }

}
