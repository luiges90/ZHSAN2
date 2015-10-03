package com.zhsan.lua;

import com.zhsan.gameobject.Faction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

/**
 * Created by Peter on 7/7/2015.
 */
public final class FactionAI {

    private FactionAI(){}

    static LuaTable createFactionTable(Faction f) {
        LuaTable factionTable = LuaValue.tableOf();

        LuaAI.processAnnotations(factionTable, Faction.class, f);

        return factionTable;
    }

}
