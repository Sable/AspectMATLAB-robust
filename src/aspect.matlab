aspect_ aspect

patterns
    p1 : (get(*) | set(*)) & ~(~istype(logical) | ~dimension([3,3]));
end

end