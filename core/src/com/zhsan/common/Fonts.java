package com.zhsan.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.io.File;

/**
 * Created by Peter on 7/3/2015.
 */
public class Fonts {

    private Fonts() {}

    public static BitmapFont disu;

    public static void init() {
        FileHandle fhDisu = Gdx.files.external(Paths.FONTS + "DISU.fnt");
        disu = new BitmapFont(fhDisu);
    }

    public static void dispose() {
        disu.dispose();
    }

}
