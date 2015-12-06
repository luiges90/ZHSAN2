package com.zhsan.gamecomponents.toolbar;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.zhsan.gamecomponents.common.StateTexture;
import com.zhsan.screen.GameScreen;

import java.io.File;

/**
 * Created by Peter on 6/12/2015.
 */
public class GameRecord extends WidgetGroup {

    public static final String RES_PATH = ToolBar.RES_PATH + "GameRecord" + File.separator;
    public static final String DATA_PATH = RES_PATH + "Data" + File.separator;

    private GameScreen screen;

    private BitmapFont.HAlignment hAlignment;


    public GameRecord(GameScreen screen, BitmapFont.HAlignment hAlignment, int toolbarSize) {
        this.screen = screen;
    }

    public final void resize(int width, int height) {

    }

    public void dispose() {

    }
}
