package libsdm.mdp;

import libsdm.pomdp.BeliefState;


public interface ValueFunction {

	double performance(ValueFunction oldv, int convCriteria);
	ValueFunction copy();
	double [] getArray();
}
