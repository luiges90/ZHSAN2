package com.zhsan;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhsan.common.Fonts;
import com.zhsan.common.GlobalVariables;
import com.zhsan.gamecomponents.NewGameFrame;
import com.zhsan.gameobject.GameScenario;
import com.zhsan.resources.GlobalStrings;
import com.zhsan.screen.GameScreen;
import com.zhsan.screen.StartScreen;

import java.util.List;

public class ZHSan2 extends ApplicationAdapter {

    public static final int DEFAULT_WIDTH = 794;
    public static final int DEFAULT_HEIGHT = 594;

    private Stage startStage;
    private Stage gameStage;

    private StartScreen startScreen;
    private GameScreen gameScreen;

	@Override
	public void create () {
        Fonts.init();
        GlobalVariables.load();

        Gdx.graphics.setTitle(GlobalStrings.getString(GlobalStrings.TITLE));

        startScreen = new StartScreen(new OnNewScenarioSelected());

        Viewport viewport = new ScreenViewport();
        startStage = new Stage(viewport);
        startStage.addActor(startScreen);

        Gdx.input.setInputProcessor(startStage);
    }

    private class OnNewScenarioSelected implements NewGameFrame.OnScenarioChosenListener {

        @Override
        public void onScenarioChosen(String scenPath, List<Integer> factionIds) {
            Gdx.input.setInputProcessor(null);

            startScreen.dispose();
            startStage.dispose();
            startScreen = null;
            startStage = null;

            GameScenario scen = new GameScenario(scenPath);

            gameScreen = new GameScreen(scen);

            Viewport viewport = new ScreenViewport();
            gameStage = new Stage(viewport);
            gameStage.addActor(gameScreen);

            Gdx.input.setInputProcessor(gameStage);
        }
    }

    @Override
	public void render () {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (startStage != null) {
            startStage.act();
            startStage.draw();
        }
        if (gameStage != null) {
            gameStage.act();
            gameStage.draw();
        }
	}

    @Override
    public void resize(int width, int height) {
        if (startStage != null) {
            startStage.getViewport().update(width, height, true);
        }
        if (gameStage != null) {
            gameStage.getViewport().update(width, height, true);
            gameScreen.resize(width, height);
        }
    }

    @Override
    public void dispose() {
        if (startScreen != null) {
            startScreen.dispose();
        }
        if (startStage != null) {
            startStage.dispose();
        }
        if (gameScreen != null) {
            gameScreen.dispose();
        }
        if (gameStage != null) {
            gameStage.dispose();
        }
    }

}
