package com.zhsan.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.gamecomponents.MapLayer;
import com.zhsan.gamecomponents.ToolBar;
import com.zhsan.gameobject.GameScenario;

/**
 * Created by Peter on 17/3/2015.
 */
public class GameScreen extends WidgetGroup {

    private GameScenario scen;

    private ToolBar toolBar;
    private MapLayer mapLayer;

    public GameScreen(GameScenario scen) {
        this.scen = scen;

        this.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        toolBar = new ToolBar(this);
        toolBar.setPosition(0, 0);
        toolBar.setWidth(Gdx.graphics.getWidth());

        mapLayer = new MapLayer(this);
        mapLayer.setPosition(0, toolBar.getHeight());
        mapLayer.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - toolBar.getHeight());
        this.addActor(mapLayer);

        this.addActor(toolBar);
    }

    public GameScenario getScenario() {
        return scen;
    }

    public void resize(int width, int height) {
        toolBar.setWidth(width);
        toolBar.resize();

        mapLayer.setSize(width, height - toolBar.getHeight());
        mapLayer.resize();
    }

    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void dispose() {
        toolBar.dispose();
        mapLayer.dispose();
    }

}
