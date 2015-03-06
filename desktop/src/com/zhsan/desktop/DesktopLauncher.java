package com.zhsan.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.zhsan.ZHSan2;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "中華三國志";
        config.useGL30 = false;
        config.width = 1024;
        config.height = 720;
		new LwjglApplication(new ZHSan2(), config);
	}
}
