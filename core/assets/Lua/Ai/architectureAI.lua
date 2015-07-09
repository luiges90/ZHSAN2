function architectureAI(architecture)
   dump(architecture)

   print("Internal work assignment for Architecture " .. architecture.getName())

   -- assign mayor: the best total internal values
   if architecture.canChangeMayor() then
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
      print("Assigning mayor to " + candidate.getName())
      architecture.changeMayor(candidate.getId())
   end

   -- assign jobs using a greedy algorithm: assign the best person to the lowest-valued task and continue
   for i, v in pairs(architecture.getPersons()) do
      if v.getDoingWork() ~= "mayor" then
         v.setDoingWork("none")
      end
   end

   local taskValues = {
      {architecture.getAgriculture(), "agriculture",
         table.sort(architecture.getPersons(), function(p, q) return p.getAgricultureAbility() > q.getAgricultureAbility() end)},
      {architecture.getCommerce(), "commerce",
         table.sort(architecture.getPersons(), function(p, q) return p.getCommerceAbility() > q.getCommerceAbility() end)},
      {architecture.getTechnology(), "technology",
         table.sort(architecture.getPersons(), function(p, q) return p.getTechnologyAbility() > q.getTechnologyAbility() end)},
      {architecture.getMorale(), "morale",
         table.sort(architecture.getPersons(), function(p, q) return p.getMoraleAbility() > q.getMoraleAbility() end)},
      {architecture.getEndurance(), "endurance",
         table.sort(architecture.getPersons(), function(p, q) return p.getEnduranceAbility() > q.getEnduranceAbility() end)}
   }
   table.sort(taskValues, function(p, q) return p[1] < q[1] end)
   local everybodyHasWork = false
   while not everybodyHasWork do
      for i, v in pairs(taskValues) do
         for j, w in pairs(v[3]) do
            if w.getDoingWork() == "none" then
               w.setDoingWork(v[2])
               print("Giving " .. w.getName() .. " work " .. v[2])
               break
            end
            everybodyHasWork = true
         end
      end
   end

end