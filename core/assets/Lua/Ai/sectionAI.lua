dofile(PATH .. "architectureAI.lua")

function sectionAI(section)
    for i, item in pairs(section.architectures) do
        architectureAI(item)
    end
end