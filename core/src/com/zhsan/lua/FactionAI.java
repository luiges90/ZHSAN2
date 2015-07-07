package com.zhsan.lua;

import com.zhsan.gameobject.Faction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

/**
 * Created by Peter on 7/7/2015.
 */
public final class FactionAI {

    private FactionAI(){}

    static LuaTable createFactionTable(Faction f) {
        LuaTable factionTable = LuaValue.tableOf();

        LuaAI.processAnnotations(factionTable, Faction.class, f);

        factionTable.set("sections", f.getSections().getAll().stream()
                .map(SectionAI::createSectionTable).collect(new LuaAI.LuaTableCollector()));

        return factionTable;
    }

}
