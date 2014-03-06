package libsdm.mdp;

import java.util.Random;

public interface TransitionFunction {
	int sampleNextState(int state, int action, Random gen, int i);
	public boolean stationary();
	//SparseVector project(AlphaVector alphaVector, int a, int i);
}
