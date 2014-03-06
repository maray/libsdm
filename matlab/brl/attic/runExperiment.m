function res = runExperiment(problem,algo,horizon,trials,seeds)
    res=zeros(trials,horizon);
    for i=1:trials
        res(i,:)=simulateProblem(algo,problem,horizon,seeds(i))';
    end
end

