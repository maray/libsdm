horizon=150;
trials=100;
max_beta=horizon;
max_eta=horizon;
max_eps=1;
seeds=randint(trials,1,10000);
tests=50;

y=0:tests;

figure('Name','Beb','NumberTitle','off')
hold on
xlabel('\beta', 'FontSize', 18);
ylabel('Value', 'FontSize', 18);
yBeb=y*max_beta/tests;
% errorbar(yBeb,muBeb05,1.96*sqrt(varBeb05/trials),'r');
% errorbar(yBeb,muBeb06,1.96*sqrt(varBeb06/trials),'g');
% errorbar(yBeb,muBeb07,1.96*sqrt(varBeb07/trials),'b');
% errorbar(yBeb,muBeb08,1.96*sqrt(varBeb08/trials),'c');
% errorbar(yBeb,muBeb09,1.96*sqrt(varBeb09/trials),'m');
% errorbar(yBeb,muBeb10,1.96*sqrt(varBeb10/trials),'k');
%plot(yBeb,muBeb05,'r');
%plot(yBeb,muBeb07,'b');
%plot(yBeb,muBeb09,'m');
plot(yBeb,muBeb10,'k');
plot(yBeb,muBeb08,'-.b');
plot(yBeb,muBeb06,'--r');
legend('p=1.0','p=0.8','p=0.6');
% plot(yBeb,ones(tests+1,1)*muUto05,'r');
% plot(yBeb,ones(tests+1,1)*muUto06,'g');
% plot(yBeb,ones(tests+1,1)*muUto07,'b');
% plot(yBeb,ones(tests+1,1)*muUto08,'c');
% plot(yBeb,ones(tests+1,1)*muUto09,'m');
% plot(yBeb,ones(tests+1,1)*muUto10,'k');
hold off

figure('Name','Bouh','NumberTitle','off')
hold on
xlabel('\eta', 'FontSize', 18);
ylabel('Value', 'FontSize', 18);
yBouh=y*max_eta/tests;
%plot(yBouh,muBouh05,'r');
%plot(yBouh,muBouh09,'m');
%plot(yBouh,muBouh07,'b');

plot(yBouh,muBouh10,'k');
plot(yBouh,muBouh08,'-.b');
plot(yBouh,muBouh06,'--r');

% errorbar(yBouh,muBouh05,1.96*sqrt(varBouh05/trials),'r');
% errorbar(yBouh,muBouh06,1.96*sqrt(varBouh06/trials),'g');
% errorbar(yBouh,muBouh07,1.96*sqrt(varBouh07/trials),'b');
% errorbar(yBouh,muBouh08,1.96*sqrt(varBouh08/trials),'c');
% errorbar(yBouh,muBouh09,1.96*sqrt(varBouh09/trials),'m');
% errorbar(yBouh,muBouh10,1.96*sqrt(varBouh10/trials),'k');
legend('p=1.0','p=0.8','p=0.6');
%legend('0.5','0.6','0.7','0.8','0.9','1.0');
% plot(yBouh,ones(tests+1,1)*muUto05,'r');
% plot(yBouh,ones(tests+1,1)*muUto06,'g');
% plot(yBouh,ones(tests+1,1)*muUto07,'b');
% plot(yBouh,ones(tests+1,1)*muUto08,'c');
% plot(yBouh,ones(tests+1,1)*muUto09,'m');
% plot(yBouh,ones(tests+1,1)*muUto10,'k');
hold off

%figure('Name','e-Exploit','NumberTitle','off')
%hold on
%xlabel('epsilon', 'FontSize', 18);
%ylabel('Value', 'FontSize', 18);
%yExp=y*max_eps/tests;
%plot(yExp,muExp05,'r');
%plot(yExp,muExp06,'g');
%plot(yExp,muExp07,'b');
%plot(yExp,muExp08,'c');
%plot(yExp,muExp09,'m');
%plot(yExp,muExp10,'k');
% errorbar(yExp,muExp05,1.96*sqrt(varExp05/trials),'r');
% errorbar(yExp,muExp06,1.96*sqrt(varExp06/trials),'g');
% errorbar(yExp,muExp07,1.96*sqrt(varExp07/trials),'b');
% errorbar(yExp,muExp08,1.96*sqrt(varExp08/trials),'c');
% errorbar(yExp,muExp09,1.96*sqrt(varExp09/trials),'m');
% errorbar(yExp,muExp10,1.96*sqrt(varExp10/trials),'k');
%legend('0.5','0.6','0.7','0.8','0.9','1.0');
% plot(yExp,ones(tests+1,1)*muUto05,'r');
% plot(yExp,ones(tests+1,1)*muUto06,'g');
% plot(yExp,ones(tests+1,1)*muUto07,'b');
% plot(yExp,ones(tests+1,1)*muUto08,'c');
% plot(yExp,ones(tests+1,1)*muUto09,'m');
% plot(yExp,ones(tests+1,1)*muUto10,'k');
%hold off
