package com.zhsan.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.zhsan.ZHSan2;

public class DesktopLauncher {
	public static void main (String[] arg) {
        System.setProperty("user.home", ".");

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "";
        config.useGL30 = false;
        config.width = 800;
        config.height = 600;
		new LwjglApplication(new ZHSan2(), config);
	}
}
