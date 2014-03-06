package libsdm.pomdp.solve.online;

import libsdm.common.OnlineIteration;
import libsdm.pomdp.BeliefState;
import libsdm.pomdp.Pomdp;

public abstract class OnlinePomdpAlgorithm extends OnlineIteration {
	protected BeliefState current_belief;
	protected int current_state;
	protected int current_action;
	protected Pomdp pomdp;
	
	/**
	 * @return the current_belief
	 */
	public BeliefState getBelief() {
		return current_belief;
	}

	public int act() {
		return(current_action);
	}

}
