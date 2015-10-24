-- various functions related to architecture

dofile(PATH .. "util.lua")

personFunc = {}

personFunc.totalInternalAbility = function(v)
    return v.getAgricultureAbility()
            + v.getCommerceAbility()
            + v.getTechnologyAbility()
            + v.getEnduranceAbility()
            + v.getMoraleAbility()
            + v.getRecruitAbility()
            + v.getTrainingAbility()
end

personFunc.militaryAbility = function(p)
    return p.getStrength()
            + p.getCommand() * 2
            + p.getIntelligence()
end