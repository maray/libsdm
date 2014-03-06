/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: BlindPolicy.java
 * Description: 
 * Copyright (c) 2010 Mauricio Araya
 --------------------------------------------------------------------------- */

package libsdm.pomdp.solve.offline;

import libsdm.common.IterationStats;
import libsdm.pomdp.AlphaVector;
import libsdm.pomdp.Pomdp;

public class BlindPolicy extends AlphaIteration {


	public BlindPolicy(Pomdp pomdp,int horizon) {
		startInit(pomdp,horizon);
		current = pomdp.getLowerBound();
		endInit();	
    }

    @Override
    public IterationStats iterate() {
    	startIteration();
    	old = current;
    	current=pomdp.getEmptyValueFunction();
    	int time=horizon - count() - 1;
    	for (int a = 0; a < pomdp.actions(); a++) {
    		AlphaVector vec = old.getAlpha(a);
    		AlphaVector res = pomdp.blindProjection(vec, a,time);
    		current.push(res);
    	}
	//System.out.println(current);
    	endIteration();
    	return iterationStats;
    }


	@Override
	public void reset() {
		current = pomdp.getLowerBound();
		super.reset();
	}

}