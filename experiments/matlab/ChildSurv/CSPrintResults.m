function CSPrintResults(file,gamma)

load(file);

if strcmp(params.algo,'PERSEUS') 
    ret=perseus;
    clear perseus;
end
if strcmp(params.algo,'PBVI') 
    ret=pbvi;
    clear pbvi;
end
if strcmp(params.algo,'HSVI') 
    ret=hsvi;
    clear hsvi;
end

G=gamma*ones(trials,horizon);
G(:,1)=ones(trials,1);
G=cumprod(G,2);

base_file=['CS-r' num2str(rooms) '-c' num2str(childs) '-baseline.mat'];

load(base_file);

ret.entropy.meanValue=mean(G.*ret.entropy.values);
ret.quadratic.meanValue=mean(G.*ret.quadratic.values);
ret.pwlc.meanValue=mean(G.*ret.pwlc.values);
myopic.meanValue=mean(G.*myopic.values);
brownian.meanValue=mean(G.*brownian.values);

ret.entropy.stdValue=std(G.*ret.entropy.values);
ret.quadratic.stdValue=std(G.*ret.quadratic.values);
ret.pwlc.stdValue=std(G.*ret.pwlc.values);
myopic.stdValue=std(G.*myopic.values);
brownian.stdValue=std(G.*brownian.values);



display(' ');

form='%10.2f';

display(' ');
display('\textbf{Algorithm} & \textbf{rooms} & \textbf{childs} & \textbf{mean time} & \textbf{offline} & \textbf{iterations} & \textbf{vectors} \\ \hline'); 
display(['\textbf{Random} & $' num2str(rooms) '$ & $' num2str(childs) '$ ' ...
        ' & $' num2str(mean(brownian.meanValue),form) ' \pm ' num2str(mean(brownian.stdValue),form) ...
        '$ & $' num2str(mean(brownian.time),form) ' \pm ' num2str(std(brownian.time),form) ...
        '$ & --- & --- & --- \\']);
display(['\textbf{Myopic} & $' num2str(rooms) '$ & $'  num2str(childs) '$ ' ...
        ' & $' num2str(mean(myopic.meanValue),form) ' \pm ' num2str(mean(myopic.stdValue),form) ... 
        '$ & $' num2str(mean(myopic.time),form) ' \pm ' num2str(std(myopic.time),form) ...
        '$ & --- & --- & --- \\']);
display(['$\rho_H$-\textbf{' params.algo '} (negentropy) & $' num2str(rooms) '$ & $'  num2str(childs) '$ ' ...
        '$ & $' num2str(mean(ret.entropy.meanValue),form) ' \pm ' num2str(mean(ret.entropy.stdValue),form) ...
        '$ & $' num2str(mean(ret.entropy.time),form) ' \pm ' num2str(std(ret.entropy.time),form) ...
        '$ & $' num2str(ret.entropy.offline/1000,form) '$ & $' num2str(size(ret.entropy.iterations,1)) '$ & $' num2str(ret.entropy.iterations(end)) '$ \\' ]);
display(['$\rho_Q$-\textbf{' params.algo '} (quadratic) & $' num2str(rooms) '$ & $'  num2str(childs) '$ ' ...
        '$ & $' num2str(mean(ret.quadratic.meanValue),form) ' \pm ' num2str(mean(ret.quadratic.stdValue),form) ...
        '$ & $' num2str(mean(ret.quadratic.time),form) ' \pm ' num2str(std(ret.quadratic.time),form) ...
        '$ & $' num2str(ret.quadratic.offline/1000,form) '$ & $' num2str(size(ret.quadratic.iterations,1)) '$ & $' num2str(ret.quadratic.iterations(end)) '$ \\' ]);
display(['$\rho_L$-\textbf{' params.algo '} (pwlc)& $' num2str(rooms) '$ & $'  num2str(childs) '$ ' ...
        '$ & $' num2str(mean(ret.pwlc.meanValue),form) ' \pm ' num2str(mean(ret.pwlc.stdValue),form) ...
        '$ & $' num2str(mean(ret.pwlc.time),form) ' \pm ' num2str(std(ret.pwlc.time),form) ...
        '$ & $' num2str(ret.pwlc.offline/1000,form) '$ & $' num2str(size(ret.pwlc.iterations,1)) '$ & $' num2str(ret.pwlc.iterations(end)) '$ \\' ]);
   
   



h=figure('Name',file);
hold on;
title(['Child Surv., rooms=' num2str(rooms) ' , childs=' num2str(childs) ]);
xlabel('time');
ylabel('reward');

plot(ret.entropy.meanValue,'b');
plot(ret.quadratic.meanValue,'m');
plot(ret.pwlc.meanValue,'g');
plot(myopic.meanValue,'r');
plot(brownian.meanValue,'c');

legend('Negentropy','Quadratic','PWLC','Myopic','Random','Location','SouthEast')
[dir,name,ext]=fileparts(file);
print(h,'-dpng',strcat(name,'.png'));
%system(['pdfcrop ' strcat(name,'.pdf')]);
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
