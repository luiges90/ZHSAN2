package com.zhsan.lua;

import com.zhsan.gameobject.Section;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

/**
 * Created by Peter on 7/7/2015.
 */
public final class SectionAI {

    private SectionAI(){}

    static LuaTable createSectionTable(Section s) {
        LuaTable sectionTable = LuaValue.tableOf();

        LuaAI.processAnnotations(sectionTable, Section.class, s);

        sectionTable.set("architectures", s.getArchitectures().getAll()
                .stream().map(ArchitectureAI::createArchitectureTable).collect(new LuaAI.LuaTableCollector()));

        return sectionTable;
    }

}
