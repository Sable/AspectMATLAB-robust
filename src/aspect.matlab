aspect_ aspect

actions
    a : before (get(*) | set(*)) & ~(~istype(logical) | ~dimension([3,3])) : ()

    end

end

end