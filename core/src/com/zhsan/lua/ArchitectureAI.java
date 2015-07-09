package com.zhsan.lua;

import com.zhsan.gameobject.Architecture;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

/**
 * Created by Peter on 7/7/2015.
 */
public final class ArchitectureAI {

    private ArchitectureAI(){}

    static LuaTable createArchitectureTable(Architecture a) {
        LuaTable architectureTable = LuaValue.tableOf();

        LuaAI.processAnnotations(architectureTable, Architecture.class, a);
        architectureTable.set("getPersons", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return a.getPersons().getAll()
                        .stream().map(PersonAI::createPersonTable).collect(new LuaAI.LuaTableCollector());
            }
        });

        return architectureTable;
    }

}
