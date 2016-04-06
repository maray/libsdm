package libsdm.mdp;

import libsdm.common.SparseVector;

public class RewardMatrix implements RewardFunction {

	protected SparseVector[][] func;
	protected SparseVector[] fcond; 
	protected boolean condensated;
	protected double maxs[];
	protected double mins[];
	protected double max;
	protected double min;
	protected int actions;
	protected int states;
	
	public RewardMatrix(SparseVector[][] r) {
		condensated=false;
		reward_init(r);
	}
	
	public RewardMatrix(SparseVector[] r) {
		condensated=true;
		
		//SparseVector[][] func=new SparseVector[r[0].size()][r.length];
		//for (int s=0;s<r[0].size();s++){
		//	for (int a=0;a<r.length;a++){
		//		func[s][a]=SparseVector.getHomogene(r[0].size(), r[a].get(s));
		//	}
		//}
		//reward_init(func);
		cond_reward_init(r);
	}

	protected void cond_reward_init(SparseVector[] r){
	    fcond=r;
	    actions=r.length;
	    states=r[0].size();
	    maxs=new double[actions];
		mins=new double[actions];
		max=Double.NEGATIVE_INFINITY;
		min=Double.POSITIVE_INFINITY;
		for (int a=0;a<actions;a++){
			maxs[a]=r[a].max();
			mins[a]=r[a].min();
			if (maxs[a]>max)
				max=maxs[a];
			if (mins[a]<min)
				min=mins[a];
		}
	}
	
	protected void reward_init(SparseVector[][] r){
		//System.out.println("INIT?");
		func=r;
		actions=r[0].length;
		states=r.length;
		maxs=new double[actions];
		mins=new double[actions];
		compute_max();
		compute_min();
	}
	
	public double get(int state, int action, int nstate, int i) {
		if (condensated)
			return fcond[action].get(state);
		else
			return func[state][action].get(nstate);
		}

  protected double compute_max(int a) {
		double maxmax=Double.NEGATIVE_INFINITY;
		for (int s=0;s<states;s++){
			double mymax=func[s][a].max();
			if (mymax>maxmax)
				maxmax=mymax;
		}
		return maxmax;		
	}

	protected void compute_max() {
		double max=Double.NEGATIVE_INFINITY;
		for (int a=0;a<actions;a++){
			maxs[a]=compute_max(a);
			if (maxs[a]>max)
				max=maxs[a];
		}
	}

	protected double compute_min(int a) {
		double minmin=Double.POSITIVE_INFINITY;
		for (int s=0;s<states;s++){
			double mymin=func[s][a].min();
			if (mymin<minmin)
				minmin=mymin;
			//System.out.println(mymin);
		}
		return minmin;
	}

	protected void compute_min() {
		double min=Double.POSITIVE_INFINITY;
		for (int a=0;a<actions;a++){
			mins[a]=compute_min(a);
			if (mins[a]>min)
				min=mins[a];
		}
	}

	public SparseVector get(int state, int action,int i) {
		if (condensated)
			return SparseVector.getHomogene(states, fcond[action].get(state));
		else
		    return func[state][action];
	}
	
	public boolean stationary() {
		return true;
	}

	public SparseVector get(int a, int i, TransitionMatrix tr) {
		if (condensated)
			return fcond[a];
		
		double val[]= new double[states];
		for (int j=0;j<states;j++){
			val[j]=tr.get(j,a,i).dot(func[j][a]);
		}
		return new SparseVector(val);
	}



	public double max(int a) {
		return maxs[a];
	}



	public double max() {
		return max;
	}



	public double min(int a) {
		return mins[a];
	}



	public double min() {
		return min;
	}

}
