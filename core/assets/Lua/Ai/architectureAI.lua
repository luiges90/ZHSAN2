function architectureAI(architecture)
   if #architecture.getPersons() > 0 then
      print("Internal work assignment for Architecture " .. architecture.getName())

      assignMayor(architecture)
      assignInternal(architecture)
   end

end

function createMilitaries(architecture)

end

function assignMayor(architecture)
   -- assign mayor: the best total internal values
   if architecture.canChangeMayorToOther() then
      local max = 0
      local candidate
      for i, v in pairs(architecture.getPersons()) do
         local ability = v.getAgricultureAbility() + v.getCommerceAbility() + v.getTechnologyAbility()
                 + v.getEnduranceAbility() + v.getMoraleAbility()
         if ability > max then
            max = ability
            candidate = v
         end
      end
      print("Assigning mayor to " .. candidate.getName())
      architecture.changeMayor(candidate.getId())
   end
end

function assignInternal(architecture)
   -- assign jobs using a greedy algorithm: assign the best person to the lowest-valued task and continue
   for i, v in pairs(architecture.getPersons()) do
      if v.getDoingWork() ~= "mayor" then
         v.setDoingWork("none")
      end
   end

   local getSortedPersons = function(func)
      local persons = architecture.getPersons()
      table.sort(persons, function(p, q) return func(p) > func(q) end)
      return persons
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