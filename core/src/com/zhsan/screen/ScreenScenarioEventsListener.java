package com.zhsan.screen;

import com.zhsan.gamecomponents.toolbar.GameRecord;
import com.zhsan.gameobject.Architecture;
import com.zhsan.gameobject.Faction;
import com.zhsan.gameobject.GameScenarioEventsListener;

/**
 * Created by Peter on 6/12/2015.
 */
public class ScreenScenarioEventsListener implements GameScenarioEventsListener {

    private GameScreen screen;

    public ScreenScenarioEventsListener(GameScreen screen) {
        this.screen = screen;
    }

    @Override
    public void onOccupyArchitecture(Architecture architecture, Faction oldFaction, Faction newFaction) {
        if (oldFaction == null) {
            screen.addGameRecordsMessage(GameRecord.StringKeys.OCCUPY_ARCHITECTURE, newFaction.getName(), architecture.getName());
        } else {
            screen.addGameRecordsMessage(GameRecord.StringKeys.OCCUPY_ARCHITECTURE_WITH_FACTION, oldFaction.getName(), newFaction.getName(), architecture.getName());
        }
    }
}
