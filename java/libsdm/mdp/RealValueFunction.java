package libsdm.mdp;

import java.util.Arrays;

import libsdm.common.DenseVector;
import libsdm.common.Utils;

public class RealValueFunction implements ValueFunction {
	

	protected DenseVector V;
	protected int[] A;	
	protected DenseVector Q[];
	protected int actions;
	protected int states;
	
	
	public RealValueFunction(int states,int actions){
		this.states=states;
		this.actions=actions;
		Q=new DenseVector[actions];
		for (int a=0;a<actions;a++)
			Q[a]=new DenseVector(states);
		V=new DenseVector(states);
		A=new int[states];
	}

	private RealValueFunction() {
	}

	public void setValue(int s, double v,int best_action){
		V.set(s,v);
		A[s]=best_action;
	}
	
	public void setQ(int s, int a, double v){
		Q[a].set(s,v);
	}
	
	public int getAction(int s){
		return A[s];
	}
	
	public double getValue(int s){
		return V.get(s);
	}
	
	public int[] getActions(){
		return A;
	}
	public DenseVector getValues(){
		return V;
	}

	public double getQ(int s, int a) {
		return Q[a].get(s);
	}
	
	public DenseVector getQ(int a) {
		return Q[a];
	}
	
	public double performance(ValueFunction oldv, int convCriteria) {
		RealValueFunction oldVf = (RealValueFunction)oldv;
		//System.out.println(oldVf.V);
		//System.out.println(V);
		DenseVector perf = V.copy();
	    perf.minus(oldVf.V);
	    double value=0;
	    switch (convCriteria) {
	    	case ValueConvergence.MAXEUCLID:
	    	value = perf.norm(2.0);
	    	break;
	    	case ValueConvergence.MAXDIST:
	    	value = perf.norm(1.0);
	    	break;
	    	case ValueConvergence.MAX:
	    		value=perf.norm(Double.POSITIVE_INFINITY);
	    	break;
	    	default:
	    		Utils.error("(RealValueFunction) Wrong Performance Criterion");
	    }
	    return value;
	}

	public RealValueFunction copy() {
		RealValueFunction retval = new RealValueFunction();
		retval.actions=actions;
		retval.states=states;
		retval.A=Arrays.copyOf(A,A.length);
		retval.Q=new DenseVector[actions];
		for (int a=0;a<actions;a++){
			retval.Q[a]=Q[a].copy();
		}
		retval.V=V.copy();
		return retval;
	}
	
	public String toString(){
		String retval="";;
		for (int a=0;a<actions;a++){
			retval+="a="+Integer.toString(a)+": "+Q[a].toString()+"\n";
		}
		return retval;
	}

	public double[] getArray() {
		return V.getArray();
	}
	
}
