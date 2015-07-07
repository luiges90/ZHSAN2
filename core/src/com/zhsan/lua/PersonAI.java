package com.zhsan.lua;

import com.zhsan.gameobject.Person;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

/**
 * Created by Peter on 7/7/2015.
 */
public final class PersonAI {

    private PersonAI(){}

    static LuaTable createPersonTable(Person p) {
        LuaTable personTable = LuaValue.tableOf();

        LuaAI.processAnnotations(personTable, Person.class, p);

        return personTable;
    }

}
