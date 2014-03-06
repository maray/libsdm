package libsdm.pomdp.solve.offline;

import libsdm.mdp.RewardMatrix;
import libsdm.mdp.SparseMdp;
import libsdm.mdp.ValueConvergence;
import libsdm.mdp.ValueIteration;
import libsdm.pomdp.AlphaVector;
import libsdm.pomdp.Pomdp;

public class QmdpBound extends ValueIteration {

	AlphaVector alpha;
	
	public QmdpBound(Pomdp pomdp,double epsilon) {
		super(new SparseMdp(pomdp.getTransitionMatrix(), (RewardMatrix)pomdp.getRewardFunction().getMdpReward(), pomdp.states(), pomdp.actions(), pomdp.gamma(), null, null, 0),null,0, false);
		this.addStopCriterion(new ValueConvergence(epsilon));
		//RewardMatrix ex=(RewardMatrix)pomdp.getRewardFunction().getMdpReward();
		//System.out.print(ex.get(91, 4, 3));
		alpha=pomdp.getEmptyAlpha();
	}
	
	/*public QmdpBound(Pomdp pomdp,int horizon) {
		super(new SparseMdp((TransitionMatrix)pomdp.getTransitionFunction(), (RewardMatrix)pomdp.getRewardFunction().getMdpReward(), pomdp.states(), pomdp.actions(), pomdp.gamma(), null, null, 0),null,horizon,false);
		this.pomdp=pomdp;
	}*/
	
	public AlphaVector getAlphaVector(){ 
		alpha.set(this.getValueFunction());
		return alpha;
	}
	
	

}
