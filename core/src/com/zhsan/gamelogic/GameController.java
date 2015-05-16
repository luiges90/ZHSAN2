package com.zhsan.gamelogic;

import com.zhsan.gamelogic.ai.FactionAI;
import com.zhsan.gameobject.Faction;
import com.zhsan.gameobject.GameScenario;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Peter on 14/5/2015.
 */
public final class GameController {

    private GameScenario scen;

    private Set<FactionAI> factionAI;

    public GameController(GameScenario scen) {
        this.scen = scen;

        this.factionAI = new HashSet<>();
        for (Faction f : scen.getFactions()) {
            if (f != scen.getGameData().getCurrentPlayer()) {
                this.factionAI.add(new FactionAI(f));
            }
        }
    }

    public void run(Set<? extends GameOrder> playerOrders) {
        Map<Faction, Set<? extends GameOrder>> orders = new HashMap<>();
        orders.put(scen.getGameData().getCurrentPlayer(), playerOrders);

        for (FactionAI f : factionAI) {
            orders.put(f.getFaction(), f.makeOrder());
        }

        // run orders
    }


}
