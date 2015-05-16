package com.zhsan.gamelogic;

import com.zhsan.gamelogic.ai.FactionAI;
import com.zhsan.gameobject.Faction;
import com.zhsan.gameobject.GameScenario;

import java.util.*;

/**
 * Created by Peter on 14/5/2015.
 */
public final class GameController {

    private GameScenario scen;

    private List<FactionAI> factionAI;

    public GameController(GameScenario scen) {
        this.scen = scen;

        this.factionAI = new ArrayList<>();
        for (Faction f : scen.getFactions()) {
            if (f != scen.getGameData().getCurrentPlayer()) {
                this.factionAI.add(new FactionAI(f));
            }
        }
    }

    public void runDay() {
        Map<Faction, List<GameOrder>> orders = new HashMap<>();

        factionAI.forEach(com.zhsan.gamelogic.ai.FactionAI::makeOrder);

        // run orders
    }


}
