aspect dW

patterns
    p1 : get(*);
    p2 : op(+);
end

actions
    a1 : before p1 : (name)
        disp(['Variable Get', name]);
    end
    a2 : before p2 : ()
        disp('Plus operation');
    end
end

end
