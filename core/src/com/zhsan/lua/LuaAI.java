package com.zhsan.lua;

import com.zhsan.common.Paths;
import com.zhsan.gameobject.Faction;
import com.zhsan.gameobject.Section;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Created by Peter on 3/7/2015.
 */
public class LuaAI {

    public static final String PATH = Paths.LUA + "Ai" + File.separator;
    public static final String FACTION_AI = "factionAi.lua";

    public static void runFactionAi(Faction f) {
        Globals globals = JsePlatform.standardGlobals();

        globals.set("faction", createFactionTable(f));

        LuaValue chunk = globals.loadfile(PATH + FACTION_AI);

        chunk.call();
    }

    private static LuaTable createFactionTable(Faction f) {
        LuaTable factionTable = LuaValue.tableOf();

        factionTable.set("id", f.getId());
        factionTable.set("sections", f.getSections().getAll().stream().map(LuaAI::createSectionTable).collect(new LuaTableCollector()));

        return factionTable;
    }

    private static LuaTable createSectionTable(Section s) {
        LuaTable sectionTable = LuaValue.tableOf();

        sectionTable.set("id", s.getId());

        return sectionTable;
    }

    private static class LuaTableCollector implements Collector<LuaTable, LuaTable, LuaTable> {

        @Override
        public Supplier<LuaTable> supplier() {
            return LuaValue::tableOf;
        }

        @Override
        public BiConsumer<LuaTable, LuaTable> accumulator() {
            return (x, y) -> x.set(x.length() + 1, y);
        }

        @Override
        public BinaryOperator<LuaTable> combiner() {
            return (x, y) -> {
                for (int i = 0; i < y.length(); ++i) {
                    x.set(x.length() + 1, y.get(i));
                }
                return x;
            };
        }

        @Override
        public Function<LuaTable, LuaTable> finisher() {
            return x -> x;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(
                    Characteristics.IDENTITY_FINISH,
                    Characteristics.CONCURRENT));
        }
    }


}
