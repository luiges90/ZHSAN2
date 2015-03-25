package com.zhsan.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.common.Fonts;
import com.zhsan.gamecomponents.MapLayer;
import com.zhsan.gamecomponents.ToolBar;
import com.zhsan.gameobject.GameScenario;

/**
 * Created by Peter on 17/3/2015.
 */
public class GameScreen extends WidgetGroup {

    private GameScenario scen;

    private MapLayer mapLayer;

    public GameScreen(GameScenario scen) {
        this.scen = scen;

        mapLayer = new MapLayer(this);
        this.addActor(mapLayer);
    }

    public GameScenario getScenario() {
        return scen;
    }

    public void resize(int width, int height) {
        mapLayer.resize(width, height);
    }

    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void dispose() {
        mapLayer.dispose();
    }

}
