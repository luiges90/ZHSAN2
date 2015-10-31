-- various functions related to militaryKind

dofile(PATH .. "util.lua")

militaryKindFunc = {}

militaryKindFunc.getMaxUnitCount =
    function(militaryKind)
        return militaryKind.getQuantity() / militaryKind.getUnitQuantity()
    end

militaryKindFunc.score =
    function(architecture, militaryKind)
        local mk = militaryKind
        local power = (mk.getDefense() + mk.getDefensePerUnit() * militaryKindFunc.getMaxUnitCount(mk)) +
                (mk.getOffense() + mk.getOffensePerUnit() * militaryKindFunc.getMaxUnitCount(mk))
        local antiArch = mk.getArchitectureOffense()
        local range = mk.getRangeHi()^1.5 - (mk.getRangeLo() - 1)^1.5
        local speed = mk.getMovability()^1.5
        local cost = 1
        if architecture ~= nil then
            cost = mk.getCostOfArchitecture(architecture.getId())
        end
        -- TODO consider terrains
        return ((power + antiArch * 0.5) * (range + speed)) / cost
    end

militaryKindFunc.personScore =
    function(person)
        return person.getCommand() * 0.85 + person.getStrength() * 0.15 - 30
    end