/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: SparseMatrix.java
 * Description: This class represents an alpha-vector based on 
 * a custom vector and an action. 
 * Copyright (c) 2010 Mauricio Araya
 --------------------------------------------------------------------------- */

package libsdm.pomdp;

import libsdm.mdp.TransitionMatrix;
import libsdm.mdp.ValueFunction;

public interface AlphaVector extends Comparable<AlphaVector>{


    /**
     * Evaluates a belief-point for this alpha.
     * 
     * @param bel the belief-state point
     * @return the evaluation on that point.
     */
    public double eval(BeliefState bel);

    /**
     * Get the associated action
     * 
     * @return the associated action
     */
    public int getAction();

    /**
     * Create a proper copy of the alpha-vector.
     * 
     * @return an alpha-vector copy
     */
    public AlphaVector copy();

    /**
     * Compare to an alpha-vector with delta tolerance.
     * 
     * @param vec
     *            the vector to compare to
     * @return zero if equal, positive if is higher, and negative if is lower.
     */
    //public int compareTo(AlphaVector vec);
    
    /**
     * Action setter.
     * 
     * @param a
     *            a valid action
     */
    public void setAction(int a);

    /**
     * New values for the alpha vector.
     * 
     * @param res
     *            the alpha vector to copy from
     */
    public void set(AlphaVector res);

   
    /**
     * Add the values of other alpha-vector. This does not modify the action
     * value.
     * 
     * @param alpha
     *            the alpha-vector to add
     */
    public void add(AlphaVector alpha);

    public double get(int s);

	//public void applyMask(int[] mask);

	public void set(ValueFunction valueFunction);

	public double gap(AlphaVector retval);

	public int size();

	//public int compareTo(AlphaVector testVec, double delta);

	public void diff(AlphaVector in);

	public double min();

	public void scale(double d);

	public double norm(double d);

	//public double[] getArray();

	public int compareTo(AlphaVector testVec, double delta);

	public double[] getArray();

	public void fill(double negativeInfinity);

	public void selectMax(AlphaVector alpha);

	public AlphaVector project(TransitionMatrix t, int a, int i);


    
    //public AlphaVector project(TransitionFunction model,int a, int i);
    
    //public AlphaVector project(ObservationFunction model,int a);
	
}
