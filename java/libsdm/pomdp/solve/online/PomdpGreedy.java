package libsdm.pomdp.solve.online;

import libsdm.common.IterationStats;
import libsdm.common.DenseVector;
import libsdm.common.SparseVector;
import libsdm.common.ValueSelector;
import libsdm.pomdp.BeliefState;
import libsdm.pomdp.Pomdp;

public class PomdpGreedy extends OnlinePomdpAlgorithm {
	

	public PomdpGreedy(Pomdp pomdp){
		startInit();
		this.pomdp=pomdp;
		current_belief=pomdp.getInitialBeliefState();
		current_action=expected_best_action(0);
		//System.out.println(current_action);
		endInit();
	}
	
	@Override
	public IterationStats iterate(int observation) {
		startIteration();
		current_belief=pomdp.nextBeliefState(current_belief, current_action, observation,count());
		current_action=expected_best_action(count()+1);
		endIteration();
		return getStats();
	}
	
	public int expected_best_action(int i){
		ValueSelector bests=new ValueSelector(ValueSelector.MAX);
		for (int a=0;a<pomdp.actions();a++){
			SparseVector var = pomdp.observationProbabilities(current_belief, a,i);
			DenseVector vec=new DenseVector(pomdp.observations());
			for (int o=0;o<pomdp.observations();o++){
				BeliefState bel = pomdp.nextBeliefState(current_belief, a, o,i);
				if (bel!=null)
					vec.set(o,pomdp.getRewardFunction().eval(bel, a,i+1));
			}
			double exval = vec.dot((SparseVector)var);
			bests.put(new Integer(a), exval);
		}
		return ((Integer) bests.getRandom()).intValue();
	}
	
	public void reset(){
		current_belief=pomdp.getInitialBeliefState();
		current_action=expected_best_action(0);
		super.reset();
	}
	
	
}
