clear java
clear all

horizon=150;
trials=500;
beta=2*horizon^2;
betaH=horizon;
eta=horizon;
%eps=0.2;
seeds=randint(trials,1,10000);


for i=0:5
    i
problem=liblearning.rl.problems.ChainProblem(0.5+i*0.1);

algo=liblearning.rl.solve.Exploit(problem,0)

res=runExperiment(problem,algo,horizon,trials,seeds);
resAc=accuRewards(res,problem.gamma);

muExp(i+1)=mean(resAc(:,horizon));
varExp(i+1)=var(resAc(:,horizon));

%algo=liblearning.rl.solve.Exploit(problem,eps)

%res=runExperiment(problem,algo,horizon,trials,seeds);
%resAc=accuRewards(res,problem.gamma);

%muExpEps(i+1)=mean(resAc(:,horizon));
%varExpEps(i+1)=var(resAc(:,horizon));

algo=liblearning.rl.solve.Beb(problem,beta)

res=runExperiment(problem,algo,horizon,trials,seeds);
resAc=accuRewards(res,problem.gamma);

muBeta(i+1)=mean(resAc(:,horizon));
varBeta(i+1)=var(resAc(:,horizon));

%algo=liblearning.rl.solve.Beb(problem,betaN2)

%res=runExperiment(problem,algo,horizon,trials,seeds);
%resAc=accuRewards(res,problem.gamma);

%muBetaN2(i+1)=mean(resAc(:,horizon));
%varBetaN2(i+1)=var(resAc(:,horizon));

algo=liblearning.rl.solve.Beb(problem,betaH)

res=runExperiment(problem,algo,horizon,trials,seeds);
resAc=accuRewards(res,problem.gamma);

muBetaH(i+1)=mean(resAc(:,horizon));
varBetaH(i+1)=var(resAc(:,horizon));

%algo=liblearning.rl.solve.Beb(problem,betaH2)

%5res=runExperiment(problem,algo,horizon,trials,seeds);
%5resAc=accuRewards(res,problem.gamma);

%muBetaH2(i+1)=mean(resAc(:,horizon));
%varBetaH2(i+1)=var(resAc(:,horizon));

algo=liblearning.rl.solve.Bouh(problem,eta)

res=runExperiment(problem,algo,horizon,trials,seeds);
resAc=accuRewards(res,problem.gamma);

muEta(i+1)=mean(resAc(:,horizon));
varEta(i+1)=var(resAc(:,horizon));

%algo=liblearning.rl.solve.Bouh(problem,etaH2)

%res=runExperiment(problem,algo,horizon,trials,seeds);
%resAc=accuRewards(res,problem.gamma);

%muEtaH2(i+1)=mean(resAc(:,horizon));
%varEtaH2(i+1)=var(resAc(:,horizon));

algo=liblearning.rl.solve.BrlUtopical(problem)

res=runExperiment(problem,algo,horizon,trials,seeds);
resAc=accuRewards(res,problem.gamma);

muUto(i+1)=mean(resAc(:,horizon));
varUto(i+1)=var(resAc(:,horizon));

end



%figure;
%hold on
%plotTraceMatrix(resExp,'k');
%plotTraceMatrix(resBeb,'r');
%plotTraceMatrix(resBouh,'b');
%plotTraceMatrix(resUto,'g');
%hold off
%figure;
%resExpAc=accuRewards(resExp,problem.gamma);
%resBebAc=accuRewards(resBeb,problem.gamma);
%resBouhAc=accuRewards(resBouh,problem.gamma);
%resUtoAc=accuRewards(resUto,problem.gamma);
%hold on
%plotTraceMatrix(resExpAc,'k');
%plotTraceMatrix(resBebAc,'r');
%plotTraceMatrix(resBouhAc,'b');
%plotTraceMatrix(resUtoAc,'g');
%hold off
