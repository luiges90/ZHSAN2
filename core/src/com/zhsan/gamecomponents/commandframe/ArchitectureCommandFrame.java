package com.zhsan.gamecomponents.commandframe;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.zhsan.common.Paths;
import com.zhsan.screen.GameScreen;

import java.io.File;

/**
 * Created by Peter on 2/6/2015.
 */
public class ArchitectureCommandFrame extends CommandFrame {

    public static final String RES_PATH = CommandFrame.RES_PATH + "Architecture" + File.separator;
    public static final String DATA_PATH = RES_PATH + "Data" + File.separator;

    private GameScreen screen;

    public ArchitectureCommandFrame(GameScreen screen) {
        super();

        this.screen = screen;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void dispose() {
        super.dispose();
    }

}
