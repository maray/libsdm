function CCPrintResults(file,gamma)

load(file);

G=gamma*ones(trials,horizon);
G(:,1)=ones(trials,1);
G=cumprod(G,2);

base_file=[typ '-base-c' num2str(cells) '-h' num2str(horizon) '-t'  num2str(trials) '-r' num2str(repts) '.mat'];

load(base_file);

n=2*2*cells*cells;

meanEntropy=zeros(repts,horizon);
meanLinear=zeros(repts,horizon);
meanVariance=zeros(repts,horizon);
meanGreedy=zeros(repts,horizon);
meanRandom=zeros(repts,horizon);
stdEntropy=zeros(repts,horizon);
stdLinear=zeros(repts,horizon);
stdVariance=zeros(repts,horizon);
stdGreedy=zeros(repts,horizon);
stdRandom=zeros(repts,horizon);
sumEntropy=zeros(repts,trials);
sumLinear=zeros(repts,trials);
sumVariance=zeros(repts,trials);
sumGreedy=zeros(repts,trials);
sumRandom=zeros(repts,trials);


for r=1:repts
    meanEntropy(r,:)=mean(G.*(valuesEntropy{r} + repmat(log(cells/n),trials,horizon)));
    meanLinear(r,:)=mean(G.*(valuesLinear{r} + repmat(log(cells/n),trials,horizon)));
    meanVariance(r,:)=mean(G.*(valuesVariance{r} + repmat(log(cells/n),trials,horizon)));
    meanGreedy(r,:)=mean(G.*(valuesGreedy{r} +  repmat(log(cells/n),trials,horizon)));
    meanRandom(r,:)=mean(G.*(valuesRandom{r} +  repmat(log(cells/n),trials,horizon)));
    stdEntropy(r,:)=std(G.*(valuesEntropy{r} + repmat(log(cells/n),trials,horizon)));
    stdLinear(r,:)=std(G.*(valuesLinear{r} + repmat(log(cells/n),trials,horizon)));
    stdVariance(r,:)=std(G.*(valuesVariance{r} + repmat(log(cells/n),trials,horizon)));
    stdGreedy(r,:)=std(G.*(valuesGreedy{r} +  repmat(log(cells/n),trials,horizon)));
    stdRandom(r,:)=std(G.*(valuesRandom{r} +  repmat(log(cells/n),trials,horizon)));
    sumEntropy(r,:)=sum(G.*(valuesEntropy{r} + repmat(log(cells/n),trials,horizon)),2);
    sumLinear(r,:)=sum(G.*(valuesLinear{r} + repmat(log(cells/n),trials,horizon)),2);
    sumVariance(r,:)=sum(G.*(valuesVariance{r} + repmat(log(cells/n),trials,horizon)),2);
    sumGreedy(r,:)=sum(G.*(valuesGreedy{r} +  repmat(log(cells/n),trials,horizon)),2);
    sumRandom(r,:)=sum(G.*(valuesRandom{r} +  repmat(log(cells/n),trials,horizon)),2);
 
end 

form='%10.2f';

display(' ');
if (strcmp(typ,'CCD') || strcmp(typ,'CCL'))
    display('Value Performance (Last Entropy)');
    display('--------------------------------');
    display(['\textbf{PB-H} & $' num2str(cells) '$ & $' num2str(bsize) ...
        '$ & $' num2str(mean(meanEntropy(:,horizon)),form) ' \pm ' num2str(mean(stdEntropy(:,horizon)),form) ...
        '$ & $' num2str(mean(mean(timeEntropyOnline)),form) ' \pm ' num2str(mean(std(timeEntropyOnline)),form) ...
        '$ & $' num2str(mean(timeEntropyOffline/1000),form) ' \pm ' num2str(std(timeEntropyOffline/1000),form) '$ \\' ]);
    display(['\textbf{PB-V} & $' num2str(cells) '$ & $' num2str(bsize) ...
        '$ & $' num2str(mean(meanVariance(:,horizon)),form) ' \pm ' num2str(mean(stdVariance(:,horizon)),form) ...
        '$ & $' num2str(mean(mean(timeVarianceOnline)),form) ' \pm ' num2str(mean(std(timeVarianceOnline)),form) ...
        '$ & $' num2str(mean(timeVarianceOffline/1000),form) ' \pm ' num2str(std(timeVarianceOffline/1000),form) '$\\' ]);
    display(['\textbf{PB-L} & $' num2str(cells) '$ & $' num2str(bsize) ...
        '$ & $' num2str(mean(meanLinear(:,horizon)),form) ' \pm ' num2str(mean(stdLinear(:,horizon)),form) ...
        '$ & $' num2str(mean(mean(timeLinearOnline)),form) ' \pm ' num2str(mean(std(timeLinearOnline)),form) ...
        '$ & $' num2str(mean(timeLinearOffline/1000),form) ' \pm ' num2str(std(timeLinearOffline/1000),form) '$\\' ]);
    display(['\textbf{Random} & $' num2str(cells) '$ & --- ' ...
        ' & $' num2str(mean(meanRandom(:,horizon)),form) ' \pm ' num2str(mean(stdRandom(:,horizon)),form) ...
        '$ & $' num2str(mean(mean(timeRandomOnline)),form) ' \pm ' num2str(mean(std(timeRandomOnline)),form) ...
        '$ & --- \\']);
    display(['\textbf{Myopic} & $' num2str(cells) '$ & --- ' ...
        ' & $' num2str(mean(meanGreedy(:,horizon)),form) ' \pm ' num2str(mean(stdGreedy(:,horizon)),form) ... 
        '$ & $' num2str(mean(mean(timeGreedyOnline)),form) ' \pm ' num2str(mean(std(timeGreedyOnline)),form) ...
        '$ & --- \\']);
else
    display('Value Performance (Sum of Entropy)');
    display('--------------------------------');
     display(['\textbf{PB-H} & $' num2str(cells) '$ & $' num2str(bsize) ...
        '$ & $' num2str(mean(mean(sumEntropy)),form) ' \pm ' num2str(mean(std(sumEntropy)),form) ...
        '$ & $' num2str(mean(mean(timeEntropyOnline)),form) ' \pm ' num2str(mean(std(timeEntropyOnline)),form) ...
        '$ & $' num2str(mean(timeEntropyOffline/1000),form) ' \pm ' num2str(std(timeEntropyOffline/1000),form) '$\\' ]);
    display(['\textbf{PB-V} & $' num2str(cells) '$ & $' num2str(bsize) ...
        '$ & $' num2str(mean(mean(sumVariance)),form) ' \pm ' num2str(mean(std(sumVariance)),form) ...
        '$ & $' num2str(mean(mean(timeVarianceOnline)),form) ' \pm ' num2str(mean(std(timeVarianceOnline)),form) ...
        '$ & $' num2str(mean(timeVarianceOffline/1000),form) ' \pm ' num2str(std(timeVarianceOffline/1000),form) '$\\' ]);
    display(['\textbf{PB-L} & $' num2str(cells) '$ & $' num2str(bsize) ...
        '$ & $' num2str(mean(mean(sumLinear)),form) ' \pm ' num2str(mean(std(sumLinear)),form) ...
        '$ & $' num2str(mean(mean(timeLinearOnline)),form) ' \pm ' num2str(mean(std(timeLinearOnline)),form) ...
        '$ & $' num2str(mean(timeLinearOffline/1000),form) ' \pm ' num2str(std(timeLinearOffline/1000),form) '$\\' ]);
    display(['\textbf{Random} & $' num2str(cells) '$ & --- ' ...
        ' & $' num2str(mean(mean(sumRandom)),form) ' \pm ' num2str(mean(std(sumRandom)),form) ...
        '$ & $' num2str(mean(mean(timeRandomOnline)),form) ' \pm ' num2str(mean(std(timeRandomOnline)),form) ...
        '$ & --- \\']);
    display(['\textbf{Myopic} & $' num2str(cells) '$ & --- ' ...
        ' & $' num2str(mean(mean(sumGreedy)),form) ' \pm ' num2str(mean(std(sumGreedy)),form) ... 
        '$ & $' num2str(mean(mean(timeGreedyOnline)),form) ' \pm ' num2str(mean(std(timeGreedyOnline)),form) ...
        '$ & --- \\']);
    %display(['Entropy = ' num2str(mean(mean(sumEntropy))) ' +- ' num2str(mean(std(sumEntropy)))]);
    %display(['Linear = ' num2str(mean(mean(sumLinear))) ' +- ' num2str(mean(std(sumLinear)))]);
    %display(['Variance = ' num2str(mean(mean(sumVariance))) ' +- ' num2str(mean(std(sumVariance)))]);
    %display(['Greedy = ' num2str(mean(mean(sumGreedy))) ' +- ' num2str(mean(std(sumGreedy)))]);
    %display(['Random = ' num2str(mean(mean(sumRandom))) ' +- ' num2str(mean(std(sumRandom)))]);
end
%display(' ');
%display('Online Time Performance');
%display('--------------------------------');
%display(['Entropy = ' num2str(mean(mean(timeEntropyOnline))) ' \pm ' num2str(mean(std(timeEntropyOnline)))]);
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

if strcmp(typ,'CCS')
    mytitle='Camera Clean (surveillance)';
end
if strcmp(typ,'CCD')
    mytitle='Camera Clean (diagnosis)';
end
if strcmp(typ,'CCL')
    mytitle='Camera Clean (localization)';
end

title([mytitle ', zones=' num2str(cells) ', |B|=' num2str(bsize)]);
xlabel('time');
ylabel('reward');

plot(mean(meanEntropy),'b');
plot(mean(meanLinear),'m');
plot(mean(meanVariance),'g');
plot(mean(meanGreedy),'r');
plot(mean(meanRandom),'c');
%plot(repmat(log(cells),1,horizon),'--k')

legend('PB-Entropy','PB-Linear','PB-Quadratic','Myopic','Random')

end