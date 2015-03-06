package com.zhsan;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.File;

public class ZHSan2 extends ApplicationAdapter {

    private GameController controller;
    private SpriteBatch batch;

    private Camera camera;
    private Viewport viewport;

	@Override
	public void create () {
		controller = new GameController();

        batch = new SpriteBatch();

        camera = new OrthographicCamera(800, 600);
        viewport = new ScreenViewport(camera);
    }

    @Override
	public void render () {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // This cryptic line clears the screen.

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
		controller.render(batch);
        batch.end();
	}

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        controller.dispose();
    }

}
