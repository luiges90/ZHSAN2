package com.zhsan.start;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zhsan.ZHSan2;

import java.io.File;

/**
 * Created by Peter on 6/3/2015.
 */
public class StartScreen {

    private SpriteBatch batch;
    private Texture txStart;

    public StartScreen() {
        batch = new SpriteBatch();
        txStart = new Texture(Gdx.files.external(ZHSan2.RESOURCES + "Start" + File.separator + "Start.jpg"));
    }

    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // This cryptic line clears the screen.
        batch.begin();
        batch.draw(txStart, 0, 0);
        batch.end();
    }

    public void dispose() {

    }

}
