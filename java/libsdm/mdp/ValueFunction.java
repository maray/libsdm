package libsdm.mdp;


public interface ValueFunction {

	double performance(ValueFunction oldv, int convCriteria);
	ValueFunction copy();
	double [] getArray();
}
