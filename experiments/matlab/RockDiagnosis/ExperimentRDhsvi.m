trials=200;
horizon=100;
metals=2;

%display('Running Baseline');
%RunRD('RDmap-r5-s7',metals,trials,horizon);
%RunRD('RDmap-r3-s6',metals,trials,horizon);
%RunRD('RDmap-r3-s3',metals,trials,horizon);

params.epsilon=0.1;
params.maxtime=1200000; %20 Minutes

display('Running HSVI');
params.algo='HSVI';
params.identifier='hsvi';

%RunRD('RDmap-r3-s3',metals,trials,horizon,params);
RunRD('RDmap-r3-s6',metals,trials,horizon,params);
%RunRD('RDmap-r5-s7',metals,trials,horizon,params);

