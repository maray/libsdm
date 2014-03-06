/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: ValueConvergence.java
 * Description: 
 * Copyright (c) 2010, 2011 Mauricio Araya
 --------------------------------------------------------------------------- */

package libsdm.pomdp.solve.offline;

import libsdm.common.Criterion;
import libsdm.common.Iteration;
import libsdm.pomdp.BeliefValueFunction;

public class AlphaConvergence extends Criterion {

	public static final int MAX = 0;
	public static final int MAXEUCLID = 2;
	public static final int MAXDIST = 1;
	public static final int NOSORT = 3;

    double epsilon;
    int convCriteria;
	boolean sort;
	
    static final int MIN_ITERATIONS = 5;

    public boolean check(Iteration i) {
    	AlphaIteration vi = (AlphaIteration) i;
    	if (vi.getOldValueFunction()==null || vi.getValueFunction()==null)
    		return false;
    	BeliefValueFunction newv = vi.getValueFunction();
    	BeliefValueFunction oldv = vi.getOldValueFunction();
    	double conv=newv.performance(oldv,convCriteria,sort);
    	if (verbose){
    		System.out.println("Eval(" + i.count() + ") = " + conv +" ("+newv.size()+" vectors)");
    		//System.out.println(newv);
    	}
    	//TODO: Check Hack
    	if (conv <= epsilon && i.count() > MIN_ITERATIONS){
    		if (verbose)
    			System.out.println("[STOP] Alpha Vector convergence ("+conv+" < "+epsilon+")");
    		return (true); 
    	}
    	return false;
    }

    public void setSort(boolean sort){
    	this.sort=sort;
    }
    
    @Override
    public boolean valid(Iteration vi) {
    	if (vi instanceof AlphaIteration) {
    		return true;
    	}
	return false;
    }

    public AlphaConvergence(double epsilon, int convCriteria) {
    	this.epsilon = epsilon;
    	this.convCriteria = convCriteria;
    	this.setSort(true);
    	this.setVerbose(false);
    }
    
    public AlphaConvergence(double epsilon) {
    	this(epsilon,MAX);
    }



}
