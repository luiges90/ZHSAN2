TROOP_STATE_ADVANCE = 1
TROOP_STATE_COMBAT = 2
TROOP_STATE_RETREAT = 3
TRROP_STATE_SIEGE = 4

function TroopAI(troop)
    local defendArch = getTag(troop, "defendArch")
    local attackArch = getTag(troop, "attackArch")
    local state = getTag(troop, "state")

    local friendlyTroops = troop.getFriendlyTroopsInView()
    local hostileTroops = troop.getHostileTroopsInView()

    local friendlyValue = sum(friendlyTroops, troopFunc.merit)
    local hostileValue = sum(hostileTroops, troopFunc.merit)

    if #hostileTroops > 0 and state ~= TROOP_STATE_RETREAT then
        if friendlyValue > hostileValue * 0.8 then
            state = TROOP_STATE_COMBAT
        else
            state = TROOP_STATE_RETREAT
        end
    elseif #hostileTroops <= 0 and troop.isArchitectureInView(attackArch) then
        if attackArch.getBelongedFaction().getId() == troop.getBelongedFaction().getId() then
            state = TROOP_STATE_RETREAT
        else
            state = TROOP_STATE_SIEGE
        end
    end

    if state == TROOP_STATE_ADVANCE then
        troop.giveMoveToArchitectureOrder(attackArch)
    elseif state == TROOP_STATE_RETREAT then
        troop.giveMoveToEnterOrder(defendArch)
    elseif state == TROOP_STATE_SIEGE then
        troop.giveAttackArchitectureOrder(attackArch)
    else
        troop.giveAttackTroopOrder(max(hostileTroops, function(x, y) return x.getOffense() / x.getDefense() > y.getOffense() / y.getDefense() end))
    end

    addTag(troop, "state", state)

end