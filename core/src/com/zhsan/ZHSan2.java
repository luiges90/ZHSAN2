package com.zhsan;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhsan.common.Fonts;
import com.zhsan.resources.GlobalStrings;
import com.zhsan.start.StartScreen;

import java.io.IOException;
import java.io.Writer;

public class ZHSan2 extends ApplicationAdapter {

    public static final int DEFAULT_WIDTH = 1024;
    public static final int DEFAULT_HEIGHT = 600;

    private Stage startStage;

    private StartScreen startScreen;

	@Override
	public void create () {
        Fonts.init();

        Gdx.graphics.setTitle(GlobalStrings.getString(GlobalStrings.TITLE));

        startStage = new Stage(new ScreenViewport());

        startScreen = new StartScreen();
        startStage.addActor(startScreen);

        Gdx.input.setInputProcessor(startStage);
    }

    @Override
	public void render () {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        startStage.act();
        startStage.draw();
	}

    @Override
    public void resize(int width, int height) {
        startStage.getViewport().update(width, height, true);

        MoveToAction move = new MoveToAction();
        move.setPosition(Gdx.graphics.getWidth() / 2 - startScreen.getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - startScreen.getHeight() / 2);
        move.setDuration(0);
        startStage.addAction(move);
    }

    @Override
    public void dispose() {
        startScreen.dispose();

        startStage.dispose();
    }

}
