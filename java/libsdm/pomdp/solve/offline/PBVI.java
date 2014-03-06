package libsdm.pomdp.solve.offline;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import libsdm.common.IterationStats;
import libsdm.common.Utils;
import libsdm.pomdp.BeliefState;
import libsdm.pomdp.BeliefValueFunction;
import libsdm.pomdp.Pomdp;

public class PBVI extends PbAlgorithm{

	private int collect;
	private int bkups;
	private int ps_size;
	private int max_new;
	
	private boolean prune;



	public PBVI(Pomdp pomdp,int bkups,int psSize,int horizon){
		startInit(pomdp,horizon);
		this.bkups=bkups;
		this.ps_size=psSize;
		this.max_new=psSize;
		this.prune=false;
		this.collect=PbAlgorithm.EXPAND_L1;
		current = pomdp.getEmptyValueFunction();
		current.push(getLowestAlpha(horizon));
		bset=null;
		endInit();
	}
	
	public void setMaxNewPoints(int maxNew) {
		max_new = maxNew;
	}
	
	public void setCollect(int collect) {
		this.collect = collect;
	}

	public void setPrune(boolean prune) {
		this.prune = prune;
	}
	

	@Override
	public IterationStats iterate() {
		startIteration();
		int time = horizon - count() - 1;	
		expand();
		
		old = current;
		for (int i = 0; i < bkups; i++) {
			BeliefValueFunction[][] projection = project(current, time + i);
			dynamicSetBackup(projection, time);
		}
		if (prune) {
			if (verbose)
				System.out.print("Pruning...");
			AlphaVectorStats iterationStats = (AlphaVectorStats) this.iterationStats;
			iterationStats.registerLp(current.prune());
		}
		endIteration();
		return iterationStats;
	}
	
	public void expand(){
		if (bset == null) {
			bset = new LinkedHashMap<Double,BeliefState>();
			BeliefState b0 = pomdp.getInitialBeliefState();
			b0.initChildMap(pomdp.actions(), pomdp.observations());
			bset.put(b0.naiveHash(),b0);
			//newBset = fullBset.copy();
		}
		int orig_size=bset.size();
		if (orig_size >= ps_size)
			return;
		LinkedHashMap<Double,BeliefState> testBset = new  LinkedHashMap<Double, BeliefState>();
		testBset.putAll(bset);
		while (bset.size() - orig_size < max_new && bset.size() < ps_size) {
			BeliefState point = null;
			switch (collect) {
				//case PbAlgorithm.EXPAND_PEMA:
				//	point = collectPEMA(testBset);
				//	break;
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
			if (point != null){
				Double hsh=point.naiveHash();
				if (!bset.containsKey(hsh))
					bset.put(hsh,point);
			}
			if (testBset.size() == 0) {
				if (verbose) 
					System.out.println("Collected points = " + bset.size());
				break;
			}
		}
		if (verbose)
			System.out.println("Size(PS)=" + bset.size());
		beliefArray = new ArrayList<BeliefState>(bset.values());
	}
	
	@Override
	public double performance() {
			return dynamicPerformance();
	}

	@Override
	public void reset() {
		current = pomdp.getEmptyValueFunction();
		current.push(getLowestAlpha(horizon));
		bset=null;
		super.reset();
	}
	
}
