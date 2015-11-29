TROOP_STATE_ADVANCE = 1
TROOP_STATE_COMBAT = 2
TROOP_STATE_RETREAT = 3
TRROP_STATE_SIEGE = 4

function troopAI(troop)
    local defendArch = getTag(troop, "defendArch")
    local attackArch = getTag(troop, "attackArch")
    local state = tonumber(getTag(troop, "state"))

    local friendlyTroops = troop.getFriendlyTroopsInView()
    local hostileTroops = troop.getHostileTroopsInView()

    local friendlyValue = sum(friendlyTroops, troopFunc.merit)
    local hostileValue = sum(hostileTroops, troopFunc.merit)

    print("Troop " .. troop.getName())
    print("Nearby friendly = " .. friendlyValue .. ". Nearby hostile = " .. hostileValue)

    if #hostileTroops > 0 and state ~= TROOP_STATE_RETREAT then
        if friendlyValue > hostileValue * 0.8 then
            state = TROOP_STATE_COMBAT
        else
            state = TROOP_STATE_RETREAT
        end
    elseif #hostileTroops <= 0 and troop.isArchitectureInView(attackArch) then
        local a = scenario.getArchitecture(attackArch)
        if a.getBelongedFaction() == nil or a.getBelongedFaction().getId() ~= troop.getBelongedFaction().getId() then
            state = TRROP_STATE_SIEGE
        else
            state = TROOP_STATE_RETREAT
        end
    end

    print("State = " .. state)
    print("item " .. state .. ", " .. TROOP_STATE_ADVANCE)
    print((state == TROOP_STATE_ADVANCE))

    if state == TROOP_STATE_ADVANCE then
        troop.giveMoveToArchitectureOrder(attackArch)
    elseif state == TROOP_STATE_RETREAT then
        troop.giveMoveToEnterOrder(defendArch)
    elseif state == TRROP_STATE_SIEGE then
        troop.giveAttackArchitectureOrder(attackArch)
    elseif state == TROOP_STATE_COMBAT then
        local target, _ = max(hostileTroops, function(x, y) return x.getOffense() / x.getDefense() > y.getOffense() / y.getDefense() end)
        troop.giveAttackTroopOrder(target)
    else
        print("Unknown state " .. state)
    end

    addTag(troop, "state", state)

end