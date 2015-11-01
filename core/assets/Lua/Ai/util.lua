-- various utility functions

function countIf(table, cond)
    local count = 0
    for _, v in pairs(table) do
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

function sum(table, func)
    func = func or function(x) return x end
    local n = 0
    for _, v in pairs(table) do
        n = n + func(v)
    end
    return n;
end

function randomPick(table)
    if #table == 0 then return nil end
    return table[math.random(1, #table)]
end

function addTag(tag)

end