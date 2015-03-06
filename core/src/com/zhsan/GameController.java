package com.zhsan;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zhsan.start.StartScreen;

/**
 * Created by Peter on 6/3/2015.
 */
public class GameController {

    private GameState state = GameState.START;

    private StartScreen startScreen = null;

    public GameController(){

    }

    public void setGameState(GameState state) {
        this.state = state;
    }

    public void render(SpriteBatch batch) {
        switch (state) {
            case START:
                if (startScreen == null) {
                    startScreen = new StartScreen();
                }
                startScreen.render(batch);
                break;
        }
    }

    public void dispose() {
        if (startScreen != null) {
            startScreen.dispose();
        }
    }
}
