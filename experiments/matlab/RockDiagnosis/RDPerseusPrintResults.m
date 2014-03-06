function RDPerseusPrintResults(base,bsize)

for r=1:10
    file=strcat(base,'-perseus-bs');
    file=strcat(file,num2str(bsize));
    file=strcat(file,'-r');
    file=strcat(file,num2str(r));
    file=strcat(file,'.mat');
    load(file);


    entropy.meanValue(r)=mean(perseus.entropy.values(:,horizon));
    quadratic.meanValue(r)=mean(perseus.quadratic.values(:,horizon));
    pwlc.meanValue(r)=mean(perseus.pwlc.values(:,horizon));
  
    entropy.stdValue(r)=std(perseus.entropy.values(:,horizon));
    quadratic.stdValue(r)=std(perseus.quadratic.values(:,horizon));
    pwlc.stdValue(r)=std(perseus.pwlc.values(:,horizon));
  
    entropy.meanTime(r)=mean(perseus.entropy.time);
    quadratic.meanTime(r)=mean(perseus.quadratic.time);
    pwlc.meanTime(r)=mean(perseus.pwlc.time);
    
    entropy.stdTime(r)=std(perseus.entropy.time);
    quadratic.stdTime(r)=std(perseus.quadratic.time);
    pwlc.stdTime(r)=std(perseus.pwlc.time);
    
    entropy.offline(r)=perseus.entropy.offline;
    quadratic.offline(r)=perseus.quadratic.offline;
    pwlc.offline(r)=perseus.pwlc.offline;
    
    
    entropy.iter(r)=size(perseus.entropy.iterations,1);
    quadratic.iter(r)=size(perseus.quadratic.iterations,1);
    pwlc.iter(r)=size(perseus.pwlc.iterations,1);
    
    entropy.vect(r)=perseus.entropy.iterations(end);
    quadratic.vect(r)=perseus.quadratic.iterations(end);
    pwlc.vect(r)=perseus.pwlc.iterations(end);
    
    
end

display(' ');

form='%10.2f';

display(' ');
display('\textbf{Algorithm} & \textbf{map} & \textbf{mean time} & \textbf{offline} & \textbf{iterations} & \textbf{vectors} \\ \hline'); 
display(['$\rho_H$-\textbf{' params.algo '} & ' num2str(bsize)  ...
        ' & $' num2str(mean(entropy.meanValue),form) ' \pm ' num2str(mean(entropy.stdValue),form) ...
        '$ & $' num2str(mean(entropy.meanTime),form) ' \pm ' num2str(mean(entropy.stdTime),form) ...
        '$ & $' num2str(mean(entropy.offline)/1000,form) '$ & $' num2str(mean(entropy.iter)) '$ & $' num2str(mean(entropy.vect)) '$ \\' ]);
display(['$\rho_Q$-\textbf{' params.algo '} & ' num2str(bsize) ...
        ' & $' num2str(mean(quadratic.meanValue),form) ' \pm ' num2str(mean(quadratic.stdValue),form) ...
        '$ & $' num2str(mean(quadratic.meanTime),form) ' \pm ' num2str(mean(quadratic.stdTime),form) ...
        '$ & $' num2str(mean(quadratic.offline)/1000,form) '$ & $' num2str(mean(quadratic.iter)) '$ & $' num2str(mean(quadratic.vect)) '$ \\' ]);
display(['$\rho_L$-\textbf{' params.algo '} & ' num2str(bsize) ...
        ' & $' num2str(mean(pwlc.meanValue),form) ' \pm ' num2str(mean(pwlc.stdValue),form) ...
        '$ & $' num2str(mean(pwlc.meanTime),form) ' \pm ' num2str(mean(pwlc.stdTime),form) ...
        '$ & $' num2str(mean(pwlc.offline)/1000,form) '$ & $' num2str(mean(pwlc.iter)) '$ & $' num2str(mean(pwlc.vect)) '$ \\' ]);
   
   



%h=figure('Name',file);
%hold on;
%title(['Rock Diagnosis, map=' mapa ]);
%xlabel('time');
%ylabel('reward');

%plot(ret.entropy.meanValue,'b');
%plot(ret.quadratic.meanValue,'m');
%plot(ret.pwlc.meanValue,'g');
%plot(myopic.meanValue,'r');
%plot(brownian.meanValue,'c');
%
%legend('Negentropy','Quadratic','PWLC','Myopic','Random','Location','SouthEast')
%[dir,name,ext]=fileparts(file);
%print(h,'-dpng',strcat(name,'.png'));
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