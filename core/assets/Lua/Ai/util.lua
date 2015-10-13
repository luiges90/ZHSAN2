-- various utility functions

function countIf(table, cond)
    local count = 0
    for i, v in pairs(table) do
        if cond(v) then
            count = count + 1
        end
    end
    return count
end

function max(table)
    local k
    local m = -9e99
    for i, v in pairs(table) do
        if (v > m) then
            k = i
            m = v
        end
    end
    return k, m
end