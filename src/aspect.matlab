function runner(size)
    x = 8 - 20;
    y = 1 + 7;
    tic();
    x = rand([1,size]);
    elapsedTime = toc();
    y = rand();
end
