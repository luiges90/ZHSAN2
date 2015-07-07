package com.zhsan.lua;

import com.zhsan.common.Paths;
import com.zhsan.gameobject.Faction;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.*;
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
public final class LuaAI {

    public static final String PATH = Paths.LUA + "AI" + File.separator;
    public static final String LOGS = PATH + "logs" + File.separator;

    public static final String FACTION_AI = "ZHSanFactionAI.lua";

    private LuaAI(){}

    public static void runFactionAi(Faction f) {
        try (PrintWriter logger = new PrintWriter(new OutputStreamWriter(new FileOutputStream(LOGS + "Faction" + f.getId() + ".log"), "UTF-8"), true)) {
            Globals globals = JsePlatform.standardGlobals();

            globals.set("PATH", PATH);
            globals.set("dump", new OneArgFunction() {

                private String ns(int n, String s) {
                    return new String(new char[n]).replace("\0", s);
                }

                private void dump(int indent, LuaValue arg) {
                    if (arg.istable()) {
                        LuaTable table = arg.checktable();
                        for (int i = 0; i < table.keyCount(); ++i) {
                            LuaValue key = table.keys()[i];
                            LuaValue value = table.get(key);
                            if (value.istable()) {
                                logger.println(ns(indent, " ") + key + " = ");
                                dump(indent + 4, value.checktable());
                            } else {
                                logger.println(ns(indent, " ") + key + " = " + value);
                            }
                        }
                    } else {
                        logger.println(arg);
                    }
                }

                @Override
                public LuaValue call(LuaValue arg) {
                    dump(0, arg);
                    return NIL;
                }
            });
            globals.set("print", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue arg) {
                    logger.println(arg);
                    return NIL;
                }
            });

            globals.set("faction", FactionAI.createFactionTable(f));

            LuaValue chunk = globals.loadfile(PATH + FACTION_AI);

            try {
                chunk.call();
            } catch (LuaError e) {
                e.printStackTrace(logger);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class LuaTableCollector implements Collector<LuaTable, LuaTable, LuaTable> {

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
