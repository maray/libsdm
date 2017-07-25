package libsdm.pomdp.rho;

import libsdm.common.SparseVector;
import libsdm.common.Utils;
import libsdm.mdp.RewardMatrix;
import libsdm.pomdp.AlphaVector;
import libsdm.pomdp.BeliefReward;
import libsdm.pomdp.BeliefState;
import libsdm.pomdp.BeliefValueFunction;
import libsdm.pomdp.plain.SparseAlphaVector;
import libsdm.pomdp.plain.SparseBeliefValueFunction;

public class VarianceReward implements BeliefReward {

	private int states;
	//private int actions;
	private SparseBeliefValueFunction[] rewardCache;
	private int horizon;

	public VarianceReward(int nS, int nA) {
		this(nS, nA, 0);
	}

	public VarianceReward(int nS, int nA, int horizon) {
		this.states = nS;
		//this.actions = nA;
		this.horizon = horizon;
	}

	/*protected SparseBeliefValueFunction approximate(int a, PointSet bset) {
		SparseBeliefValueFunction retval = new SparseBeliefValueFunction();
		// remove the point from the boundary.
		// ealVector hack = SparseVector.getHomogene(states,1e-2/states);
		for (BeliefState bel : bset) {
			// SparseVector vec = bel.getPoint().copy();

			// vec.add(hack);
			// vec.normalize();
			double c = bel.getPoint().dot(bel.getPoint());
			SparseVector tan = new SparseVector(states);
			for (int i = 0; i < states; i++) {
				tan.set(i, 2 * bel.getPoint().get(i) - c);
			}
			SparseAlphaVector av = SparseAlphaVector.transform(tan, a);
			if (!retval.member(av, 1e-09))
				retval.push(av);
		}
		retval.prune();
		System.out.println(retval.size());
		// System.out.println(retval.getAlpha(0));
		// System.out.println(retval.getAlpha(1));
		return retval;
	}
*/
	public double eval(BeliefState b, int a, int i) {
		if (horizon == 0 || i == horizon - 1) {
			SparseVector v = b.getPoint().copy();
			v.add(SparseVector.getHomogene(v.size(), -1.0 / ((double) states)));
			return v.dot(v);
		} else
			return 0;
	}

	public double max(int a) {
		return (1.0-  1.0/Math.pow((double)states,2.0));
	}

	public double max() {
		return (1.0- 1.0/Math.pow((double)states,2.0));
	}

	public double min(int a) {
		return 0;
	}

	public double min() {
		return 0;
	}

	/*
	public void approx(PointSet bset) {
		rewardCache = new SparseBeliefValueFunction[actions];
		for (int a = 0; a < actions; a++) {
			rewardCache[a] = (SparseBeliefValueFunction) approximate(a, bset);
			// System.out.println(rewardCache[a]);
		}
	}
*/
	/*
	 * public double get(int state, int action, int nstate, int obs, BeliefState
	 * bel,int i) { if (horizon==0 || i==horizon -1) return eval(bel,action,i);
	 * else return 0; }
	 */
	public BeliefValueFunction getValueFunction(int a, int i) {
		if (horizon == 0 || i == horizon - 1) {
			return rewardCache[a].copy();
		} else {
			SparseBeliefValueFunction nullvf = new SparseBeliefValueFunction();
			nullvf.push(new SparseAlphaVector(states, a));
			return nullvf;
		}
	}

	public RewardMatrix getMdpReward() {
		Utils.warning("Using Entropy Rewards as normal rewards, always null");
		return null;
	}

	public double get(int state, int action, int nstate, int i) {
		Utils.warning("Using Entropy Rewards as normal rewards, always 0");
		return 0;
	}

	public SparseVector get(int currentState, int a, int i) {
		Utils.warning("Using Entropy Rewards as normal rewards, always null");
		return null;
	}

	public boolean stationary() {
		if (horizon == 0)
			return true;
		else
			return false;
	}

	public AlphaVector getTangentAlpha(int a, BeliefState bel, int i) {
		SparseAlphaVector av;
		if (horizon == 0 || i == horizon - 1) {
			double c = bel.getPoint().dot(bel.getPoint()) - 1.0/Math.pow((double)states,2.0);
			double tan[]=bel.getPoint().getArray();
			for (int j = 0; j < states; j++) {
				tan[j]= 2 * tan[j] - c;
			}
			av = SparseAlphaVector.transform(new SparseVector(tan), a);
		} else {
			av = SparseAlphaVector.transform(SparseVector.getHomogene(states, 0),
					a);

		}
		return av;
	}

	public SparseAlphaVector getBorderValues(int a,int i) {
		return SparseAlphaVector.transform(SparseVector.getHomogene(states, max(a)),a);
	}

}
