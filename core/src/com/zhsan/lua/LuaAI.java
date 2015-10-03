package com.zhsan.lua;

import com.zhsan.common.Paths;
import com.zhsan.gameobject.Faction;
import com.zhsan.gameobject.GameObject;
import com.zhsan.gameobject.GameObjectList;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
                                dump(indent + 4, value);
                            } else if (value.isfunction()) {
                                if (key.tojstring().startsWith("get")) {
                                    LuaValue result = value.call();
                                    if (result.istable()) {
                                        logger.println(ns(indent, " ") + key + " = ");
                                        dump(indent + 4, result);
                                    } else {
                                        logger.println(ns(indent, " ") + key + " = " + result);
                                    }
                                } else {
                                    logger.println(ns(indent, " ") + key + " = " + value);
                                }
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
        } catch (Exception e) {
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

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    public @interface ExportToLua{}

    private static LuaValue toLuaValue(Object obj) {
        if (obj instanceof Double) {
            return LuaValue.valueOf((Double) obj);
        } else if (obj instanceof Float) {
            return LuaValue.valueOf((Float) obj);
        } else if (obj instanceof Long) {
            return LuaValue.valueOf((Long) obj);
        } else if (obj instanceof Integer) {
            return LuaValue.valueOf((Integer) obj);
        } else if (obj instanceof Short) {
            return LuaValue.valueOf((Short) obj);
        } else if (obj instanceof Byte) {
            return LuaValue.valueOf((Byte) obj);
        } else if (obj instanceof Character) {
            return LuaValue.valueOf((Character) obj);
        } else if (obj instanceof Boolean) {
            return LuaValue.valueOf((Boolean) obj);
        } else if (obj instanceof String) {
            return LuaValue.valueOf((String) obj);
        } else if (obj == null) {
            return LuaValue.NIL;
        } else if (obj instanceof GameObjectList) {
            GameObjectList<?> list = (GameObjectList) obj;
            if (list.size() == 0) {
                return LuaValue.tableOf();
            }
            LuaTable table = LuaValue.tableOf();
            int index = 0;
            for (GameObject i : list) {
                LuaTable child = LuaValue.tableOf();
                LuaAI.processAnnotations(child, i.getClass(), i);
                table.set(index, child);
                index++;
            }
            return table;
        } else {
            throw new IllegalArgumentException("toLuaValue only accept strings, primitives, GameObjectLists or null. " +
                    obj + "(" + obj.getClass().getName() + ") received.");
        }
    }

    private static Object fromLuaValue(LuaValue val) {
        if (val.isboolean()) {
            return val.toboolean();
        } else if (val.isint()){
            return val.toint();
        } else if (val.islong()) {
            return val.tolong();
        } else if (val.isstring()) {
            return val.tojstring();
        } else if (val.isnil()) {
            return null;
        } else {
            throw new IllegalArgumentException("fromLuaValue only accept booleans, numbers, strings or NIL. " +
                    val + " received.");
        }
    }

    static void processAnnotations(LuaTable table, Class<?> klass, Object obj) {
        for (Method m : klass.getMethods()) {
           if (m.isAnnotationPresent(ExportToLua.class)) {
               table.set(m.getName(), new VarArgFunction() {
                   @Override
                   public Varargs invoke(Varargs args) {
                       Object[] objArgs = new Object[args.narg()];
                       if (args.narg() == 1) {
                            objArgs[0] = fromLuaValue(args.arg1());
                       } else {
                           for (int i = 0; i < args.narg(); ++i) {
                               objArgs[i] = fromLuaValue(args.arg(i));
                           }
                       }
                       Object result;
                       try {
                           result = m.invoke(obj, objArgs);
                       } catch (IllegalAccessException | InvocationTargetException e) {
                           throw new RuntimeException("Exception occurred invoking java method " + m, e);
                       }
                       return toLuaValue(result);
                   }
               });
           }
        }
    }

}
