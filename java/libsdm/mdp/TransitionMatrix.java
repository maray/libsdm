package libsdm.mdp;

import java.util.Random;

import libsdm.common.DenseVector;
import libsdm.common.SparseMatrix;
import libsdm.common.SparseVector;
import libsdm.pomdp.plain.DenseAlphaVector;

public class TransitionMatrix implements TransitionFunction {

	protected int states;
	protected int actions;
	protected SparseVector model[][];
	protected SparseMatrix[] matrixModel;

	public TransitionMatrix(SparseVector[][] t) {
		states = t.length;
		actions = t[0].length;
		// System.out.println("actions="+actions);
		// System.out.println("states="+states);
		model = t;
		matrixModel = new SparseMatrix[actions];
		for (int a = 0; a < actions; a++) {
			matrixModel[a] = new SparseMatrix(states, states);
			for (int s = 0; s < states; s++) {
				matrixModel[a].assignColumn(s, t[s][a]);
			}
		}
	}

	public TransitionMatrix(int ns, int na) {
		states = ns;
		actions = na;
		model = new SparseVector[states][actions];
		matrixModel = new SparseMatrix[actions];
		for (int a = 0; a < actions; a++) {
			for (int s = 0; s < states; s++) {
				model[s][a] = SparseVector.getUniform(states);
				matrixModel[a] = SparseMatrix.getUniform(states, states);
			}
		}
		
	}


	public int sampleNextState(int state, int action) {
		SparseVector vec = model[state][action];
		return vec.sample();
	}

	public static TransitionMatrix getRandom(int states, int actions) {
		SparseVector[][] model = new SparseVector[states][actions];
		for (int a = 0; a < actions; a++) {
			for (int s = 0; s < states; s++)
				model[s][a] = SparseVector.getRandomUnitary(states);
		}
		return new TransitionMatrix(model);
	}

	public static TransitionMatrix getUniform(int states, int actions) {
		SparseVector[][] model = new SparseVector[states][actions];
		for (int a = 0; a < actions; a++) {
			for (int s = 0; s < states; s++)
				model[s][a] = SparseVector.getUniform(states);
		}
		return new TransitionMatrix(model);
	}

	/*public static TransitionMatrix getDeterministicRandom(int states,
			int actions) {
		SparseVector[][] model = new SparseVector[states][actions];
		for (int a = 0; a < actions; a++) {
			for (int s = 0; s < states; s++) {
				SparseVector vec = new SparseVector(states);
				vec.setExpensive(Utils.gen.nextInt(states), 1.0);
				model[s][a] = vec;
			}
		}
		return new TransitionMatrix(model);
	}
	*/

	public void set(int s, int a, SparseVector av) {
		model[s][a] = av;
		matrixModel[a].assignColumn(s, av);
	}
	

	public SparseMatrix getMatrix(int a, int i) {
		return matrixModel[a];
	}

	public int sampleNextState(int state, int action, Random gen, int i) {
		SparseVector vec = model[state][action];
		return vec.sample(gen);
	}

	public boolean stationary() {
		return true;
	}

	public SparseVector get(int s, int a, int i) {
		return model[s][a];
	}

	public DenseVector project(DenseVector vec, int a, int i) {
		return matrixModel[a].mult(1,vec);
	}
	
	public SparseVector project(SparseVector vec, int a, int i) {
		return matrixModel[a].mult(1,vec);
	}

}
