function [a] = fib(fib1, fib2)
    for i = 1 : 10
        disp('hello');
        a = fib1 + fib2;
    end
    if a == 0
        disp('nooo');
    end
end