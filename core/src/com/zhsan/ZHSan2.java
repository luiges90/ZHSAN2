package com.zhsan;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.io.File;

public class ZHSan2 extends ApplicationAdapter {

    public static final String FILE_ROOT = "ZHSAN2";

    public static final String RESOURCES = FILE_ROOT + File.separator + "Resources" + File.separator;

    private GameController controller;

	@Override
	public void create () {
		controller = new GameController();
    }

    @Override
	public void render () {
		controller.render();
	}

    @Override
    public void dispose() {
        controller.dispose();
    }
}
