-- entry point of ZHSan2 AI
-- `faction` is passed into this script and can be used to access everything the faction knows
-- All output will be written to Lua/AI/Logs/Faction<ID>.log files

-- Additionally, you can use dump(var) function to print the content of the variable, for debugging and inspection
-- PATH is the Lua AI Path

dofile(PATH .. "sectionAI.lua")

for i, item in pairs(faction.sections) do
    sectionAI(item)
end