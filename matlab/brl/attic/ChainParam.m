
horizon=150;
trials=100;
max_beta=horizon;
max_eta=horizon;
max_eps=1;
seeds=randint(trials,1,10000);
tests=50;

problem
algoUto=liblearning.rl.solve.BrlUtopical(problem)
resUto=runExperiment(problem,algoUto,horizon,trials,seeds);
resUtoAc=accuRewards(resUto,problem.gamma);
muUto=mean(resUtoAc(:,horizon));
varUto=var(resUtoAc(:,horizon));

for j=0:tests
    j
    %problem
    %algoExp=liblearning.rl.solve.Exploit(problem,j*max_eps/tests)
    %resExp=runExperiment(problem,algoExp,horizon,trials,seeds);
    %resExpAc=accuRewards(resExp,problem.gamma);
    %muExp(j+1)=mean(resExpAc(:,horizon));
    %varExp(j+1)=var(resExpAc(:,horizon));

    problem
    algoBeb=liblearning.rl.solve.Beb(problem,j*max_beta/tests)
    resBeb=runExperiment(problem,algoBeb,horizon,trials,seeds);
    resBebAc=accuRewards(resBeb,problem.gamma);
    muBeb(j+1)=mean(resBebAc(:,horizon));
    varBeb(j+1)=var(resBebAc(:,horizon));
    
    problem
    algoBouh=liblearning.rl.solve.Bouh(problem,j*max_eta/tests)
    resBouh=runExperiment(problem,algoBouh,horizon,trials,seeds);
    resBouhAc=accuRewards(resBouh,problem.gamma);
    muBouh(j+1)=mean(resBouhAc(:,horizon));
    varBouh(j+1)=var(resBouhAc(:,horizon));
    
    
end


    