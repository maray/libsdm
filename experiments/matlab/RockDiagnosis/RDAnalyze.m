function RDAnalyze(file,gamma,bias)

load(file);


G=gamma*ones(trials,horizon);
G(:,1)=ones(trials,1);
G=cumprod(G,2);

base_file=[mapa '-base-m' num2str(metals) '-h' num2str(horizon) '-t'  num2str(trials) '-r' num2str(repts) '.mat'];

load(base_file);

%meanEntropy=zeros(repts,horizon);
%meanLinear=zeros(repts,horizon);
%meanVariance=zeros(repts,horizon);
%meanGreedy=zeros(repts,horizon);
%meanRandom=zeros(repts,horizon);
%stdEntropy=zeros(repts,horizon);
%stdLinear=zeros(repts,horizon);
%stdVariance=zeros(repts,horizon);
%stdGreedy=zeros(repts,horizon);
%stdRandom=zeros(repts,horizon);
%sumEntropy=zeros(repts,trials);
%sumLinear=zeros(repts,trials);
%sumVariance=zeros(repts,trials);
%sumGreedy=zeros(repts,trials);
%sumRandom=zeros(repts,trials);

r=1
    meanEntropy=mean(G.*(valuesEntropy{r})  - repmat(bias,trials,horizon) );
    meanLinear=mean(G.*(valuesLinear{r}) - repmat(bias,trials,horizon) );
    meanVariance=mean(G.*(valuesVariance{r}) - repmat(bias,trials,horizon));
    meanGreedy=mean(G.*(valuesGreedy{r}) - repmat(bias,trials,horizon)) ;
    meanRandom=mean(G.*(valuesRandom{r}) - repmat(bias,trials,horizon)) ;
    stdEntropy=std(G.*(valuesEntropy{r}) - repmat(bias,trials,horizon)) ;
    stdLinear=std(G.*(valuesLinear{r}) - repmat(bias,trials,horizon));
    stdVariance=std(G.*(valuesVariance{r}) - repmat(bias,trials,horizon));
    stdGreedy=std(G.*(valuesGreedy{r}) - repmat(bias,trials,horizon));
    stdRandom=std(G.*(valuesRandom{r}) - repmat(bias,trials,horizon));
    %sumEntropy(r,:)=sum(G.*(valuesEntropy{r} - repmat(bias,trials,horizon)),2);
    %sumLinear(r,:)=sum(G.*(valuesLinear{r} - repmat(bias,trials,horizon)),2);
    %sumVariance(r,:)=sum(G.*(valuesVariance{r}),2);
    %sumGreedy(r,:)=sum(G.*(valuesGreedy{r}),2);
    %sumRandom(r,:)=sum(G.*(valuesRandom{r}),2);

display(' ');

form='%10.2f';

display(' ');
display('Value Performance (Last Entropy)');
    display('--------------------------------');
    display(['\textbf{Random} & $' mapa '$ & --- ' ...
        ' & $' num2str(meanRandom(:,horizon),form) ' \pm ' num2str(stdRandom(:,horizon),form) ...
        '$ & $' num2str(mean(timeRandomOnline),form) ' \pm ' num2str(std(timeRandomOnline),form) ...
        '$ & --- \\']);
    display(['\textbf{Myopic} & $' mapa '$ & --- ' ...
        ' & $' num2str(meanGreedy(:,horizon),form) ' \pm ' num2str(stdGreedy(:,horizon),form) ... 
        '$ & $' num2str(mean(timeGreedyOnline),form) ' \pm ' num2str(std(timeGreedyOnline),form) ...
        '$ & --- \\']);
    display(['\textbf{PB-Entropy} & $' mapa '$ & $' num2str(bsize) ...
        '$ & $' num2str(meanEntropy(:,horizon),form) ' \pm ' num2str(stdEntropy(:,horizon),form) ...
        '$ & $' num2str(mean(timeEntropyOnline),form) ' \pm ' num2str(std(timeEntropyOnline),form) ...
        '$ & $' num2str(timeEntropyOffline/1000,form) ' \pm ' num2str(std(timeEntropyOffline/1000),form) '$ \\' ]);
    display(['\textbf{PB-Quadratic} & $' mapa '$ & $' num2str(bsize) ...
        '$ & $' num2str(meanVariance(:,horizon),form) ' \pm ' num2str(stdVariance(:,horizon),form) ...
        '$ & $' num2str(mean(timeVarianceOnline),form) ' \pm ' num2str(std(timeVarianceOnline),form) ...
        '$ & $' num2str(timeVarianceOffline/1000,form) ' \pm ' num2str(std(timeVarianceOffline/1000),form) '$\\' ]);
    display(['\textbf{PB-Linear} & $' mapa '$ & $' num2str(bsize) ...
        '$ & $' num2str(meanLinear(:,horizon),form) ' \pm ' num2str(stdLinear(:,horizon),form) ...
        '$ & $' num2str(mean(timeLinearOnline),form) ' \pm ' num2str(std(timeLinearOnline),form) ...
        '$ & $' num2str(timeLinearOffline/1000,form) ' \pm ' num2str(std(timeLinearOffline/1000),form) '$\\' ]);
  


    
%display('Value Performance (Sum of Entropy)');
%display('--------------------------------');
%display(['Entropy = ' num2str(mean(mean(sumEntropy))) ' +- ' num2str(mean(std(sumEntropy)))]);
%display(['Linear = ' num2str(mean(mean(sumLinear))) ' +- ' num2str(mean(std(sumLinear)))]);
%display(['Variance = ' num2str(mean(mean(sumVariance))) ' +- ' num2str(mean(std(sumVariance)))]);
%display(['Greedy = ' num2str(mean(mean(sumGreedy))) ' +- ' num2str(mean(std(sumGreedy)))]);
%display(['Random = ' num2str(mean(mean(sumRandom))) ' +- ' num2str(mean(std(sumRandom)))]);
%display(' ');
%display('Online Time Performance');
%display('--------------------------------');
%display(['Entropy = ' num2str(mean(mean(timeEntropyOnline))) ' +- ' num2str(mean(std(timeEntropyOnline)))]);
%display(['Linear = ' num2str(mean(mean(timeLinearOnline))) ' +- ' num2str(mean(std(timeLinearOnline)))]);
%display(['Variance = ' num2str(mean(mean(timeVarianceOnline))) ' +- ' num2str(mean(std(timeVarianceOnline)))]);
%display(['Greedy = ' num2str(mean(mean(timeGreedyOnline))) ' +- ' num2str(mean(std(timeGreedyOnline)))]);
%display(['Random = ' num2str(mean(mean(timeRandomOnline))) ' +- ' num2str(mean(std(timeRandomOnline)))]);
%display(' ');
%display('Offline Time Performance');
%display('--------------------------------');
%display(['Entropy = ' num2str(mean(timeEntropyOffline)) ' +- ' num2str(std(timeEntropyOffline))]);
%display(['Linear = ' num2str(mean(timeLinearOffline)) ' +- ' num2str(std(timeLinearOffline))]);
%display(['Variance = ' num2str(mean(timeVarianceOffline)) ' +- ' num2str(std(timeVarianceOffline))]);

figure('Name',file);
hold on;
title(['Rock Diagnosis, map=' mapa ', |B|=' num2str(bsize)]);
xlabel('time');
ylabel('reward');

plot(meanEntropy,'b');
plot(meanLinear,'m');
plot(meanVariance,'g');
plot(meanGreedy,'r');
plot(meanRandom,'c');
%plot(repmat(log(cells),1,horizon),'--k')

legend('PB-Entropy','PB-Linear','PB-Quadratic','Myopic','Random')

%plot(mean(meanEntropy),'b');
%plot(mean(meanLinear),'m');
%plot(mean(meanVariance),'g');
%plot(mean(meanGreedy),'r');
%plot(mean(meanRandom),'c');
%plot(repmat(log(cells),1,horizon),'--k')

%legend('PB-Entropy','PB-Linear','PB-Quadratic','Myopic','Random')
%title(file);
%xlabel('time');
%ylabel('reward');

%plot(mean(meanEntropy),'b');
%plot(mean(meanLinear),'m');
%plot(mean(meanVariance),'g');
%plot(mean(meanGreedy),'r');
%plot(mean(meanRandom),'c');
%plot(repmat(log(cells),1,horizon),'--k')

%legend('Entropy','Linear','Variance','Greedy','Random')

end