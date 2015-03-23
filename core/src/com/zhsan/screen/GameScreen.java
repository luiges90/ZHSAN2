package com.zhsan.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.gamecomponents.MapLayer;
import com.zhsan.gameobject.GameScenario;

/**
 * Created by Peter on 17/3/2015.
 */
public class GameScreen extends WidgetGroup {

    private GameScenario scen;
    private MapLayer mapLayer;

    public GameScreen(GameScenario scen) {
        this.scen = scen;

        this.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        mapLayer = new MapLayer(this);
        mapLayer.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.addActor(mapLayer);
    }

    public GameScenario getScenario() {
        return scen;
    }

    public void resize(int width, int height) {
        mapLayer.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        mapLayer.resize();
    }

    public void draw(Batch batch, float parentAlpha) {
        this.drawChildren(batch, parentAlpha);
    }

    public void dispose() {
        mapLayer.dispose();
    }

}
