package com.zhsan.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.zhsan.ZHSan2;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DesktopLauncher {
	public static void main (String[] arg) {
        System.setProperty("user.home", ".");

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.err.println("Uncaught exception on thread: " + t.getName());
                e.printStackTrace(System.err);

                String date = new SimpleDateFormat("yyyy-MM-dd HHmmss").format(Calendar.getInstance().getTime());
                try (PrintWriter w = new PrintWriter(new FileWriter("Crash_" + date + ".log"))) {
                    w.println("Uncaught exception on thread: " + t.getName());
                    e.printStackTrace(w);
                } catch (Exception e2) {

                }
            }
        });

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "";
        config.useGL30 = false;
        config.width = ZHSan2.DEFAULT_WIDTH;
        config.height = ZHSan2.DEFAULT_HEIGHT;
		new LwjglApplication(new ZHSan2(), config);
	}
}
