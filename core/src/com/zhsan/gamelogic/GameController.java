package com.zhsan.gamelogic;

import com.zhsan.gameobject.Faction;
import com.zhsan.gameobject.GameScenario;

/**
 * Created by Peter on 14/5/2015.
 */
public final class GameController {

    private GameScenario scen;

    public GameController(GameScenario scen) {
        this.scen = scen;
    }

    public void run() {
        for (Faction f : scen.getFactions()) {
            if (f == scen.getGameData().getCurrentPlayer()) {
                return;
            } else {

            }
        }

    }


}
