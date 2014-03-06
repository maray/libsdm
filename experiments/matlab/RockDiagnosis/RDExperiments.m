trials=200;
horizon=100;
metals=2;

%display('Running Baseline');
%RunRD('RDmap-r5-s7',metals,trials,horizon);
%RunRD('RDmap-r3-s6',metals,trials,horizon);
%RunRD('RDmap-r3-s3',metals,trials,horizon);

params.epsilon=0.01;
params.maxtime=1200000; %20 Minutes

display('Running HSVI');
params.algo='HSVI';
params.identifier='hsvi';

%RunRD('RDmap-r5-s7',metals,trials,horizon,params);
%RunRD('RDmap-r3-s6',metals,trials,horizon,params);
RunRD('RDmap-r3-s3',metals,trials,horizon,params);

display('Running PBVI');
params.algo='PBVI';
params.backups=10;
params.bsize=1000;
params.identifier=['pbvi-bk' num2str(params.backups) '-bs' num2str(params.bsize)];

%RunRD('RDmap-r5-s7',metals,trials,horizon,params);
%RunRD('RDmap-r3-s6',metals,trials,horizon,params);
RunRD('RDmap-r3-s3',metals,trials,horizon,params);


display('Running PERSEUS');
params.algo='PERSEUS';
params.bsize=10000;
for r=1:10
    params.identifier=['perseus-bs' num2str(params.bsize) '-r' num2str(r)];
    %RunRD('RDmap-r5-s7',metals,trials,horizon,params);
    %RunRD('RDmap-r3-s6',metals,trials,horizon,params);
    RunRD('RDmap-r3-s3',metals,trials,horizon,params);
end


