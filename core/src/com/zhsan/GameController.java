package com.zhsan;

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

    public void render() {
        switch (state) {
            case START:
                if (startScreen == null) {
                    startScreen = new StartScreen();
                }
                startScreen.render();
                break;
        }
    }

    public void dispose() {
        if (startScreen != null) {
            startScreen.dispose();
        }
    }
}
