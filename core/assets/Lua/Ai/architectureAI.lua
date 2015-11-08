-- AI at architecture level

dofile(PATH .. "militaryKindAI.lua")
dofile(PATH .. "personFuncAI.lua")
dofile(PATH .. "troopFuncAI.lua")
dofile(PATH .. "util.lua")

function getTargetArchitectureId(architecture)
   return string.match(architecture.getBelongedSection().getAiTags(), "targetArch(%d+)")
end

function getMilitaryThreat(architecture)
   local connecting = architecture.getHostileConnectedArchitectures()
   local total = 0
   for _, v in pairs(connecting) do
      total = total + v.getMilitaryUnitCount()
   end
   return total * 1.5 + 50
end

function architectureAI(architecture)
   if #architecture.getPersons() > 0 then
      print("Internal work assignment for Architecture " .. architecture.getName())

      defend(architecture)
      attack(architecture)

      local militaryThreat = getMilitaryThreat(architecture)
      local fullRecruit = architecture.getMilitaryUnitCountInFullRecruit()
      print("Military Threat = " .. militaryThreat .. " and unitCountFullRecruit = " .. fullRecruit)
      if militaryThreat >= fullRecruit then
         createMilitaries(architecture)
      end

      assignMayor(architecture)
      assignMilitary(architecture)
      assignInternal(architecture)
   end

end

function defend(architecture)
   local hostileTroops = architecture.getHostileTroopsInView()
   local hostileValue = sum(hostileTroops, troopFunc.merit)
   if #hostileTroops > 0 then
      local friendlyTroops = architecture.getFriendlyTroopsInView()
      local friendlyValue = sum(friendlyTroops, troopFunc.merit)
      while friendlyValue * 3 < hostileValue do
         local position = randomPick(architecture.getCampaignablePositions())
         if position ~= nil then
            local _, m = max(architecture.getMilitaries(), troopFunc.militaryMerit)
            local troop = m.startCampaign(position.x, position.y)
            -- TODO set troop AI
            friendlyValue = friendlyValue + troopFunc.merit(troop)
         end
      end
   end
end

function attack(architecture)
   local targetId = getTargetArchitectureId(architecture)
   local target = scenario.getArchitecture(targetId)

   local reserve = getMilitaryThreat(architecture)
   local targetPower = target.getMilitaryUnitCount() * 1.5

   if architecture.getMilitaryUnitCount() > (reserve + targetPower) * 1.1 then
      local powerSent = 0
      while powerSent < targetPower do
         local position = randomPick(architecture.getCampaignablePositions())
         if position ~= nil then
            local _, m = max(architecture.getMilitaries(), troopFunc.militaryMerit)
            local troop = m.startCampaign(position.x, position.y)
            -- TODO set troop AI
            powerSent = friendlyValue + troopFunc.merit(troop)
         end
      end
   end
end

function createMilitaries(architecture)
   -- create military
   local militaries = architecture.getMilitaries()

   local militaryKinds = architecture.getActualCreatableMilitaryKinds()
   if (#militaryKinds <= 0) then return end

   local kindScores = {}
   for _, mk in pairs(militaryKinds) do
      kindScores[mk.getId()] = militaryKindFunc.score(architecture, mk) / (countIf(militaries, function(k) return k.getId() == mk.getId() end) + 1)
   end

   local toRecruit, _ = max(kindScores)
   print("creating military of kind id " .. toRecruit)
   local createdMilitary = architecture.createMilitary(toRecruit)

   -- Assign main officer
   local toAssign, value
   value = 0
   for _, p in pairs(architecture.getPersonsWithoutLeadingMilitary()) do
      local x = militaryKindFunc.personScore(p)
      if x > value then
         value = x
         toAssign = p
      end
   end

   if toAssign ~= nil then
      createdMilitary.setLeader(toAssign.getId())
      print("assigning " .. toAssign.getName() .. " to the newly created troop")
   end

end

function assignMayor(architecture)
   -- assign mayor: the best total internal values
   if architecture.canChangeMayorToOther() then
      local max = 0
      local candidate
      for _, v in pairs(architecture.getPersons()) do
         local ability = personFunc.totalInternalAbility(v)
         if ability > max then
            max = ability
            candidate = v
         end
      end
      print("Assigning mayor to " .. candidate.getName())
      architecture.changeMayor(candidate.getId())
   end
end

function assignMilitary(architecture)
   -- assign recruit/training jobs to main officers
   local hasNoLeaderTrainingTroop = false
   for _, m in pairs(architecture.getTrainableMilitaries()) do
      if m.getLeader() ~= nil and m.getLeader().getDoingWork() ~= "mayor" then
         m.getLeader().setDoingWork("training")
         print("assigning " .. m.getLeader().getName() .. " to training")
      else
         hasNoLeaderTrainingTroop = true
      end
   end
   if hasNoLeaderTrainingTroop then
      local _, p = max(architecture.getPersons(), function(x, y) return x.getTrainingAbility() > y.getTrainingAbility() end)
      p.setDoingWork("training")
      print("assigning " .. p.getName() .. " to training")
   end

   local hasNoLeaderRecruitTroop = false
   for _, m in pairs(architecture.getRecruitableMilitaries()) do
      if m.getLeader() ~= nil and m.getLeader().getDoingWork() ~= "mayor" then
         m.getLeader().setDoingWork("recruit")
         print("assigning " .. m.getLeader().getName() .. " to recruit")
      else
         hasNoLeaderRecruitTroop = true
      end
   end
   if hasNoLeaderRecruitTroop then
      local _, p = max(architecture.getPersonsExcludingMayor(), function(x, y) return x.getRecruitAbility() > y.getRecruitAbility() end)
      p.setDoingWork("recruit")
      print("assigning " .. p.getName() .. " to recruit")
   end
end

function assignInternal(architecture)
   -- assign jobs using a greedy algorithm: assign the best person to the lowest-valued task and continue
   for i, v in pairs(architecture.getPersons()) do
      if v.getDoingWork() ~= "mayor" and v.getDoingWork() ~= "recruit" and v.getDoingWork() ~= "training" then
         v.setDoingWork("none")
      end
   end

   local availablePersons = {}
   for i, p in pairs(architecture.getPersons()) do
      if p.getDoingWork() == "none" then
         table.insert(availablePersons, p)
      end
   end

   local getSortedPersons = function(func)
      table.sort(availablePersons, function(p, q) return func(p) > func(q) end)
      return availablePersons
   end

   local taskValues = {
      {architecture.getAgriculture(), "agriculture", getSortedPersons(function(x) return x.getAgricultureAbility() end)},
      {architecture.getCommerce(), "commerce", getSortedPersons(function(x) return x.getCommerceAbility() end)},
      {architecture.getTechnology(), "technology", getSortedPersons(function(x) return x.getTechnologyAbility() end)},
      {architecture.getMorale(), "morale", getSortedPersons(function(x) return x.getMoraleAbility() end)},
      {architecture.getEndurance(), "endurance", getSortedPersons(function(x) return x.getEnduranceAbility() end)},
   }
   table.sort(taskValues, function(p, q) return p[1] < q[1] end)
   local everybodyHasWork = false
   while not everybodyHasWork do
      for i, v in pairs(taskValues) do
         local assignedAnybody = false
         for j, w in pairs(v[3]) do
            if w.getDoingWork() == "none" then
               w.setDoingWork(v[2])
               print("Giving " .. w.getName() .. " work " .. v[2])
               assignedAnybody = true
               break
            end
         end
         if not assignedAnybody then
            everybodyHasWork = true
         end
      end
   end
end