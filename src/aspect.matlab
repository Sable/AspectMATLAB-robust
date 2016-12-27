aspect_ myAspect
    %comemnt
    patterns
        p1 : set(*);  %this is a set pattern
    end
    actions   %begin of action
        a1 : before p1 : (name)  %comment
            disp(name);
        end
        %end of action
    end
    %end of aspect
end