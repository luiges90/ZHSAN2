package com.zhsan.gameobject;

/**
 * Created by Peter on 6/12/2015.
 */
public interface GameScenarioEventsListener {

    public default void onOccupyArchitecture(Architecture architecture, Faction oldFaction, Faction newFaction){}

}
