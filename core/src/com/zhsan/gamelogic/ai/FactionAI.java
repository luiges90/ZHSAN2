package com.zhsan.gamelogic.ai;

import com.zhsan.gamelogic.GameOrder;
import com.zhsan.gameobject.Faction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Peter on 16/5/2015.
 */
public class FactionAI {

    private Faction faction;

    public FactionAI(Faction f) {
        this.faction = f;
    }

    public void makeOrder() {
        faction.clearOrder();

    }

    public Faction getFaction() {
        return faction;
    }
}
