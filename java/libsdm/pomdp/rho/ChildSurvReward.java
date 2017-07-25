package libsdm.pomdp.rho;

import libsdm.common.SparseVector;
import libsdm.common.Utils;
import libsdm.mdp.RewardMatrix;
import libsdm.pomdp.AlphaVector;
import libsdm.pomdp.BeliefReward;
import libsdm.pomdp.BeliefState;
import libsdm.pomdp.BeliefValueFunction;
import libsdm.pomdp.plain.SparseAlphaVector;

public class ChildSurvReward implements BeliefReward {

private int rooms;
private int states;
private int actions;
private BeliefReward underReward;

public ChildSurvReward(int type, int rooms, int childs) {
	this.rooms=rooms;
	int ncodes=(int) Math.pow(rooms, childs);
	this.states=(int) ncodes*rooms;
	this.actions=3;
	switch (type) {
	case CameraClean.RTYPE_STATE:
		Utils.error("Rock Diagnosis does not support State-Based Rewards");
		return;
	case CameraClean.RTYPE_ENTROPY:
		underReward = new EntropyReward(states, actions, 0);
		break;
	case CameraClean.RTYPE_LINEAR:
		underReward = new LinearReward(states, actions, 0);
		break;
	case CameraClean.RTYPE_VARIANCE:
		underReward = new VarianceReward(states, actions, 0);
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
	return underReward.eval(b, a, i) - Math.log(rooms);
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
		return true;
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
