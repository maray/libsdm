function CSCreate(rooms,childs,trials,horizon,params)
libsdm

base=false;
if nargin == 4
    base=true;
end

display('Loading rhoPOMDP');
pomdp=ChildSurv.getPomdp(ChildSurv.RTYPE_ENTROPY,rooms,childs);
sim=PomdpSimulator(pomdp);

if (base)
    file=['CS-r' num2str(rooms) '-c' num2str(childs) '-baseline.mat'];
    online=PomdpGreedy(pomdp);
    myopic.values=zeros(trials,horizon);
    myopic.time=zeros(trials,1);
    brownian.time=zeros(trials,1);
    brownian.values=zeros(trials,horizon);
    for i=1:trials
        sim.setVerbose(false);
        if i==1
            sim.setVerbose(true);
        end
        results=sim.simulate(online,horizon);
        myopic.values(i,:)=results.getArray();
        myopic.time(i)=online.getStats().totalTime();
    end
    display('Random Simulation');
    online=PomdpRandom(pomdp);
    for i=1:trials
        sim.setVerbose(false);
        if i==1
            sim.setVerbose(true);
        end
        results=sim.simulate(online,horizon);
        brownian.values(i,:)=results.getArray();
        brownian.time(i)=online.getStats().totalTime();
    end
    save(file,'rooms','childs','horizon','trials', 'brownian','myopic');
else
    file=['CS-r' num2str(rooms) '-c' num2str(childs) '-' params.identifier '.mat'];

     %PWLC
    pomdpPwlc=ChildSurv.getPomdp(ChildSurv.RTYPE_LINEAR,rooms,childs); 
    pbPwlc=CreateAlgorithm(pomdpPwlc,params);
    display('===PWLC===');
    display('Running offline algorithm');
    pbPwlc.run();
    algo.pwlc.offline=pbPwlc.getStats().totalTime();
    algo.pwlc.iterations=pbPwlc.getStats().iteration_vector_count.toArray();
    online=ExecPomdpVF(pomdp,pbPwlc);
    algo.pwlc.values=zeros(trials,horizon);
    algo.pwlc.time=zeros(trials,1);
    display('Simulation');
    for i=1:trials
         sim.setVerbose(false);
         if i==1
            sim.setVerbose(true);
         end
         results=sim.simulate(online,horizon);
         algo.pwlc.values(i,:)=results.getArray();
         algo.pwlc.time(i)=online.getStats().totalTime();
    end
     clear online pbPwlc pomdpPwlc;

	%Entropy
  pbEntropy=CreateAlgorithm(pomdp,params);
  display('===NEGENTROPY===');
  display('Running offline algorithm');
   pbEntropy.run();
   algo.entropy.offline=pbEntropy.getStats().totalTime();
   algo.entropy.iterations=pbEntropy.getStats().iteration_vector_count.toArray();
  online=ExecPomdpVF(pomdp,pbEntropy);
   algo.entropy.values=zeros(trials,horizon);
   algo.entropy.time=zeros(trials,1);
   display('Simulation');
   for i=1:trials
        sim.setVerbose(false);
        if i==1
           sim.setVerbose(true);
        end
        results=sim.simulate(online,horizon);
        algo.entropy.values(i,:)=results.getArray();
        algo.entropy.time(i)=online.getStats().totalTime();
   end
   clear online pbEntropy;
  
  % QUADRATIC
    
   pomdpQuad=ChildSurv.getPomdp(ChildSurv.RTYPE_VARIANCE,rooms,childs);
   pbQuad=CreateAlgorithm(pomdpQuad,params);
   display('===QUADRATIC===');
   display('Running offline algorithm');
   pbQuad.run();
   algo.quadratic.offline=pbQuad.getStats().totalTime();
   algo.quadratic.iterations=pbQuad.getStats().iteration_vector_count.toArray();
   online=ExecPomdpVF(pomdp,pbQuad);
   algo.quadratic.values=zeros(trials,horizon);
   algo.quadratic.time=zeros(trials,1);
   display('Simulation');
   for i=1:trials
        sim.setVerbose(false);
        if i==1
           sim.setVerbose(true);
        end
        results=sim.simulate(online,horizon);
        algo.quadratic.values(i,:)=results.getArray();
        algo.quadratic.time(i)=online.getStats().totalTime();
   end
    clear online pbQuad pomdpQuad;
    
  
    switch params.algo
        case 'PERSEUS',
            perseus=algo;
            clear algo;
            save(file,'rooms','childs','horizon','trials','perseus','params');
        case 'PBVI',
            pbvi=algo;
            clear algo;
            save(file,'rooms','childs','horizon','trials','pbvi','params')
        case 'HSVI',
            hsvi=algo;
            clear algo;
            save(file,'rooms','childs','horizon','trials','hsvi','params')
    end
     
    end

end
