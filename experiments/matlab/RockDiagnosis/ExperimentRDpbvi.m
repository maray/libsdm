trials=200;
horizon=100;
metals=2;

%display('Running Baseline');
%RunRD('RDmap-r5-s7',metals,trials,horizon);
%RunRD('RDmap-r3-s6',metals,trials,horizon);
%RunRD('RDmap-r3-s3',metals,trials,horizon);

params.epsilon=0.01;
params.maxtime=1200000; %20 Minutes

display('Running PBVI');
params.algo='PBVI';
bks=[1 5 10 50 100];
params.bsize=2000;

for bk=bks
    params.backups=bk;
    params.identifier=['pbvi-bk' num2str(params.backups) '-bs' num2str(params.bsize)];
    RunRD('RDmap-r5-s7',metals,trials,horizon,params);
    RunRD('RDmap-r3-s6',metals,trials,horizon,params);
    RunRD('RDmap-r3-s3',metals,trials,horizon,params);
end

