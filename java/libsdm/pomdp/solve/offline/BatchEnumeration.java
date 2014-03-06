/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: SparseMatrix.java
 * Description: Wrapper for sparse vector implementation.
 * Copyright (c) 2010, 2011 Mauricio Araya
 --------------------------------------------------------------------------- */

package libsdm.pomdp.solve.offline;

import java.util.ArrayList;

import libsdm.common.IterationStats;
import libsdm.pomdp.AlphaVector;
import libsdm.pomdp.BeliefValueFunction;
import libsdm.pomdp.Pomdp;

public class BatchEnumeration extends AlphaIteration {
	

    double delta;

    public BatchEnumeration(Pomdp pomdp, double delta,int horizon) {
    	startInit(pomdp,horizon);
    	this.delta = delta;
    	current = pomdp.getEmptyValueFunction();
    	current.push(pomdp.getEmptyAlpha());
    	endInit();
    }

    

    public IterationStats iterate() {
	startIteration();
	old = current;
	AlphaVectorStats iterationStats = (AlphaVectorStats) this.iterationStats;
	int time=horizon - count() - 1;
	current = pomdp.getEmptyValueFunction();
	for (int a = 0; a < pomdp.actions(); a++) {
		ArrayList<BeliefValueFunction> psi = new ArrayList<BeliefValueFunction>();
	    for (int o = 0; o < pomdp.observations(); o++) {
		BeliefValueFunction proj = pomdp.getEmptyValueFunction();
		for (int idx = 0; idx < old.size(); idx++) {
		    AlphaVector alpha = old.getAlpha(idx);
		    AlphaVector res = pomdp.projectAlpha(alpha, a, o,time);
		    proj.push(res);
		}
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
		psi.add(vfA);
	    }
	    BeliefValueFunction vfA = psi.remove(0);
	    current.merge(vfA);
	}
    //System.out.println(current);
	//System.out.println(current);
	// Prune hack
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
