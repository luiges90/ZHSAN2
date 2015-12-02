-- AI at section level

dofile(PATH .. "architectureAI.lua")
dofile(PATH .. "troopAI.lua")
dofile(PATH .. "architectureFuncAI.lua")
dofile(PATH .. "personFuncAI.lua")

function allocatePersons(section)
    if #section.getArchitectures() > 1 then
        local personMin = {}
        local personGood = {}
        for _, item in pairs(section.getArchitectures()) do
            if item.isFrontline() then
                personMin[item.getId()] = 3 * (#item.getMilitaries() + 1)
                personGood[item.getId()] = math.min(9, 3 * (#item.getMilitaries() + 1))
            elseif not archiectureFunc.wellDeveloped(item) then
                personMin[item.getId()] = 3
                personGood[item.getId()] = 9
            else
                personMin[item.getId()] = 0
                personGood[item.getId()] = 1
            end
        end

        for _, a in pairs(section.getArchitectures()) do
            if #a.getPersonsIncludingMoving() < personMin[a.getId()] then
                local moveFrom = section.getArchitectures()
                table.sort(moveFrom, function(p, q) return p.distanceTo(a.getId()) < q.distanceTo(a.getId()) end)
                for _, b in pairs(moveFrom) do
                    if a ~= b and #b.getPersons() > personMin[b.getId()] then
                        local candidates = b.getPersons()
                        if #candidates > 0 then
                            if a.isFrontline() and not b.isFrontline() then
                                table.sort(candidates, function(p, q) return personFunc.militaryAbility(p) > personFunc.militaryAbility(q) end)
                            else
                                table.sort(candidates, function(p, q) return personFunc.totalInternalAbility(p) > personFunc.totalInternalAbility(q) end)
                            end
                            local index = 1;
                            while #b.getPersons() > personMin[b.getId()] and #a.getPersonsIncludingMoving() < personMin[a.getId()] do
                                print("Moving " .. candidates[index].getName() .. " from " .. b.getName() .. " to " .. a.getName())
                                candidates[index].moveToArchitecture(a.getId())
                                index = index + 1
                            end
                        end
                    end
                end
            end
        end
    end
end

function chooseNextTarget(section)
    local candidates = {}
    for _, i in pairs(section.getArchitectures()) do
        for _, j in pairs(i.getHostileConnectedArchitectures()) do
            candidates[j.getId()] = j
        end
    end
    local scores = {}
    for _, i in pairs(candidates) do
        local v = i.getPopulation() / (getMilitaryThreat(i) + 10) / (#i.getHostileConnectedArchitectures() + 1)
        scores[i] = v
    end
    local target, _ = max(scores)

    print("Setting target to " .. target.getName())
    addTag(section, "targetArch", target.getId())
end

function sectionAI(section)
    chooseNextTarget(section)
    allocatePersons(section)
    for _, item in pairs(section.getArchitectures()) do
        architectureAI(item)
    end
    for _, item in pairs(section.getTroops()) do
        troopAI(item)
    end
end