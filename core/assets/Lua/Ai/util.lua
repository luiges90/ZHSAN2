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

function elem(table)
    for i, v in pairs(table) do
        return i, v
    end
end

function max(table, comparator)
    comparator = comparator or function(x, y) return x > y end
    local k, m = elem(table)
    for i, v in pairs(table) do
        if (comparator(v, m)) then
            k = i
            m = v
        end
    end
    return k, m
end