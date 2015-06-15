package libsdm.pomdp.reward;

import libsdm.common.SparseVector;
import libsdm.common.Utils;
import libsdm.mdp.RewardMatrix;
import libsdm.pomdp.AlphaVector;
import libsdm.pomdp.BeliefReward;
import libsdm.pomdp.BeliefState;
import libsdm.pomdp.BeliefValueFunction;
import libsdm.pomdp.PomdpFactory;
import libsdm.pomdp.plain.SparseAlphaVector;

public class RDReward implements BeliefReward {

	private int actions;
	private int horizon;
	BeliefReward underReward;
	private int rocks_configs;
	private int states;
	
	
	public RDReward(int type,int rocks_configs,int states,int actions) {
		this(type,rocks_configs,states,actions,0);
	}
	
	public RDReward(int type,int rocks_configs,int states,int actions, int horizon) {
		this.rocks_configs=rocks_configs;
		this.actions=actions;
		this.states=states;
		this.horizon=horizon;
		switch (type) {
		case PomdpFactory.RTYPE_STATE:
			Utils.error("Rock Diagnosis does not support State-Based Rewards");
			return;
		case PomdpFactory.RTYPE_ENTROPY:
			underReward = new EntropyReward(states, actions, horizon);
			break;
		case PomdpFactory.RTYPE_LINEAR:
			underReward = new LinearReward(states, actions, horizon);
			break;
		case PomdpFactory.RTYPE_VARIANCE:
			underReward = new VarianceReward(states, actions, horizon);
			break;
		}
	}

	/*protected SparseBeliefValueFunction approximate(int a, PointSet bset) {
		Utils.error("Complete Gamma-set approximation not implemented yet");
		return null;
	}
	*/

	public double eval(BeliefState b, int a,int i) {
		//SparseBeliefState underBelief = getUnderBelief(b);
		//System.out.println(underBelief);
		return underReward.eval(b, a, i) - Math.log(states/rocks_configs);
	}


	public double max(int a) {
		return underReward.max(a);
	}

	public double max() {
		return underReward.max();
	}

	public double min(int a) {
		return underReward.min(a);
	}

	public double min() {
		return underReward.min();
	}

	public double get(int state, int action, int nstate,int i) {
		return max(action);
	}

	/*public void approx(PointSet bset) {
		rewardCache = new SparseBeliefValueFunction[actions];
    	for (int a = 0; a < actions; a++) {
    	    rewardCache[a] = (SparseBeliefValueFunction) approximate(a, bset);
    	    // System.out.println(rewardCache[a]);
    	}
	}
	*/

	/*public double get(int state, int action, int nstate, int obs,
			BeliefState bel,int i) {
		if (horizon==0 || i==horizon -1)
			return eval(bel,action,i);
		return 0;
	}*/

	public BeliefValueFunction getValueFunction(int a,int i) {
		Utils.error("Complete Gamma-set approximation not implemented yet");
		return null;
	}

	public RewardMatrix getMdpReward() {
		SparseVector[] rew=new SparseVector[actions];
		for (int a=0;a<actions;a++)
			rew[a]=SparseVector.getHomogene(states,max(a));
		return new RewardMatrix(rew);
	}
	public SparseVector get(int currentState, int a,int i) {
		return SparseVector.getHomogene(states,max(a));
	}

	public boolean stationary() {
		if (horizon==0)
			return true;
		else 
			return false;
	}

	public AlphaVector getTangentAlpha(int a, BeliefState bel, int i) {
		return underReward.getTangentAlpha(a,bel, i);
	}
	
	public SparseAlphaVector getBorderValues(int a,int i) {
		return SparseAlphaVector.transform(SparseVector.getHomogene(states, max(a)),a);
	}

	/*public void approx(PointSet fullBset) {
		Utils.error("Complete Gamma-set approximation not implemented yet");
	}*/
}
