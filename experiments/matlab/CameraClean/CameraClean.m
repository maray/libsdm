function CameraClean(typ,cells,repts,trials,horizon,bsize)

epsilon=0.1;

libsdm

base=false;
if nargin == 5
    base=true;
end

%horizon=100;
%bsize=1000;
%trials=500;
%repts=10;
%cells=3;


moveprob=0.0;
if strcmp(typ,'CCS')
    moveprob=0.05;
end
algo_horizon=0;
if strcmp(typ,'CCD')
    algo_horizon=horizon+1;
end
localization=false;
if strcmp(typ,'CCL')
    localization=true;
end

clean_prob=0.8;
dirty_prob=0.55;
 
%ip_delta=1e-5;

display('Loading rhoPOMDP');
pomdp=PomdpFactory.getCameraClean(PomdpFactory.RTYPE_ENTROPY,cells,clean_prob,dirty_prob,moveprob,0,localization);
sim=PomdpSimulator(pomdp);

if (base)
    file=[typ '-base-c' num2str(cells) '-h' num2str(horizon) '-t'  num2str(trials) '-r' num2str(repts) '.mat'];
    timeGreedyOnline=zeros(repts,trials);
    timeRandomOnline=zeros(repts,trials);
    for r=1:repts
        display(['** Repetition ' num2str(r)]);
        display('Greedy Simulation');
        online=PomdpGreedy(pomdp);
        valuesGreedy{r}=zeros(trials,horizon);
        valuesRandom{r}=zeros(trials,horizon);

        for i=1:trials
            sim.setVerbose(false);
            %if i==1
            %    sim.setVerbose(true);
            %end
            results=sim.simulate(online,horizon,IntegerVector(0));
            valuesGreedy{r}(i,:)=results.getArray();
            timeGreedyOnline(r,i)=online.getStats().totalTime();
        end
        
        display('Random Simulation');
        online=PomdpRandom(pomdp);
        for i=1:trials
            sim.setVerbose(false);
            %if i==1
            % sim.setVerbose(true);
            %end
            results=sim.simulate(online,horizon,IntegerVector(0));
            valuesRandom{r}(i,:)=results.getArray();
            timeRandomOnline(r,i)=online.getStats().totalTime();

        end
    end
    save(file,'typ','horizon','trials', 'repts','cells', 'valuesGreedy','valuesRandom','timeGreedyOnline','timeRandomOnline');
else
    timeEntropyOnline=zeros(repts,trials);
    timeVarianceOnline=zeros(repts,trials);
    timeLinearOnline=zeros(repts,trials);
    timeEntropyOffline=zeros(repts,1);
    timeVarianceOffline=zeros(repts,1);
    timeLinearOffline=zeros(repts,1);
    
    if algo_horizon==0
        pbparam=PbParams.getPERSEUS(bsize);
    else
       pbparam=PbParams(PbParams.BACKUP_SYNC_FULL,PbParams.EXPAND_RANDOM_DYNAMIC,1,bsize,bsize);
    end
    file=[typ '-c' num2str(cells) '-h' num2str(horizon) '-t'  num2str(trials) '-r' num2str(repts) '-b' num2str(bsize) '.mat'];
    pomdpEntropy=PomdpFactory.getCameraClean(PomdpFactory.RTYPE_ENTROPY,cells,clean_prob,dirty_prob,moveprob,algo_horizon,localization);
    pomdpVariance=PomdpFactory.getCameraClean(PomdpFactory.RTYPE_VARIANCE,cells,clean_prob,dirty_prob,moveprob,algo_horizon,localization);
    pomdpLinear=PomdpFactory.getCameraClean(PomdpFactory.RTYPE_LINEAR,cells,clean_prob,dirty_prob,moveprob,algo_horizon,localization);
    display('Initializating PBVI');
  
    for r=1:repts
        display(['** Repetition ' num2str(r)]);
        display('PB Entropy...');
        pbEntropy=PointBased(pomdpEntropy,pbparam,algo_horizon);
        pbEntropy.setVerbose(true);
        if algo_horizon==0
            epsi=epsilon*pomdpEntropy.getRewardFunction.max();
            conv=PointBasedConvergence(epsi);
            conv.setVerbose(true);
            pbEntropy.addStopCriteria(conv);
        end
        pbEntropy.run();
        timeEntropyOffline(r)=pbEntropy.getStats().totalTime();
        online=ExecPomdpVF(pomdpEntropy,pbEntropy);
        valuesEntropy{r}=zeros(trials,horizon);
        display('Entropy Simultation...');  
        for i=1:trials
            sim.setVerbose(false);
            %if i==1
            %    sim.setVerbose(true);
            %end
            results=sim.simulate(online,horizon,IntegerVector(0));
            valuesEntropy{r}(i,:)=results.getArray();
            timeEntropyOnline(r,i)=online.getStats().totalTime();
        end
        clear online pbEntropy;
        display('PB Linear...');
        pbLinear=PointBased(pomdpLinear,pbparam,algo_horizon);
        pbLinear.setVerbose(true);
        if algo_horizon==0
            epsi=epsilon*pomdpLinear.getRewardFunction.max();
            conv=PointBasedConvergence(epsi);
            conv.setVerbose(true);
            pbLinear.addStopCriteria(conv);
            
        end
        %pbLinear.setVerbose(true);
        %display('Running Value Iteration!    
        pbLinear.run();
        timeLinearOffline(r)=pbLinear.getStats().totalTime();
        online=ExecPomdpVF(pomdpLinear,pbLinear);
        valuesLinear{r}=zeros(trials,horizon);
        
        display('Linear Simultation...');  
        for i=1:trials
            sim.setVerbose(false);
            %if i==1
            %    sim.setVerbose(true);
            %end
            results=sim.simulate(online,horizon,IntegerVector(0));
            valuesLinear{r}(i,:)=results.getArray();
            timeLinearOnline(r,i)=online.getStats().totalTime();
        end
        %meanLinear(r,:)=mean(valuesLinear);
        %stdLinear(r,:)=std(valuesLinear);
        clear online pbLinear;
        
        display('PB Variance...');
        pbVariance=PointBased(pomdpVariance,pbparam,algo_horizon);
        pbVariance.setVerbose(true);
        if algo_horizon==0
            epsi=epsilon*pomdpVariance.getRewardFunction.max();
            conv=PointBasedConvergence(epsi);
            conv.setVerbose(true);
            pbVariance.addStopCriteria(conv);
        end
        pbVariance.run();
        timeVarianceOffline(r)=pbVariance.getStats().totalTime();
        online=ExecPomdpVF(pomdpVariance,pbVariance);
        valuesVariance{r}=zeros(trials,horizon);
    
        display('Variance Simultation...');  
        for i=1:trials
            sim.setVerbose(false);
            %if i==1
            %    sim.setVerbose(true);
            %end
            results=sim.simulate(online,horizon,IntegerVector(0));
            valuesVariance{r}(i,:)=results.getArray();
            timeVarianceOnline(r,i)=online.getStats().totalTime();
        end
        clear online pbVariance;
    end
    save(file,'typ','horizon','trials', 'repts', 'bsize','cells', 'valuesEntropy', 'valuesLinear','valuesVariance', 'timeEntropyOffline','timeLinearOffline','timeVarianceOffline','timeEntropyOnline','timeLinearOnline','timeVarianceOnline');
end
clear global
