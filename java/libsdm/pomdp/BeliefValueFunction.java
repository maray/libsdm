/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: BeliefValueFunction.java
 * Description: representation of a set of alpha vectors and their
 *              associated actions for direct control (if possible)
 * Copyright (c) 2009, 2010, 2011 Diego Maniloff 
 * Copyright (c) 2010, 2011 Mauricio Araya
 --------------------------------------------------------------------------- */

package libsdm.pomdp;

import libsdm.mdp.ValueFunction;
import libsdm.pomdp.plain.SparseBeliefState;

public interface BeliefValueFunction extends ValueFunction {

    public double value(BeliefState b);

    public int[] getActions();

    public AlphaVector getAlpha(int idx);

    public int size();

    public void sort();

	public BeliefValueFunction copy();

	public boolean push(AlphaVector vec);

	//public AlphaVector getUpperBound();

	public void crossSum(BeliefValueFunction rewardValueFunction);

	public long prune(double delta);

	public void merge(BeliefValueFunction vfA);

	public long prune();
	
	public void dominationCheck(double delta);
	
	public void dominationCheck();

	public AlphaVector getBestAlpha(BeliefState bel);

	public boolean member(AlphaVector alpha, double i);

	public double performance(BeliefValueFunction oldv, int convCriteria,
			boolean sort);

	public AlphaVector getUpperBound();

	public BeliefState find_region(AlphaVector alpha, double delta);

	
	//public AlphaVector getUpperBound();


} // valueFunction
