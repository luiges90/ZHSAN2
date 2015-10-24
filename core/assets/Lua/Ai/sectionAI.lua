-- AI at section level

dofile(PATH .. "architectureAI.lua")

function allocatePersons(section)
    local personNeeded = {}
    for _, item in pairs(section.getArchitectures()) do

    end
end

function sectionAI(section)
    allocatePersons(section)
    for _, item in pairs(section.getArchitectures()) do
        architectureAI(item)
    end
end