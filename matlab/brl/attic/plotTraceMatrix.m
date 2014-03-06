function plotTraceMatrix(res,linespec)
    %cfin=1.96*sqrt(var(res)/size(res,1));
    %plotTrace(mean(res),linespec,cfin);
    plotTrace(mean(res),linespec);
end

