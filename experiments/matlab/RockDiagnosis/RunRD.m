function RunRD(mapa,metals,trials,horizon,params)
libsdm

load([mapa '.mat']);

base=false;
if nargin == 4
    base=true;
end

display('Loading rhoPOMDP');
pomdp=RockDiagnosis.getPomdp(RockDiagnosis.RTYPE_ENTROPY,xsize,ysize,metals,rocks,x_pos,y_pos,0,0);
sim=PomdpSimulator(pomdp);

if (base)

    file=[mapa '-baseline.mat'];
    display('Myopic Simulation');
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
        results=sim.simulate(online,horizon,IntegerVector(0));
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
        results=sim.simulate(online,horizon,IntegerVector(0));
        brownian.values(i,:)=results.getArray();
        brownian.time(i)=online.getStats().totalTime();
    end
    save(file,'mapa','metals','horizon','trials', 'brownian','myopic');
else

    file=[mapa '-' params.identifier '.mat'];
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
    
    pomdpQuad=RockDiagnosis.getPomdp(RockDiagnosis.RTYPE_VARIANCE,xsize,ysize,metals,rocks,x_pos,y_pos,0,0);
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
    
     %PWLC
    
    pomdpPwlc=RockDiagnosis.getPomdp(RockDiagnosis.RTYPE_LINEAR,xsize,ysize,metals,rocks,x_pos,y_pos,0,0);
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
  
    switch params.algo
        case 'PERSEUS',
            perseus=algo;
            clear algo;
            save(file,'mapa','metals','horizon','trials','perseus','params');
        case 'PBVI',
            pbvi=algo;
            clear algo;
            save(file,'mapa','metals','horizon','trials','pbvi','params')
        case 'HSVI',
            hsvi=algo;
            clear algo;
            save(file,'mapa','metals','horizon','trials','hsvi','params')
    end
     
    end

end
