package com.zhsan.lua;

import com.zhsan.common.Paths;
import com.zhsan.gameobject.Faction;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;

/**
 * Created by Peter on 3/7/2015.
 */
public class LuaAI {

    public static final String PATH = Paths.LUA + "Ai" + File.separator;
    public static final String FACTION_AI = "factionAi.lua";

    public static void runFactionAi(Faction f) {
        Globals globals = JsePlatform.standardGlobals();
        initFactionInterface(globals, f);

        LuaValue chunk = globals.loadfile(PATH + FACTION_AI);

        chunk.call();
    }

    private static void initFactionInterface(Globals globals, Faction f) {
        LuaValue factionTable = LuaValue.tableOf();

        factionTable.set("id", f.getId());
        factionTable.set("setName", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                System.out.println("From java, " + arg);
                return LuaValue.valueOf(true);
            }
        });

        globals.set("faction", factionTable);
    }

}
