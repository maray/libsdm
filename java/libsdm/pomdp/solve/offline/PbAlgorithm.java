package libsdm.pomdp.solve.offline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import libsdm.common.SparseVector;
import libsdm.common.Utils;
import libsdm.common.ValueSelector;
import libsdm.pomdp.AlphaVector;
import libsdm.pomdp.BeliefState;
import libsdm.pomdp.BeliefValueFunction;
import libsdm.pomdp.ChildMap;

public abstract class PbAlgorithm extends AlphaIteration {

	ArrayList<BeliefState> beliefArray; // Static
	protected LinkedHashMap<Double, BeliefState> bset; // Dynamic
	//static boolean useMask=true;
	
	public static final int EXPAND_L1 = 1;
	public static final int EXPAND_RANDOM = 2;
	public static final int EXPAND_EXPLORATORY = 3;
    
	
	protected AlphaVector getLowestAlpha(int horizon) {
		if (horizon >= 1) {
			return (pomdp.getHomogeneAlpha(0));
		} else {
			//pomdp.getLowerBound();
			double best_val = pomdp.getRewardMaxMin();
			//System.out.println("MAXMIN="+best_val);
			best_val = best_val / (1 - pomdp.gamma());
			return (pomdp.getHomogeneAlpha(best_val));
		}
	}
	
	
	protected BeliefValueFunction[][] project(BeliefValueFunction newvf, int time) {
		BeliefValueFunction[][] projection = new BeliefValueFunction[pomdp.actions()][pomdp
				.observations()];
		for (int a = 0; a < pomdp.actions(); a++) {
			for (int o = 0; o < pomdp.observations(); o++) {
				
				projection[a][o] = pomdp.getEmptyValueFunction();
				for (int idx = 0; idx < newvf.size(); idx++) {
					AlphaVector prev = newvf.getAlpha(idx);
					// System.out.println(prev);
					AlphaVector proj = pomdp.projectAlpha(prev, a, o, time);
					if (proj!=null)
						projection[a][o].push(proj);
				}
				// System.out.println("Project a="+a+" o="+o);
				// System.out.println(projection[a][o]);
			}
		}
		return projection;
	}
	
	/*protected AlphaVector hsviBackup(BeliefState bel,BeliefValueFunction vf) {
		ValueSelector best_act = new ValueSelector(ValueSelector.MAX);
		for (int a = 0; a < pomdp.actions(); a++) {
			AlphaVector alpha_sum = pomdp.getEmptyAlpha(a);
			
			for (int o = 0; o < pomdp.observations(); o++) {
				if (pao.isBlocked(a,o)) continue;
				BeliefState bp;
				bp=pao.projectBelief(bel, a, o, 0);
				ValueSelector bests = new ValueSelector(ValueSelector.MAX);
				for (int idx = 0; idx < vf.size(); idx++){
					AlphaVector vect = vf.getAlpha(idx);
					double val = vect.eval(bp);
					bests.put(vect, val);

				}
				try {
					AlphaVector  maskedAlpha =(AlphaVector)  bests.getRandom();
					if (useMask)
						maskedAlpha.applyMask(bp.getMask());
					alpha_sum.add(pao.projectAlpha(maskedAlpha, a, o, 0));
				} catch (Exception e) {
					e.printStackTrace();
					Utils.warning("Empty Best set?");
					return(null);
				}	
			}
			AlphaVector re = pomdp.getRewardAlpha(a, bel, 0);
			alpha_sum.add(re);
			if (useMask)
				alpha_sum.applyMask(bel.getMask());
			best_act.put(alpha_sum, alpha_sum.eval(bel));
			}
		return (AlphaVector) (best_act.getRandom());
		}
	*/
	protected AlphaVector hsviBackup(BeliefState bel,BeliefValueFunction vf) {
		
	//System.out.println("bel="+bel);
	ValueSelector best_act = new ValueSelector(ValueSelector.MAX);
	for (int a = 0; a < pomdp.actions(); a++) {
		AlphaVector alpha_sum = pomdp.getEmptyAlpha(a);
		for (int o = 0; o < pomdp.observations(); o++) {
			if (pomdp.isBlocked(a, o)) continue;
			ValueSelector bests = new ValueSelector(ValueSelector.MAX);
			//BeliefState bp = pomdp.nextBeliefState(bel, a, o, 0);
			for (int idx = 0; idx < vf.size(); idx++){
				//AlphaVector alpha = vf.getAlpha(idx);
				// bests.put(alpha, alpha.eval(bp));
				//if ( vf.getAlpha(idx) == null ) System.out.println("WTF!");
				AlphaVector vect =pomdp.projectAlpha( vf.getAlpha(idx), a, o, 0);
				//if (vect!=null){
				//	if (bel.getMask()==null){
		    	//		Utils.warning("HSVI BACKUP: Belief with complete mask Uhh?");
		    	//		System.out.println(bel);
				//	}
				double val = vect.eval(bel);
				if (val!=Double.NaN)
						bests.put(vect, val);
			}
			try {
				AlphaVector  maskedAlpha =(AlphaVector)  bests.getRandom();
			//	if (useMask)
			//		maskedAlpha.applyMask(bel.getMask());
				
				alpha_sum.add(maskedAlpha);
			} catch (Exception e) {
				e.printStackTrace();
				Utils.warning("Empty Best set?");
				return(null);
			}
		}
		AlphaVector re = pomdp.getRewardAlpha(a, bel, 0);
		//System.out.println(re);
		alpha_sum.add(re);
	//	if (useMask)
		//	alpha_sum.applyMask(bel.getMask());
		best_act.put(alpha_sum, alpha_sum.eval(bel));
		
	}
	AlphaVector avec = (AlphaVector) (best_act.getRandom());
	//if (bel.getCorner()==0){
//		System.out.println("v'(b)="+avec.eval(bel) + " v(b)="+vf.value(bel));
//	}
	
	return avec;
	}
	
	
	protected AlphaVector fullBackup(BeliefState bel, BeliefValueFunction[][] projection, int time) {
		ValueSelector best_act = new ValueSelector(ValueSelector.MAX);
		for (int a = 0; a < pomdp.actions(); a++) {
			AlphaVector alpha_sum = pomdp.getEmptyAlpha(a);
			for (int o = 0; o < pomdp.observations(); o++) {
				ValueSelector bests = new ValueSelector(ValueSelector.MAX);
				if (pomdp.isBlocked(a,o)) continue;
				for (int idx = 0; idx < projection[a][o].size(); idx++) {
					AlphaVector vect = projection[a][o].getAlpha(idx);
					// System.out.println(vect);
					double val = vect.eval(bel);
					// System.out.println(vect);
					bests.put(vect, val);
					// System.out.println("int bests");
				}
				try {
					alpha_sum.add((AlphaVector) bests.getRandom());
				} catch (Exception e) {
					for (int idx = 0; idx < projection[a][o].size(); idx++) {
						AlphaVector vect = projection[a][o].getAlpha(idx);
						System.out.println(vect);
						double val = vect.eval(bel);
						System.out.println(vect);
						bests.put(vect, val);
						// System.out.println("int bests");
					}
					// e.printStackTrace();
					Utils.warning("Empty Best set?");
					return(null);
				}
			}
			// System.out.println("Alpha Sum a="+a);
			// System.out.println(alpha_sum);
			AlphaVector re = pomdp.getRewardAlpha(a, bel, time);
			// System.out.println("Belief");
			// System.out.println(bel);
			// System.out.println("Re");
			// System.out.println(re);
			alpha_sum.add(re);
			best_act.put(alpha_sum, alpha_sum.eval(bel));
		}
		return (AlphaVector) (best_act.getRandom());
	}

	protected SupportPoints asyncSetBackup( BeliefValueFunction[][] projection, SupportPoints oldsupports) {
		current = pomdp.getEmptyValueFunction();
		SupportPoints supports = new SupportPoints(beliefArray);
		ArrayList<Integer> toImprove = new ArrayList<Integer>();
		for (int i = 0; i < beliefArray.size(); i++)
			toImprove.add(new Integer(i));
		do {
			int aridx = Utils.gen.nextInt(toImprove.size());
			int bidx = toImprove.get(aridx).intValue();

			BeliefState bel = beliefArray.get(bidx);
			AlphaVector alpha = fullBackup(bel,projection, 0);
			double a_val = alpha.eval(bel);
			double o_val = oldsupports.getValue(bidx);
			if (a_val < o_val) {
				AlphaVector old_alpha = oldsupports.getVector(bidx);
				alpha = old_alpha;
				a_val = o_val;
			}
			supports.setSupport(bidx, a_val, alpha);
			toImprove.remove(aridx);
			// Check if exists... already
			if (!current.member(alpha, 1e-09)) {
				current.push(alpha.copy());
				Iterator<Integer> iter = toImprove.iterator();
				while (iter.hasNext()) {
					int idxtest = iter.next().intValue();
					BeliefState beltest = beliefArray.get(idxtest);
					double val = alpha.eval(beltest);
					double oldval = oldsupports.getValue(idxtest);
					if (val > oldval) {
						supports.setSupport(idxtest, val, oldsupports
							.getVector(idxtest));
						iter.remove();
					}
				}
			}
		} while (toImprove.size() != 0);
		return (supports);
	}

	protected void dynamicSetBackup(BeliefValueFunction[][] projection,int time) {
		current = pomdp.getEmptyValueFunction();
		for (BeliefState bel : beliefArray) {
			current.push(fullBackup(bel,projection, time));
		}
	}

	protected SupportPoints staticSetBackup(BeliefValueFunction[][] projection,int time) {
		current = pomdp.getEmptyValueFunction();
		SupportPoints supports = new SupportPoints(beliefArray);
		for (int i = 0; i < beliefArray.size(); i++){
			BeliefState bel = beliefArray.get(i);
			AlphaVector alpha = fullBackup(bel,projection, time);
			supports.setSupport(i, alpha.eval(bel), alpha);
			current.push(alpha);
		}
		return supports;
	}


	protected SupportPoints computeSupports() {
		SupportPoints supports = new SupportPoints(beliefArray);
		for (int idx = 0; idx < beliefArray.size(); idx++) {
			BeliefState bel = beliefArray.get(idx);
			AlphaVector alpha = current.getBestAlpha(bel);
			double value = alpha.eval(bel);
			supports.setSupport(idx, value, alpha);
		}
		return supports;
	}

	protected BeliefState collectExploratory(LinkedHashMap<Double, BeliefState> testBset) {
		Double key=testBset.keySet().iterator().next();
		BeliefState b = (BeliefState) testBset.remove(key);
		double max_dist = Double.NEGATIVE_INFINITY;
		BeliefState bnew = null;
		for (int a = 0; a < pomdp.actions(); a++) {
			int o = pomdp.sampleObservation(b, a, -1);
			BeliefState ba = pomdp.nextBeliefState(b, a, o, 0);
			double dist = distance(ba);
			if (dist > max_dist) {
				max_dist = dist;
				bnew = ba;
			}
		}
		if (max_dist == 0.0)
			bnew = null;
		return (bnew);
	}

	protected BeliefState collectL1(LinkedHashMap<Double, BeliefState> testBset) {
		Double key=testBset.keySet().iterator().next();
		BeliefState b = (BeliefState) testBset.remove(key);
		double max_dist = Double.NEGATIVE_INFINITY;
		BeliefState bnew = null;
		for (int a = 0; a < pomdp.actions(); a++) {
			SparseVector poba = pomdp.observationProbabilities(b, a, count());
			//double[] data=poba.getData();
			int[] index=poba.getIndex();
			for (int in=0;in<index.length;in++){
				BeliefState ba = pomdp.nextBeliefState(b, a, index[in], 0);
				double dist = distance(ba);
				if (dist > max_dist) {
					max_dist = dist;
					bnew = ba;
				}
			}
		}
		if (max_dist == 0.0)
			bnew = null;
		return (bnew);
	}

	protected BeliefState collectRandomExplore(LinkedHashMap<Double, BeliefState> testBset) {
		Iterator<Double> iter = testBset.keySet().iterator();
		Double key=iter.next();
		int elem=Utils.gen.nextInt(Integer.MAX_VALUE)%testBset.size();
		for (int i = 0;i<elem;i++)
			key=iter.next();
		
		BeliefState b = (BeliefState) testBset.remove(key);
		testBset.remove(b);
		BeliefState bprime;
		// System.out.print("in ");
		// System.out.println(b);

		int a = pomdp.getRandomAction();
		int o = pomdp.sampleObservation(b, a, 0);
		ChildMap map = b.getChildMap();
		try{
		if (map.get(a, o))
			return null;
		} catch(java.lang.ArrayIndexOutOfBoundsException e){
			System.out.println("a="+a+" o="+o);
			throw e;
		}
		map.set(a, o);
		// int o = pomdp.sampleObservation(b, a, -1);
		// System.out.println("a="+a+" o="+o);
		// System.out.print("out ");
		//bprime = pomdp.nextBeliefState(b, a, o, -1);
		bprime = pomdp.nextBeliefState(b, a, o, 0); // option
		bprime.initChildMap(pomdp.actions(), pomdp.observations());
		// System.out.println(bprime);
		return bprime;
	}

	private double distance(BeliefState ba) {
		double min_val = Double.POSITIVE_INFINITY;
		for (BeliefState bprime : bset.values()) {
			SparseVector vect = bprime.getPoint().copy();
			vect.minus(ba.getPoint());
			double val = vect.norm(1.0);
			if (val < min_val)
				min_val = val;
		}
		return min_val;
	}


	
	protected double dynamicPerformance(){
		double max_diff = 0;
		double diff = 0;
		double sumcurr = 0;
		for (BeliefState bel: beliefArray){
			diff = Math.abs(current.value(bel) - old.value(bel));
			sumcurr += current.value(bel);
			if (max_diff < diff) 
				max_diff = diff;
		}
		// System.out.println(current);
		if (verbose)
			System.out.println("SumV=" + (sumcurr / beliefArray.size()));
		return max_diff;
	}
	
	protected double staticPerformance(SupportPoints supports, SupportPoints oldsupports){
		double max_diff = 0;
		double diff = 0;
		double sumcurr = 0;
		for (int idx = 0; idx < beliefArray.size(); idx++) {
			diff = supports.getValue(idx) - oldsupports.getValue(idx);
			sumcurr += supports.getValue(idx);
			if (max_diff < diff) 
				max_diff = diff;
		}
		// System.out.println(current);
		if (verbose)
			System.out.println("SumV=" + (sumcurr / beliefArray.size()));
		return max_diff;
	}

	protected abstract double performance();
	
	
	protected class SupportPoints {
		SupportPoints(ArrayList<BeliefState> set) {
			this.value = new double[set.size()];
			this.vector = new AlphaVector[set.size()];
			for (int i = 0; i < set.size(); i++) {
				value[i] = Double.NEGATIVE_INFINITY;
				vector[i] = null;
			}
		}

		public double value[];
		public AlphaVector vector[];

		public double getValue(int idx) {
			return value[idx];
		}

		public AlphaVector getVector(int idx) {
			return vector[idx];
		}

		public void setSupport(int idx, double value, AlphaVector vector) {
			this.value[idx] = value;
			this.vector[idx] = vector;
		}
	}
	/*
	protected static BeliefState collectRandomExplore(PointSet testBset,
			Pomdp pomdp) {
		BeliefState b = testBset.getRandom();
		testBset.remove(b);
		BeliefState bprime;
		// System.out.print("in ");
		// System.out.println(b);
		int a = pomdp.getRandomAction();
		int o = pomdp.sampleObservation(b, a, -1);
		// System.out.println("a="+a+" o="+o);
		// System.out.print("out ");
		bprime = pomdp.nextBeliefState(b, a, o, -1);
		// bprime = pao.projectBelief(b, a, o, -1); //option
		// System.out.println(bprime);
		return bprime;
	}
	*/
	
	//private BeliefState collectPEMA(LinkedHashMap<Double, BeliefState> testBset) {
	//Utils.error("PEMA not implemented yet...");
	//return null;

	/*
	 * double max_val = Double.NEGATIVE_INFINITY; int aprime = -1; int
	 * oprime = -1; BeliefState bnew = null; BeliefState bopt = null; double
	 * rmax=bmdp.getRewardFunction().max()/(1-bmdp.getGamma()); double
	 * rmin=bmdp.getRewardFunction().min()/(1-bmdp.getGamma()); //
	 * System.out.println(testBset.size()); for (BeliefState b : testBset) {
	 * // System.out.println("Yes"); AlphaVector alpha =
	 * current.getBestAlpha(b); for (int a = 0; a < bmdp.nrActions(); a++) {
	 * BeliefState bprime = null; double sum_err = 0; AlphaVector poba =
	 * bmdp.observationProbabilities(b, a, it()); //
	 * System.out.println(b.getPoint()); double
	 * max_sum=Double.NEGATIVE_INFINITY; for (int o = 0; o <
	 * bmdp.nrObservations(); o++) { if (poba.get(o)==0) continue;
	 * bprime=bmdp.nextBeliefState(b, a, o, it()); double sum=0; for (int s
	 * = 0; s < bmdp.nrStates(); s++) { double bdiff = bprime.prob(s) -
	 * b.prob(s); if (bdiff >= 0) sum += (rmax - alpha.get(s)) * bdiff; else
	 * sum += (rmin - alpha.get(s)) * bdiff;
	 * 
	 * } sum=poba.get(o)*sum; if (sum>max_sum){ max_sum=sum; bopt=bprime; }
	 * sum_err += sum; } } }
	 */
//}



/*
 * private double minError(BeliefState beliefState, PointSet bset) { double
 * rmax = bmdp.getRewardFunction().max() / (1.0 - bmdp.getGamma()); double
 * rmin = bmdp.getRewardFunction().min() / (1.0 - bmdp.getGamma()); double
 * min_val = Double.POSITIVE_INFINITY; for (BeliefState b : bset) { double
 * sum = 0; AlphaVector vect = current.getBestAlpha(b); for (int s = 0; s <
 * bmdp.nrStates(); s++) { double bdiff = beliefState.getPoint().get(s) -
 * b.getPoint().get(s); if (bdiff >= 0) sum += (rmax - vect.get(s)) * bdiff;
 * else sum += (rmin - vect.get(s)) * bdiff; } if (sum < min_val) min_val =
 * sum; } return (min_val); }
 */

/*private BeliefState collectInfoBased(
		LinkedHashMap<Double, BeliefState> testBset, double prob) {
	Iterator<Double> iter = testBset.keySet().iterator();
	Double key=iter.next();
	int elem=Utils.gen.nextInt(Integer.MAX_VALUE)%testBset.size();
	for (int i = 0;i<elem;i++)
		key=iter.next();
	
	BeliefState b = (BeliefState) testBset.remove(key);
	testBset.remove(b);
	BeliefState bprime;
	int a;
	// System.out.print("in ");
	// System.out.println(b);
	
	if (Utils.gen.nextDouble() > prob){
		ValueSelector bests=new ValueSelector(ValueSelector.MAX);
		for (a=0;a<pomdp.actions();a++){
			AlphaVector var = pomdp.observationProbabilities(b, a,-1);
			SparseVector vec=new SparseVector(pomdp.observations());
			for (int o=0;o<pomdp.observations();o++){
				BeliefState bel = pomdp.nextBeliefState(b, a, o,-1);
				vec.set(o,pomdp.getRewardFunction().eval(bel, a,-1));
			}
			double exval = vec.dot((SparseVector)var);
			bests.put(new Integer(a), exval);
		}
		a=((Integer) bests.getRandom()).intValue();
	}
	else
	{
		a = pomdp.getRandomAction();
	}
	int o = pao.sampleObservation(b, a, -1);
	ChildMap map = b.getChildMap();
	if (map.get(a, o))
		return null;
	map.set(a, o);
	// int o = pomdp.sampleObservation(b, a, -1);
	// System.out.println("a="+a+" o="+o);
	// System.out.print("out ");
	// bprime = pomdp.nextBeliefState(b, a, o, -1);
	bprime = pao.projectBelief(b, a, o, -1); // option
	bprime.initChildMap(pomdp.actions(), pomdp.observations());
	// System.out.println(bprime);
	return bprime;
}*/

}
