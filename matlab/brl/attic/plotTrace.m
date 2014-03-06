function plotTrace(vec,linespec,error)
    if exist('error','var')
        errorbar(vec,error,linespec);
    else 
        plot(vec,linespec)
    end
end

