function architectureAI(architecture)
   dump(architecture)

   print("Internal work assignment for Architecture " + architecture.name)

   -- assign mayor: the best total internal values
   if architecture.canChangeMayor() then
      local max = 0
      local candidate
      for i, v in pairs(architecture.persons) do
         local ability = v.agricultureAbility + v.commerceAbility + v.technologyAbility + v.enduranceAbility + v.moraleAbility
         if ability > max then
            max = ability
            candidate = v
         end
      end
      print("Assigning mayor to " + candidate.name)
      architecture.changeMayor(candidate.id)
   end

   -- assign jobs using a greedy algorithm: assign the best person to the lowest-valued task and continue
   for i, v in pairs(architecture.persons) do
      if v.doingWork ~= "mayor" then
         v.setDoingWork("none")
      end
   end

   local taskValues = {
      {architecture.agriculture, "agriculture",
         table.sort(architecture.persons, function(p, q) return p.agricultureAbility > q.agricultureAbility end)},
      {architecture.commerce, "commerce",
         table.sort(architecture.persons, function(p, q) return p.commerceAbility > q.commerceAbility end)},
      {architecture.technology, "technology",
         table.sort(architecture.persons, function(p, q) return p.technologyAbility > q.technologyAbility end)},
      {architecture.morale, "morale",
         table.sort(architecture.persons, function(p, q) return p.moraleAbility > q.moraleAbility end)},
      {architecture.endurance, "endurance",
         table.sort(architecture.persons, function(p, q) return p.enduranceAbility > q.enduranceAbility end)}
   }
   table.sort(taskValues, function(p, q) return p[1] < q[1] end)
   local everybodyHasWork = false
   while not everybodyHasWork do
      for i, v in pairs(taskValues) do
         for j, w in pairs(v[3]) do
            if w.doingWork == "none" then
               w.setDoingWork(v[2])
               print("Giving " + w.name + " work " + v[2])
               break
            end
            everybodyHasWork = true
         end
      end
   end

end