/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: belStateSparseMTJ.java
 * Description: 
 * Copyright (c) 2009, 2010, 2011 Diego Maniloff 
 * Copyright (c) 2010, 2011 Mauricio Araya  
 --------------------------------------------------------------------------- */

package libsdm.pomdp.plain;

// imports
import java.io.Serializable;

import libsdm.common.SparseVector;
import libsdm.common.Utils;
import libsdm.pomdp.BeliefState;
import libsdm.pomdp.ChildMap;

public class SparseBeliefState extends SparseAlphaVector implements BeliefState, Serializable  {

    /**
     * 
     */
    private static final long serialVersionUID = 1232321752664518575L;

	public static SparseBeliefState transform(SparseVector vec){
		return new SparseBeliefState(vec,true);
	}
	  
    // associated P(o|b,a)
    private double poba = -1.0;
	private ChildMap childMap;

    // associated alpha vector id
    //private int planid = -1;

    // constructor
    // in case this is the initial belief, poba = 0.0
    public SparseBeliefState(SparseVector bSparse, double poba) {
    	this(bSparse);
    	this.poba = poba;
    }

    public SparseBeliefState(SparseVector bSparse) {
	super(bSparse);
    }

    public SparseBeliefState(int length) {
    	super(length);
     }

	public SparseBeliefState(SparseVector vec, boolean b) {
		super(vec,b);
	}

	public double getPoba() {
	return poba;
    }

    
    public void setPoba(double poba) {
	this.poba = poba;
    }

    
   // public int getAlpha() {
	//return planid;
    //}

    
 //   public void setAlpha(int planid) {
//	this.planid = planid;
 //   }

    
    public SparseBeliefState copy() {
    	SparseBeliefState bel = new SparseBeliefState(this, poba);
		return bel;
    }

	/*public boolean compare(BeliefState bel) {
		return(compare((SparseVector)bel));
	}*/

	public SparseVector getPoint() {
		return this;
	}

	public static SparseBeliefState getUniformBelief(int size) {
		return SparseBeliefState.transform(SparseVector.getUniform(size));
	}

	public double getEntropy() {
		return getEntropy(Math.E);
	}

	public double prob(int s) {
		return(get(s));
	}
	
	/*public SparseBeliefState getRange(int ini, int range) {
		SparseDoubleMatrix1D view=(SparseDoubleMatrix1D) v.viewPart(ini, range);
		return(transform(new SparseVector(view)));
	}
	*/

	public ChildMap getChildMap() {
		return childMap;
	}

	public void initChildMap(int actions, int observations) {
		childMap=new ChildMap(actions,observations);
	}

	public int[] getMask() {
		//IntArrayList ial=new IntArrayList();
		//DoubleArrayList dal=new DoubleArrayList();
		//v.trimToSize();
		//this.v.getNonZeros(ial, null);
		//System.out.println(v.size());
		return index;
	}

	public double minratio(BeliefState bp) {
		SparseBeliefState bel=(SparseBeliefState)bp;
		double minratio=Double.POSITIVE_INFINITY;
		int j=0;
		for (int i=0;i<bel.index.length;i++){
			while(j < index.length && index[j] < bel.index[i])
				j++;
			//System.out.println("j="+j+" i="+i);
			double ratio=0;
			if (j < index.length && index[j] == bel.index[i])
				ratio=data[j]/bel.data[i];
			else
				return ratio;
			if (ratio < minratio)
				minratio=ratio;
		}
		//if (minratio>0) System.out.println("POSITIVE RATIO");
		if (minratio<0) Utils.error("Negative Ratio.. something is terribly wrong");
		if (minratio==Double.POSITIVE_INFINITY) {
			System.out.println(this);
			System.out.println(bp);
			System.out.println("this -> ");
			for (int i=0;i<index.length;i++) 
				System.out.print("("+index[i]+")="+data[i]);
			System.out.println();
			System.out.println("other -> ");
			for (int i=0;i<bel.index.length;i++) 
				System.out.print("("+bel.index[i]+")="+bel.data[i]);
			System.out.println();
			Utils.error("Infinite Ratio.. something is terribly wrong");
		}
		return minratio;
	}
	
	public static SparseBeliefState getUniform(int size){
		SparseVector re=SparseVector.getUniform(size);
		return SparseBeliefState.transform(re);
	}

	public int getCorner() {
		if (this.data.length!=1){
			return -1;
		}
		return this.index[0];
	}




} // BeliefStateStandard
