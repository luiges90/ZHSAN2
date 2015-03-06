package com.zhsan.start;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zhsan.common.Paths;

import java.io.File;

/**
 * Created by Peter on 6/3/2015.
 */
public class StartScreen {

    private Texture txStart;

    public StartScreen() {
        txStart = new Texture(Gdx.files.external(Paths.RESOURCES + "Start" + File.separator + "Start.jpg"));
    }

    public void render(SpriteBatch batch) {
        batch.draw(txStart, -txStart.getWidth() / 2, -txStart.getHeight() / 2);
    }

    public void dispose() {
        txStart.dispose();
    }

}
