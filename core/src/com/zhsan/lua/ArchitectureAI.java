package com.zhsan.lua;

import com.zhsan.gameobject.Architecture;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

/**
 * Created by Peter on 7/7/2015.
 */
public final class ArchitectureAI {

    private ArchitectureAI(){}

    static LuaTable createArchitectureTable(Architecture a) {
        LuaTable architectureTable = LuaValue.tableOf();

        architectureTable.set("id", a.getId());
        architectureTable.set("name", a.getName());

        return architectureTable;
    }

}
