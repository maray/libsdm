/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: ValueConvergence.java
 * Description: 
 * Copyright (c) 2010, 2011 Mauricio Araya
 --------------------------------------------------------------------------- */

package libsdm.mdp;

import libsdm.common.Criterion;
import libsdm.common.Iteration;

public class ValueConvergence extends Criterion {


	public static final int MAXEUCLID = 2;
	public static final int MAXDIST = 1;
	public static final int MAX = 0;

    double epsilon;
    int convCriteria;

    static final int MIN_ITERATIONS = 5;

    public boolean check(Iteration i) {
    	ValueIteration vi = (ValueIteration) i;
    	if (vi.getOldValueFunction()==null || vi.getValueFunction()==null)
    		return false;
    	ValueFunction newv = vi.getValueFunction();
    	ValueFunction oldv = vi.getOldValueFunction();
    	double conv=newv.performance(oldv,convCriteria);
    	if (verbose) System.out.println("Eval(" + i.count() + ") = " + conv);
    	if (conv <= epsilon && i.count() > MIN_ITERATIONS){
    		if (verbose)
    			System.out.println("[STOP] Value convergence ("+conv+" < "+epsilon+")");
    		return (true); 
    	}
    	return false;
    }

    @Override
    public boolean valid(Iteration vi) {
    	if (vi instanceof ValueIteration) {
    		return true;
    	}
	return false;
    }

    public ValueConvergence(double epsilon, int convCriteria) {
    	this.epsilon = epsilon;
    	this.convCriteria = convCriteria;
    }

	public ValueConvergence(double epsilon) {
		this(epsilon,MAX);
	}

}
