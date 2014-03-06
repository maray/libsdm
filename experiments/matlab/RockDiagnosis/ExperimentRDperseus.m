trials=200;
horizon=100;
metals=2;

%display('Running Baseline');
%RunRD('RDmap-r5-s7',metals,trials,horizon);
%RunRD('RDmap-r3-s6',metals,trials,horizon);
%RunRD('RDmap-r3-s3',metals,trials,horizon);

params.epsilon=0.01;
params.maxtime=1200000; %20 Minutes

display('Running PERSEUS');
params.algo='PERSEUS';
bsizes=[10000 50000];
for bs = bsizes
    params.bsize=bs;
    for r=2:10
        params.identifier=['perseus-bs' num2str(params.bsize) '-r' num2str(r)];
        %RunRD('RDmap-r5-s7',metals,trials,horizon,params);
        RunRD('RDmap-r3-s6',metals,trials,horizon,params);
        RunRD('RDmap-r3-s3',metals,trials,horizon,params);
    end
end



