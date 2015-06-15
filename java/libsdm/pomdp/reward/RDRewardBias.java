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

public class RDRewardBias implements BeliefReward {

	private int actions;
	private int horizon;
	BeliefReward underReward;
	private SparseAlphaVector rew;
	private int rocks_configs;
	private int states;
	private double eta;
	private int metals;
	SparseVector r;
	public RDRewardBias(int type,int rocks_configs,int states,int actions,double eta) {
		this(type,rocks_configs,states,actions,eta,0);
	}
	
	public RDRewardBias(int type,int rocks_configs,int states,int actions,double eta, int horizon) {
		this.rocks_configs=rocks_configs;
		this.eta=eta;
		this.actions=actions;
		this.states=states;
		this.horizon=horizon;
		int rocks=actions-4;
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
		metals = (int)Math.pow(rocks_configs, 1.0/(double)rocks);
		
		double rval[]=new double[states];
		for (int rc=0;rc<rocks_configs;rc++){
			double bias=eta*bias_reward(rc,rocks,metals);
			//System.out.println("Bias " +bias);
			
			for (int pos=0;pos<states/rocks_configs;pos++){
					rval[pos*rocks_configs + rc]=bias;
			}
		}
		rew=SparseAlphaVector.transform(r);
		r=new SparseVector(rval);
	}

	/*protected SparseBeliefValueFunction approximate(int a, PointSet bset) {
		Utils.error("Complete Gamma-set approximation not implemented yet");
		return null;
	}
	*/

	private double bias_reward(int rc, int rocks, int metals) {
		double contrib=0;
		for (int r=0;r<rocks;r++)
			contrib+=PomdpFactory.getRDConfigState(rc, r, metals) + 1;
		return contrib/rocks;
	}

	public double eval(BeliefState b, int a,int i) {
		//SparseBeliefState underBelief = getUnderBelief(b);
		//System.out.println(underBelief);
		return underReward.eval(b, a, i) + b.getPoint().dot(r);
	}


	public double max(int a) {
		return underReward.max(a) + r.max();
	}

	public double max() {
		return underReward.max() + r.max();
	}

	public double min(int a) {
		return underReward.min(a) + r.min();
	}

	public double min() {
		return underReward.min() + r.min();
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
		AlphaVector vec = underReward.getTangentAlpha(a,bel, i).copy();
		//System.out.println("F");
		//System.out.println(vec);
		vec.add(rew);
		//System.out.println("S");
		//System.out.println(vec);
		return vec;
	}
		
	public SparseAlphaVector getBorderValues(int a,int i) {
		return SparseAlphaVector.transform(SparseVector.getHomogene(states, max(a)),a);
	}

	
	/*public void approx(PointSet fullBset) {
		Utils.error("Complete Gamma-set approximation not implemented yet");
	}*/
}
