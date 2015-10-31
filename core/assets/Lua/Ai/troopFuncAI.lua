-- various functions related to troop

dofile(PATH .. "militaryKindAI.lua")
dofile(PATH .. "util.lua")

troopFunc = {}

troopFunc.merit = function(troop)
    return troop.getOffense()
            + troop.getDefense()
            + troop.getIntelligence()
            + militaryKindFunc.score(nil, troop.getKind())
end

troopFunc.militaryMerit = function(military)
    return (militaryKindFunc.personScore(military.getLeader())
            + militaryKindFunc.score(nil, military.getKind()))
            * military.getQuantity()
end