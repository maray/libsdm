function pb = CreateAlgorithm(pomdp,params)
display(['Creating ' params.identifier ]); 
epsi=params.epsilon*pomdp.getRewardFunction.max();
timeCri=libsdm.common.MaxTimeCriteria(params.maxtime);
%timeCri=MaxTimeCriteria(1200000);
switch params.algo
    case 'PERSEUS',
        pb=libsdm.pomdp.solve.offline.Perseus(pomdp,params.bsize,0);
    case 'PBVI',
        pb=libsdm.pomdp.solve.offline.PBVI(pomdp,params.backups,params.bsize,0);
    case 'HSVI',
        pb=libsdm.pomdp.solve.offline.HSVI(pomdp,epsi);
end
pb.addStopCriteria(libsdm.pomdp.solve.offline.PbConvergence(epsi));
pb.addStopCriteria(timeCri);
pb.setVerbose(true);
end