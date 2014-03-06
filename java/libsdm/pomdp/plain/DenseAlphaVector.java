/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: SparseMatrix.java
 * Description: This class represents an alpha-vector based on 
 * a custom vector and an action. 
 * Copyright (c) 2010 Mauricio Araya
 --------------------------------------------------------------------------- */

package libsdm.pomdp.plain;


import libsdm.common.DenseVector;
import libsdm.common.SparseVector;
import libsdm.common.Utils;
import libsdm.mdp.TransitionMatrix;
import libsdm.mdp.ValueFunction;
import libsdm.pomdp.AlphaVector;
import libsdm.pomdp.BeliefState;
import libsdm.pomdp.ObservationMatrix;



public class DenseAlphaVector extends DenseVector implements AlphaVector {

    /**
	 * 
	 */
	private static final long serialVersionUID = -9053379653819618307L;
	protected int a;
	//protected boolean masked;
	//private int[] mask;
	
	public static DenseAlphaVector transform(DenseVector vec){
		return new DenseAlphaVector(vec,true);
	}
	
	public static DenseAlphaVector transform(DenseVector vec,int a){
		DenseAlphaVector ne=new DenseAlphaVector(vec,true);
		ne.setAction(a);
		return(ne);
	}
	
    /**
     * Constructor using an existing vector.
     * 
     * @param v
     *            the reference of the vector to use
     * @param a
     *            the action associated to the vector v
     */
    public DenseAlphaVector(DenseVector v, int a) {
    	super(v);
    	//masked=false;
    	this.a = a;
    }
    
    public DenseAlphaVector(DenseVector v) {
    	super(v);
    	//masked=false;
    	this.a = Integer.MIN_VALUE;
    }

    public DenseAlphaVector(DenseAlphaVector vec) {
    	super(vec);
    //	masked=false;
    	this.a = vec.a;
    }
    
    /**
     * Constructor by vector dimension. Creates a zero-vector associated with
     * the action -1
     * 
     * @param dim
     *            the size of the zero-vector to create
     */
    public DenseAlphaVector(int dim) {
    	this(new DenseVector(dim), -1);
    	//masked=false;
    }

    /**
     * Constructor by vector dimension and action.
     * 
     * @param dim
     *            the size of the zero-vector to create
     * @param a
     *            the action associated to the vector v
     */
    public DenseAlphaVector(int dim, int a) {
    	this(new DenseVector(dim), a);
    //	masked=false;
    }

    public DenseAlphaVector(DenseVector internal,boolean shallow) {
		super(internal,shallow);
	//	masked=false;
	}

	/**
     * Evaluates a belief-point for this alpha.
     * 
     * @param bel
     *            the belief-state point
     * @return the dot product between both vectors.
     */
    public double eval(BeliefState bel) {
    	SparseBeliefState b = (SparseBeliefState)bel;
    	/*if (masked){
    		//System.out.println("Masked");
    		int[] nmas = b.getMask();
    		if (nmas==null) {
    			Utils.error("[Stop] due to malformed b");
    			//System.out.println(b);
    			return Double.NaN;
    		}
    		int j=0;
    		for (int i=0;i<nmas.length;i++){
    			int se=nmas[i];
    			while (j < mask.length && mask[j] < se)
    				j++;
    			if (j==mask.length) continue;
    			if (mask[j]!=se)
    				return Double.NaN;
    		}
    	}
    	*/
    	return (dot((SparseBeliefState)b));
    }

    /**
     * Create a proper copy of the alpha-vector.
     * 
     * @return an alpha-vector copy
     */
    public DenseAlphaVector copy() {
    	return new DenseAlphaVector(this);
    }


    /**
     * Compare to an alpha-vector with delta tolerance.
     * 
     * @param vec
     *            the vector to compare to
     * @param delta
     *            maximum difference between them for considering them equqls.
     * @return zero if (almost) equal, positive if is higher, and negative if is
     *         lower;
     */
    //public int compareTo(AlphaVector vec, double delta) {
    //	SparseAlphaVector v=(SparseAlphaVector) vec;
    //	return (((SparseVector)v,delta));
    //}

	/**
     * Action setter.
     * 
     * @param a
     *            a valid action
     */
    public void setAction(int a) {
	this.a = a;
    }

    /**
     * New values for the alpha vector.
     * 
     * @param res
     *            the alpha vector to copy from
     */
    public void set(AlphaVector res) {
    	DenseAlphaVector vec=(DenseAlphaVector) res;
    	set((DenseVector)vec);
    	setAction(res.getAction());
    }

    /**
     * Change one value of the alpha-vector.
     * 
     * @param idx
     *            the vector index
     * @param value
     *            the nez value
     */
    /*public void setValueExpensive(int idx, double value) {
    	assign(idx, value);
    }
    */


	/**
     * Add the values of other alpha-vector. This does not modify the action
     * value.
     * 
     * @param alpha
     *            the alpha-vector to add
     */
    public void add(AlphaVector alpha) {
    	if (alpha instanceof SparseAlphaVector){
    		SparseAlphaVector res = (SparseAlphaVector)alpha;
    		add((SparseVector)res);
    	}
    	else{
    		DenseAlphaVector res = (DenseAlphaVector)alpha;
    		add((DenseVector)res);
    	}
    	
    }

	public int getAction() {
		return a;
	}

	
	public DenseAlphaVector project(TransitionMatrix model, int a, int i) {
		return transform(model.project(this,a,i),a);
	}
	
	public DenseAlphaVector project(ObservationMatrix model, int a) {
		return transform(model.project(this,a),a);
	}
	

	//public void applyMask(int[] mask) {
	//	//this.mask=new int[mask.length];
	//	this.mask=Arrays.copyOf(mask, mask.length);
	//	masked=true; 
		//super.applyMask(mask);
	
	//}

	public void set(ValueFunction valueFunction) {
		set((AlphaVector)transform(new DenseVector(valueFunction.getArray())));
	}

	public double gap(AlphaVector retval) {
		DenseAlphaVector re=(DenseAlphaVector)retval;
		DenseAlphaVector perf=re.copy();
		perf.minus((DenseVector)this);
		return(perf.norm(Double.POSITIVE_INFINITY));
	}

	public int compareTo(AlphaVector testVec, double delta) {
		if (testVec instanceof DenseAlphaVector){
			DenseAlphaVector t = (DenseAlphaVector)testVec;
			return (compare(t,delta));
		}
		Utils.error("Mixing Dense and Sparse AlphaVectors");
		return -1;
	}


	public void diff(AlphaVector in) {
		if (in instanceof DenseAlphaVector){
			DenseAlphaVector t = (DenseAlphaVector)in;
			this.minus(t);
			return;
		}
		Utils.error("Mixing Dense and Sparse AlphaVectors");
	}

    public int compareTo(AlphaVector vec) {
    	return (compareTo(vec,0));
    }

	public void selectMax(AlphaVector alpha) {
		super.selectMax((DenseAlphaVector)alpha);
		
	}

}
