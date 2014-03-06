package libsdm.pomdp.solve.online;

import libsdm.common.IterationStats;
import libsdm.pomdp.Pomdp;
import libsdm.pomdp.solve.offline.AlphaIteration;

public class ExecPomdpVF extends OnlinePomdpAlgorithm {
	

	protected AlphaIteration solution;
	
	public ExecPomdpVF(Pomdp pomdp,AlphaIteration solution){
		startInit();
		this.pomdp=pomdp;
		this.solution=solution;
		current_belief=pomdp.getInitialBeliefState();
		current_action=solution.getValueFunction(0).getBestAlpha(current_belief).getAction();
		endInit();
	}
	
	
	@Override
	public IterationStats iterate(int observation) {
		startIteration();
		current_belief=pomdp.nextBeliefState(current_belief, current_action, observation,count());
		current_action=solution.getValueFunction(count()+1).getBestAlpha(current_belief).getAction();
		endIteration();
		return getStats();
	}
	
	public void reset(){
		current_belief=pomdp.getInitialBeliefState();
		current_action=solution.getValueFunction(0).getBestAlpha(current_belief).getAction();
		super.reset();
	}
	
}
