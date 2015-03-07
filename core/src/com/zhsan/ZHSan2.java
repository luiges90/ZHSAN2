package com.zhsan;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhsan.resources.GlobalStrings;
import com.zhsan.start.StartScreen;

public class ZHSan2 extends ApplicationAdapter {

    public static enum GameState {
        START,
    }

    public static final int DEFAULT_WIDTH = 1024;
    public static final int DEFAULT_HEIGHT = 600;

    private GameState state = GameState.START;

    private StartScreen startScreen = null;

    private SpriteBatch batch;

    private Camera camera;
    private Viewport viewport;

	@Override
	public void create () {
        batch = new SpriteBatch();

        camera = new OrthographicCamera(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        viewport = new ScreenViewport(camera);

        Gdx.graphics.setTitle(GlobalStrings.getString(GlobalStrings.TITLE));
    }

    @Override
	public void render () {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // This cryptic line clears the screen.

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        switch (state) {
            case START:
                if (startScreen == null) {
                    startScreen = new StartScreen();
                    Gdx.input.setInputProcessor(startScreen.getInputProcessor());
                }
                startScreen.render(batch);
                break;
        }

        batch.end();
	}

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();

        if (startScreen != null) {
            startScreen.dispose();
        }
    }

}
