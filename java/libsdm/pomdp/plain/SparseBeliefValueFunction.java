/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: 
 * Description: 
 * Copyright (c) 2009, 2010 Diego Maniloff 

 --------------------------------------------------------------------------- */

package libsdm.pomdp.plain;

// imports
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import libsdm.common.Utils;
import libsdm.mdp.ValueConvergence;
import libsdm.mdp.ValueFunction;
import libsdm.pomdp.AlphaVector;
import libsdm.pomdp.BeliefState;
import libsdm.pomdp.BeliefValueFunction;

public class SparseBeliefValueFunction implements BeliefValueFunction, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 999938545519704337L;



    // ------------------------------------------------------------------------
    // properties
    // ------------------------------------------------------------------------

    // serial id

    // represent a value function via a Matrix object
    private ArrayList<AlphaVector> set;


    // constructor
    public SparseBeliefValueFunction() {
	set = new ArrayList<AlphaVector>();
    }

    // ------------------------------------------------------------------------
    // interface methods
    // ------------------------------------------------------------------------

    //public SparseBeliefValueFunction(double[][] v, int[] a) {
	//this();
	//for (int i = 0; i < a.length; i++) {
	 //   push(v[i], a[i]);
	//}
    //}

    // list of actions associated with each alpha
    public int[] getActions() {
	int[] retval = new int[size()];
	for (int i = 0; i < size(); i++) {
	    retval[i] = getAction(i);
	}
	return (retval);
    }

    // return value of a belief state
    public double value(BeliefState bel) {
	// long start = System.currentTimeMillis();
	double valmax = Double.NEGATIVE_INFINITY;
	//AlphaVector sel = null;
	for (AlphaVector alpha : set) {
	    double sol = alpha.eval(bel);
	    if (sol > valmax) {
		valmax = sol;
		//sel = alpha;
	    }
	}
	//bel.setAlpha(set.indexOf(sel));
	return valmax;
    }

//    public boolean push(double list[], int a) {
//	return push(new SparseVector(list), a);
 //   }

    //public boolean newAlpha(SparseVector vec, int a) {
	//return (push(new AlphaVector(vec, a)));
    //}

    public boolean push(AlphaVector ent) {
    	if (ent==null){
    		Utils.error("Added a NULL alpha vector to the Value Function");
    	}
    //	if (ent instanceof SparseAlphaVector){
    //		Utils.error("WRONF!");
    //	}
	return (set.add(ent));
    }

    public AlphaVector getAlpha(int idx) {
	return set.get(idx);
    }

    public int size() {
	return (set.size());
    }

    public SparseBeliefValueFunction copy() {
	SparseBeliefValueFunction newv = new SparseBeliefValueFunction();
	for (int i = 0; i < set.size(); i++)
	    newv.push(set.get(i).copy());
	return newv;
    }

    public int getAlphaAction(int idx) {
	return (set.get(idx).getAction());
    }

    public String toString() {
	String retval = "";
	for (int i = 0; i < size(); i++) {
	    retval += " a=" + getAlphaAction(i) +" v"+i +" \t";
	    AlphaVector v = getAlpha(i);
	    retval+=v.toString();
	    retval += "\n" ;
	}
	return retval;
    }

    public long prune() {
	return prune(1e-10);
    }
    
    public void dominationCheck() {
    	dominationCheck(1e-10);
        }

    public long prune(double delta) {
	dominationCheck(delta);
	//System.out.println("Domination");
	//System.out.println(this);
	return (lpPruning(delta));
    }

    private long lpPruning(double delta) {
	LpSolver.resetTotalLpTime();
	if (set.size() < 2)
	    return 0;
	ArrayList<AlphaVector> newv = new ArrayList<AlphaVector>();
	while (set.size() > 0) {
	   SparseBeliefState b;
	   AlphaVector sel_vect = set.remove(0);
	   if (newv.size() == 0) {
		   b = SparseBeliefState.getUniformBelief(sel_vect.size());
	   } else {
		   b = find_region(sel_vect, newv, delta);
	   }
	   if (b != null) {
		   set.add(sel_vect);
		   sel_vect = getBestAlpha(b, set, delta);
		   int idx = set.indexOf(sel_vect);
		   set.remove(idx);
		   newv.add(sel_vect);
	    }
	}
	set = newv;
	return LpSolver.getTotalLpTime();
    }

    public AlphaVector getBestAlpha(BeliefState b) {
	return (getBestAlpha((SparseBeliefState) b, this.set, 0.0));
    }

    private AlphaVector getBestAlpha(SparseBeliefState b,
	    ArrayList<AlphaVector> set2, double delta) {
	AlphaVector best_vec = set2.get(0);
	double best_val = best_vec.eval(b);
	for (AlphaVector test_vec : set2) {
	    double val = test_vec.eval(b);
	    if (Math.abs(val - best_val) < delta) {
		best_vec = lexicographic_max(best_vec, test_vec, delta);
	    } else if (val > best_val) {
		best_vec = test_vec;
	    }
	    best_val = best_vec.eval(b);
	}
	return best_vec;
    }

    private AlphaVector lexicographic_max(AlphaVector bestVec,
	    AlphaVector testVec, double delta) {
	if (bestVec.compareTo(testVec, delta) > 0)
	    return (bestVec);
	else
	    return (testVec);
    }

    public SparseBeliefState find_region(AlphaVector selVect,
	    ArrayList<AlphaVector> newv, double delta) {
    	return(LpSolver.solve(selVect, newv, delta));
    }

    public void dominationCheck(double delta) {
	if (set.size() < 2)
	    return;
	ArrayList<AlphaVector> newv = new ArrayList<AlphaVector>();
	while (set.size() > 0) {
	    AlphaVector sel_vect = set.remove(set.size() - 1);
	    if (newv.size() == 0) {
		newv.add(sel_vect);
		continue;
	    }
	    ArrayList<AlphaVector> tempv = new ArrayList<AlphaVector>();
	    double max_dom = Double.NEGATIVE_INFINITY;
	    for (AlphaVector test_vect : newv) {
		AlphaVector res = test_vect.copy();
		res.diff(sel_vect);
		double min_dom = res.min();
		if (min_dom > max_dom)
		    max_dom = min_dom;
		if (max_dom > 0.0) {
		    break;
		}
		res.scale(-1.0);
		if (res.min() < 0.0) {
		    tempv.add(test_vect);
		}
	    }
	    if (max_dom < 0.0) {
		newv = tempv;
		newv.add(sel_vect);
	    }
	}
	set = newv;
    }

    public void crossSum(SparseBeliefValueFunction vfB) {
	ArrayList<AlphaVector> backup = set;
	set = new ArrayList<AlphaVector>();
	for (AlphaVector vecA : backup) {
		int a=vecA.getAction();
	    for (AlphaVector vecB : vfB.set) {
	    	AlphaVector thevec = vecB.copy();
	    	thevec.add(vecA);
	    	thevec.setAction(a);
	    	push(thevec);
	    }
	}
    }

    public void merge(SparseBeliefValueFunction vfA) {
	for (int i = 0; i < vfA.size(); i++) {
	    push(vfA.getAlpha(i).copy());
	}
    }

    public int getAction(int i) {
	return (set.get(i).getAction());

    }

    public void sort() {
    	Collections.sort(set);
    }

    //public double getAlphaElement(int i, int s) {
	//return set.get(i).get(s);
    //}

	public double performance(BeliefValueFunction oldv, int perfCriteria,boolean sort) {
		if (oldv == null || this.size() != oldv.size()) {
			return Double.NaN;
		}
		if (sort){
			this.sort();
			oldv.sort();
		}
		double conv = 0;
		for (int j = 0; j < this.size(); j++) {
		    AlphaVector newAlpha = this.getAlpha(j);
		    AlphaVector oldAlpha = ((SparseBeliefValueFunction)oldv).getAlpha(j);
		    if (newAlpha.getAction() != oldAlpha.getAction()) {
				return Double.POSITIVE_INFINITY;
		    }
		    AlphaVector perf = newAlpha.copy();
		    perf.diff(oldAlpha);
		    double a_value = 0;
		    switch (perfCriteria) {
		    case ValueConvergence.MAXEUCLID:
			a_value = perf.norm(2.0);
			break;
		    case ValueConvergence.MAXDIST:
			a_value = perf.norm(1.0);
			break;
		    case ValueConvergence.MAX:
		    	a_value=perf.norm(Double.POSITIVE_INFINITY);
			break;
		    }
		    if (a_value > conv)
			conv = a_value;
		}
		return conv;
	}

	public double performance(ValueFunction oldv, int perfCriteria) {
		return performance((SparseBeliefValueFunction)oldv,perfCriteria);
	}
	

	public void crossSum(BeliefValueFunction vf) {
		crossSum((SparseBeliefValueFunction)vf);
	}

	public void merge(BeliefValueFunction vfA) {
		merge((SparseBeliefValueFunction)vfA);
	}

	/*public AlphaVector getFirstAlpha() {
		return getAlpha(0);
	}
	*/

	public boolean member(AlphaVector alpha, double tol) {
		for (AlphaVector vecA : set) {
			if (vecA.compareTo(alpha,tol)==0)
				return true;
			}
		return false;
	}

	public double performance(BeliefValueFunction oldv, int convCriteria) {
			return(performance(oldv,convCriteria,true));
		
	}

	/*public double[] getArray() {
		DenseVector retval=new DenseVector(set.get(0).size());
		retval.fill(Double.NEGATIVE_INFINITY);
		for (AlphaVector alpha: set){
			retval.selectMax((SparseVector)alpha);
		}
		return retval.getArray();
	}*/

	public AlphaVector getUpperBound() {
		AlphaVector cp=set.get(0).copy();
		cp.fill(Double.NEGATIVE_INFINITY);
		for (AlphaVector alpha: set){
			cp.selectMax(alpha);
		}
		return cp;
	}
	
	public double[] getArray() {
		return getUpperBound().getArray();
	}

	public BeliefState find_region(AlphaVector alpha, double delta) {
		return find_region(alpha,this.set,delta);
	}

	
	/*static {
	    try {
	        System.loadLibrary("glpk_java");
	    } catch (UnsatisfiedLinkError e) {
	        System.err.println("Couldn't find glpk_java library in " + System.getProperty("java.library.path"));
	        //e.printStackTrace(System.err);
	    }
	   }
*/


	
} // SparseBeliefValueFunction
