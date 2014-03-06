trials=500;
horizon=200;
rooms=4;
childs=2;

display('Running Baseline');
CSCreate(rooms,childs,trials,horizon);

params.epsilon=0.1;
params.maxtime=1200000; %20 Minutes


display('Running PERSEUS');
params.algo='PERSEUS';
params.bsize=50000;
for r=1:10
    params.identifier=['perseus-bs' num2str(params.bsize) '-r' num2str(r)];
    CSCreate(rooms,childs,trials,horizon,params);       
end