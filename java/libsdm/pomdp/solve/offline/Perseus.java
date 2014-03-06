package libsdm.pomdp.solve.offline;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import libsdm.common.IterationStats;
import libsdm.common.Utils;
import libsdm.pomdp.BeliefState;
import libsdm.pomdp.BeliefValueFunction;
import libsdm.pomdp.Pomdp;

public class Perseus extends PbAlgorithm{

	private int collect;
	private int ps_size;
	private SupportPoints supports;
	private SupportPoints oldsupports;
	

	public Perseus(Pomdp pomdp,int psSize,int horizon){
		startInit(pomdp, horizon);
		this.ps_size=psSize;
		collect=PbAlgorithm.EXPAND_RANDOM;
		this.pomdp=pomdp;
		current = pomdp.getEmptyValueFunction();
		current.push(getLowestAlpha(horizon));	
		beliefArray=null;
		endInit();
	}
	
	public void setCollect(int collect) {
		this.collect = collect;
	}


	@Override
	public IterationStats iterate() {
		startIteration();
		int time = horizon - count() - 1;	
		expand();
		old = current;
		BeliefValueFunction[][] projection = project(old, time);
		oldsupports=supports;
		supports=asyncSetBackup(projection, oldsupports);
		endIteration();
		return iterationStats;
	}
	
	public void expand(){
		// Execute this only once;
		if (beliefArray!=null) return;
		
		bset = new LinkedHashMap<Double,BeliefState>();
		BeliefState b0 = pomdp.getInitialBeliefState();
		b0.initChildMap(pomdp.actions(), pomdp.observations());
		bset.put(b0.naiveHash(),b0);
		LinkedHashMap<Double,BeliefState> testBset = new  LinkedHashMap<Double, BeliefState>();
		testBset.putAll(bset);
		while (bset.size() < ps_size) {
			BeliefState point = null;
			switch (collect) {
				case PbAlgorithm.EXPAND_EXPLORATORY:
					point = collectExploratory(testBset);
					break;
				case PbAlgorithm.EXPAND_L1:
					point = collectL1(testBset);
					break;
				case PbAlgorithm.EXPAND_RANDOM:
					point = collectRandomExplore(testBset);
					break;
				default:
					Utils.error("Wrong Collection Method");
					break;
			}
			//System.out.println(point);
			if (point != null){
				Double hsh=point.naiveHash();
				if (!bset.containsKey(hsh))
					bset.put(hsh,point);
			}
			if (testBset.size() == 0) {
				if (verbose) 
					System.out.println("Collected points = " + bset.size());
				// if (params.getExpandMethod() ==
				// PbParams.EXPAND_RANDOM_STATIC)
				testBset = new  LinkedHashMap<Double, BeliefState>();
				testBset.putAll(bset);
			}
		}
		if (verbose)
			System.out.println("Size(PS)=" + bset.size());
		
		beliefArray = new ArrayList<BeliefState>(bset.values());
		supports=computeSupports();
	}
		@Override
	public double performance() {
			return staticPerformance(supports,oldsupports);
	}

	@Override
	public void reset() {
		current = pomdp.getEmptyValueFunction();
		current.push(getLowestAlpha(horizon));	
		beliefArray=null;
		super.reset();
	}

}
