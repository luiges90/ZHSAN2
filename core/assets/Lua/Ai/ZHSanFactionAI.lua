-- entry point of ZHSan2 AI
-- `faction` contains current faciton information
-- `scenario` contains various scenario information
-- All output will be written to Lua/AI/Logs/Faction<ID>.log files

-- Additionally, you can use dump(var) function to print the content of the variable, for debugging and inspection
-- PATH is the Lua AI Path

dofile(PATH .. "sectionAI.lua")

print(scenario.getGameDateYear() .. "年" .. scenario.getGameDateMonth() .. "月" .. scenario.getGameDateDay() .. "日")

for i, item in pairs(faction.getSections()) do
    sectionAI(item)
end