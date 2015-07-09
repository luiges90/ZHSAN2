dofile(PATH .. "architectureAI.lua")

function sectionAI(section)
    for i, item in pairs(section.getArchitectures()) do
        architectureAI(item)
    end
end