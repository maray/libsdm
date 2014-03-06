package libsdm.pomdp;

import java.util.Random;

import libsdm.common.DenseVector;
import libsdm.common.SparseMatrix;
import libsdm.common.SparseVector;
import libsdm.pomdp.plain.DenseAlphaVector;
import libsdm.pomdp.plain.SparseAlphaVector;

public class ObservationMatrix {

	protected int states;
	protected int actions;
	protected int observations;
	protected SparseVector model[][];
	protected SparseMatrix matrixModel[];
	
	public ObservationMatrix(SparseVector[][] o) {
		states=o.length;
		actions=o[0].length;
		observations=o[0][0].size();
		model=o;
		matrixModel=new SparseMatrix[actions];
		for(int a=0;a<actions;a++){
			matrixModel[a]=new SparseMatrix(observations,states);
			for(int s=0;s<states;s++){
				matrixModel[a].assignColumn(s, o[s][a]);
			}
		}
	}

	//public SparseVector getRow(int o, int a) {
	//	return matrixModel[a].getRow(o);
	//}

	
	public double getValue(int o, int s, int a) {
		return model[s][a].get(o);
	}

	//public SparseVector project(SparseVector vec,int a) {
	//	return matrixModel[a].mult(vec);
	//}

	public int sampleNextObservation(int s, int a, Random gen) {
		return model[s][a].sample(gen);
	}

	public SparseMatrix getMatrix(int a) {
		return(this.matrixModel[a]);
	}

	public SparseVector project(SparseAlphaVector vec, int a) {
		return matrixModel[a].mult(1,(SparseVector) vec);
	}
	
	public DenseVector project(DenseAlphaVector vec, int a) {
		return matrixModel[a].mult(1,(DenseVector) vec);
	}
	
	/*public SparseMatrix getMatrix(int a){
		return(model[s][a]);
	}*/

}
