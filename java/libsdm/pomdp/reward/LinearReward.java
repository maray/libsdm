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

public class LinearReward implements BeliefReward {

	private int states;
	private int actions;
	private SparseBeliefValueFunction[] rewardCache;
	private int horizon;

	public LinearReward(int nS, int nA,int horizon) {
		this.horizon=horizon;
		this.states=nS;
		this.actions=nA;
		rewardCache = new SparseBeliefValueFunction[actions];
		for (int a=0;a<actions;a++){
			rewardCache[a]=new SparseBeliefValueFunction();
			for (int s=0;s<states;s++){
				SparseVector vec = new SparseVector(states);
				vec.assign(s,1.0);
				rewardCache[a].push(SparseAlphaVector.transform(vec, a));
			}
		}
	}

	/*protected SparseBeliefValueFunction approximate(int a, PointSet bset) {
		SparseBeliefValueFunction retval = new SparseBeliefValueFunction();
		//remove the point from the boundary.
		//ealVector hack = SparseVector.getHomogene(states,1e-2/states);
		for (BeliefState bel : bset) {
			//SparseVector vec = bel.getPoint().copy();
			
			//vec.add(hack);
			//vec.normalize();
			double c=0;
			SparseVector tan = new SparseVector(states);
			for (int i=0;i<states;i++){
				c+=Math.abs(bel.getPoint().get(i) - 1.0/(double)states);
				if (bel.getPoint().get(i) > 1.0/(double)states)
					tan.set(i,1);
				else if (bel.getPoint().get(i) == 1.0/(double)states)
					tan.set(i,0);
				else
					tan.set(i,-1);
			}
			c=c- bel.getPoint().dot(tan);
			tan.add(SparseVector.getHomogene(states, c));
			SparseAlphaVector av = SparseAlphaVector.transform(tan, a);
			if (!retval.member(av,1e-09))
				retval.push(av);
		}
		retval.prune();
		System.out.println(retval.size());
		//System.out.println(retval.getAlpha(0));
		//System.out.println(retval.getAlpha(1));
		return retval;
	}*/

	public double eval(BeliefState b, int a,int i) {
		if (horizon==0 || i==horizon -1)
			return rewardCache[a].value(b);
		else return 0;
	}

	public double max(int a) {
		return 1;
	}

	public double max() {
		return 1;
	}

	public double min(int a) {
		return 1/(float)states;
	}

	public double min() {
		return 1/(float)states;
	}

	public double get(int state, int action, int nstate,int i) {
		Utils.warning("Using Info Rewards as normal rewards, always 0");
		return 0;
	}

	/*public void approx(PointSet bset) {
		Utils.warning("No need to approximate LinearReward");
		//rewardCache = new SparseBeliefValueFunction[actions];
    	//for (int a = 0; a < actions; a++) {
    	 //   rewardCache[a] = (SparseBeliefValueFunction) approximate(a, bset);
    	 //   // System.out.println(rewardCache[a]);
    //	}
	}
*/
	/*public double get(int state, int action, int nstate, int obs,
			BeliefState bel, int i) {
		return eval(bel,action,i);
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
		Utils.warning("Using Info Rewards as normal rewards, always null");
		return null;
	}
	
	public SparseVector get(int currentState, int a,int i) {
		Utils.warning("Using Info Rewards as normal rewards, always null");
		return null;
	}


	public boolean stationary() {
		if (horizon==0)
			return true;
		else 
			return false;
	}

	public AlphaVector getTangentAlpha(int a, BeliefState bel, int i) {
		if (horizon == 0 || i == horizon - 1) {
			return (rewardCache[a].getBestAlpha(bel));
		}
		else{
			return(SparseAlphaVector.transform(SparseVector.getHomogene(states, 0),a));
		}
		
	}

	public AlphaVector getBorderValues(int a,int i) {
		return SparseAlphaVector.transform(SparseVector.getHomogene(states, max(a)),a);
	}
	
}
