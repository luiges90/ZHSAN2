package com.zhsan.screen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.gameobject.GameScenario;

/**
 * Created by Peter on 17/3/2015.
 */
public class GameScreen extends WidgetGroup {

    private GameScenario scen;

    public GameScreen(GameScenario scen) {
        this.scen = scen;


    }

    public void draw(Batch batch, float parentAlpha) {

    }

    public void dispose() {

    }

}
