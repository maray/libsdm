package libsdm.pomdp;

import libsdm.mdp.RewardFunction;

public interface BeliefReward extends RewardFunction {

	public abstract BeliefValueFunction getValueFunction(int a, int i);
	public abstract double eval(BeliefState b, int a,int i);
	//public abstract void approx(PointSet fullBset);
	//public abstract double get(int state, int action, int nstate, int obs,BeliefState bel,int i);
	public abstract RewardFunction getMdpReward();
	public abstract AlphaVector getTangentAlpha(int a, BeliefState bel, int i);
	public abstract AlphaVector getBorderValues(int a, int i);
	
}
