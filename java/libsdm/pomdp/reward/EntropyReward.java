package libsdm.pomdp.reward;

import libsdm.common.SparseVector;
import libsdm.common.Utils;
import libsdm.mdp.RewardMatrix;
import libsdm.pomdp.AlphaVector;
import libsdm.pomdp.BeliefReward;
import libsdm.pomdp.BeliefState;
import libsdm.pomdp.BeliefValueFunction;
import libsdm.pomdp.plain.SparseAlphaVector;
import libsdm.pomdp.plain.SparseBeliefValueFunction;

public class EntropyReward implements BeliefReward {

	private int states;
	private int actions;
	private SparseBeliefValueFunction[] rewardCache;
	private int horizon;

	
	
	public EntropyReward(int nS,int nA) {
		this(nS,nA,0);
	}
	
	public EntropyReward(int nS,int nA, int horizon) {
		this.states=nS;
		this.actions=nA;
		this.horizon=horizon;
	}

	/*
	protected BeliefValueFunction approximate(int a, PointSet bset) {
		SparseBeliefValueFunction retval = new SparseBeliefValueFunction();
		//remove the point from the boundary.
		//ealVector hack = SparseVector.getHomogene(states,1e-2/states);
		for (BeliefState bel : bset) {
			SparseVector vec = bel.getPoint().copy();		
			SparseVector tan = vec.getEntropyTangent(Math.E);
			for (int j = 0; j < tan.size(); j++) {
				if (tan.get(j) > 2*Math.log(states))
					tan.set(j, 2*Math.log(states));
			}
			tan.scale(-1.0);
			tan.add(SparseVector.getHomogene(tan.size(), Math.log(states)));
			// System.out.println(tan);
			SparseAlphaVector av = SparseAlphaVector.transform(tan, a);
			if (!retval.member(av,1e-09))
				retval.push(av);
		}
		retval.prune();
		System.out.println("Reward approximation: vectors="+retval.size());
		return retval;
	}
	*/

	public double eval(BeliefState b, int a,int i) {
		if (horizon==0 || i==horizon -1)
			return max() - b.getEntropy(Math.E);
		else return 0;
	}

	public double max(int a) {
		return -Math.log(1.0/(double)states);
	}

	public double max() {
		return -Math.log(1.0/(double)states);
	}

	public double min(int a) {
		return 0;
	}

	public double min() {
		return 0;
	}

	public double get(int state, int action, int nstate,int i) {
		Utils.warning("Using Entropy Rewards as normal rewards, always 0");
		return 0;
	}

	/*public void approx(PointSet bset) {
		rewardCache = new SparseBeliefValueFunction[actions];
    	for (int a = 0; a < actions; a++) {
    	    rewardCache[a] = (SparseBeliefValueFunction) approximate(a, bset);
    	    // System.out.println(rewardCache[a]);
    	}
	}*/

	/*public double get(int state, int action, int nstate, int obs,
			BeliefState bel,int i) {
		if (horizon==0 || i==horizon -1)
			return eval(bel,action,i);
		return 0;
	}*/

	public BeliefValueFunction getValueFunction(int a,int i) {
		if (horizon==0 || i==horizon -1)
			return rewardCache[a].copy();
		else {
			SparseBeliefValueFunction nullvf = new SparseBeliefValueFunction();
			nullvf.push(new SparseAlphaVector(states,a));
			return nullvf;
		}
	}

	public RewardMatrix getMdpReward() {
		Utils.warning("Using Entropy Rewards as normal rewards, always null");
		return null;
	}
	public SparseVector get(int currentState, int a,int i) {
		Utils.warning("Using Entropy Rewards as normal rewards, always null");
		return null;
	}

	public boolean stationary() {
		if (horizon==0)
			return true;
		else 
			return false;
	}

	public AlphaVector getTangentAlpha(int a, BeliefState bel, int i) {
		SparseAlphaVector av; 
		// SparseVector hack = SparseVector.getHomogene(states,1e-2/states);
		if (horizon == 0 || i == horizon - 1) {
			SparseVector vec = bel.getPoint().copy();
			vec.deborder(0.0001);
			/*for (int j = 0; j < vec.size(); j++) {
				if (vec.get(j) < 0.0001 )
					vec.setExpensive(j, 0.0001);
			}
			*/
			vec.normalize();
			SparseVector tan = vec.getEntropyTangent(Math.E);

			tan.scale(-1.0);
			tan.add(SparseVector.getHomogene(tan.size(), Math.log(states)));
			// System.out.println(tan);
			av= SparseAlphaVector.transform(tan, a);
		} else {
			av=SparseAlphaVector.transform(SparseVector.getHomogene(states, 0),a);
		}
		return av;
	}

	public SparseAlphaVector getBorderValues(int a,int i) {
		return SparseAlphaVector.transform(SparseVector.getHomogene(states, max(a)),a);
	}
}
