%Common defs
repts=10;
trials=500;
horizon=100;
bsize=[10 50 100 500 1000 2000];

%4 cells test

cells=[3 4 5];

CameraClean('CCS',5,repts,trials,horizon);
CameraClean('CCL',5,repts,trials,horizon);
    
%Active Surv
for c=cells
    for b=bsize
        display(['Experiment cells=' num2str(c) ' |b|=' num2str(b) ])
        display('--------------- (SURV AND LOC)')    
        CameraClean('CCS',c,repts,trials,horizon,b);
        CameraClean('CCL',c,repts,trials,horizon,b);
    end
end

horizon=20;
cells=[3 4 5];

for c=cells
    CameraClean('CCD',c,repts,trials,horizon);
    for b=bsize   
        display(['Experiment cells=' num2str(c) ' |b|=' num2str(b) ])
        display('--------------- (DIAGNOSIS)')
        CameraClean('CCD',c,repts,trials,horizon,b);
    end
end


% Active Localization
%type='CCL';
%CameraClean(type,cells,repts,trials,horizon);
%CameraClean(type,cells,repts,trials,horizon,bsize);

% Active Diagnosis
%type='CCD';
%CameraClean(type,cells,repts,trials,horizon);
%CameraClean(type,cells,repts,trials,horizon,bsize);


% Active Diagnosis
%type='CCD';
%CameraClean(type,cells,repts,trials,horizon);
%CameraClean(type,cells,repts,trials,horizon,10);
%CameraClean(type,cells,repts,trials,horizon,50);
%CameraClean(type,cells,repts,trials,horizon,500);
%CameraClean(type,cells,repts,trials,horizon,1000);

% Active Surv
%type='CCS';
%CameraClean(type,cells,repts,trials,horizon,10);
%CameraClean(type,cells,repts,trials,horizon,50);
%CameraClean(type,cells,repts,trials,horizon,100);
%CameraClean(type,cells,repts,trials,horizon,1000);

% Active Localization
%type='CCL';
%CameraClean(type,cells,repts,trials,horizon,10);
%CameraClean(type,cells,repts,trials,horizon,50);
%CameraClean(type,cells,repts,trials,horizon,500);
%CameraClean(type,cells,repts,trials,horizon,1000);

