
y=0.5:0.1:1.0;

figure('Name','Algorithms','NumberTitle','off')
xlabel('Chain(prob)', 'FontSize', 18);
ylabel('Value', 'FontSize', 18);
axis([0.45 1.05 2.0 8.0])
hold on
%plot(y,muExp,'g');
%plot(y,muExpEps,'--g');
errorbar(y,muExp,1.96*sqrt(varExp/500),'g');
%errorbar(y,muBetaN2,sqrt(varBetaN2),'--r');
%errorbar(y,muBetaH,sqrt(varBetaH),':r');
%errorbar(y,muBetaH2,sqrt(varBetaH2),'-.r');
errorbar(y,muBeta,1.96*sqrt(varBeta/500),'-.r');
%plot(y,muBeta,':r');
%plot(y,muBetaH,'-.r');
errorbar(y,muBetaH,1.96*sqrt(varBetaH/500),':r');
%plot(y,muBetaH,':r');
%plot(y,muBetaH2,'-.r');
%plot(y,muEta,'--b');
errorbar(y,muEta,1.96*sqrt(varEta/500),'--b');
%plot(y,muEtaH2,'--b');
%plot(y,muUto,'k');
%legend('exploit','beb(2H^2)','beb(H)','bouh(H)','utopic');
legend('exploit','beb(2H^2)','beb(H)','bouh(H)');
hold off;