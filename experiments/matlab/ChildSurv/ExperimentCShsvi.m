trials=500;
horizon=200;
rooms=4;
childs=2;

display('Running Baseline');
CSCreate(rooms,childs,trials,horizon);

params.epsilon=0.1;
params.maxtime=1200000; %20 Minutes

display('Running HSVI');
params.algo='HSVI';
params.identifier='hsvi';

CSCreate(rooms,childs,trials,horizon,params);

