-- various functions related to architecture

dofile(PATH .. "util.lua")

archiectureFunc = {}

archiectureFunc.wellDeveloped = function(architecture)
    return architecture.getAgriculture() < architecture.getKind().getAgriculture() * 0.5 and
            architecture.getCommerce() < architecture.getKind().getCommerce() * 0.5 and
            architecture.getTechnology() < architecture.getKind().getTechnology() * 0.5 and
            architecture.getMorale() < architecture.getKind().getMorale() * 0.5 and
            architecture.getEndurance() < architecture.getKind().getEndurance() * 0.5
end