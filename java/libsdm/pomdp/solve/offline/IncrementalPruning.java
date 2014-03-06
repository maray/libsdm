package libsdm.pomdp.solve.offline;

import java.util.ArrayList;

import libsdm.common.IterationStats;
import libsdm.pomdp.AlphaVector;
import libsdm.pomdp.BeliefValueFunction;
import libsdm.pomdp.Pomdp;

public class IncrementalPruning extends AlphaIteration {

    private double delta;
    
    public IncrementalPruning(Pomdp pomdp, double delta,int horizon) {
    	startInit(pomdp,horizon);
    	this.delta = delta;
    	current = pomdp.getEmptyValueFunction();
    	current.push(pomdp.getEmptyAlpha());
    	endInit();
    }

    public IterationStats iterate() {
	startIteration();
	old = current;
	int time=horizon - count() - 1;
	AlphaVectorStats iterationStats = (AlphaVectorStats) this.iterationStats;
	current = pomdp.getEmptyValueFunction();
	for (int a = 0; a < pomdp.actions(); a++) {
	    // Perform Projections
	    ArrayList<BeliefValueFunction> psi = new ArrayList<BeliefValueFunction>();
	    for (int o = 0; o < pomdp.observations(); o++) {
		BeliefValueFunction proj = pomdp.getEmptyValueFunction();
		for (int idx = 0; idx < old.size(); idx++) {
		    AlphaVector alpha = old.getAlpha(idx);
		    AlphaVector res = pomdp.projectAlpha(alpha, a, o,time);
		    proj.push(res);
		}
		iterationStats.registerLp(proj.prune(delta));
		psi.add(proj);
	    }
	    BeliefValueFunction rewFunc = pomdp.getRewardValueFunction(a,time);
	    // rewFunc.scale(1.0/(double)bmdp.nrObservations());
	    psi.add(rewFunc);
	    // Now Cross sum...
	    while (psi.size() > 1) {
		BeliefValueFunction vfA = psi.remove(0);
		BeliefValueFunction vfB = psi.remove(0);
		vfA.crossSum(vfB);
		iterationStats.registerLp(vfA.prune(delta));
		psi.add(vfA);
	    }
	    BeliefValueFunction vfA = psi.remove(0);
	    current.merge(vfA);
	}
	iterationStats.registerLp(current.prune(delta));
	//System.out.println(current);
	endIteration();
	return iterationStats;
    }

    
	@Override
	public void reset() {
    	current = pomdp.getEmptyValueFunction();
    	current.push(pomdp.getEmptyAlpha());
    	super.reset();
	}

    
}
