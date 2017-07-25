package libsdm.pomdp.plain;

import java.io.Serializable;

import libsdm.common.DenseVector;
import libsdm.common.SparseMatrix;
import libsdm.common.SparseVector;
import libsdm.pomdp.AlphaVector;
import libsdm.pomdp.BeliefState;
import libsdm.pomdp.Pomdp;

public class PlainPomdpProjector implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5928693504174861602L;
	SparseMatrix tau[][];
	SparseMatrix OT[];
	boolean blocker[][];
    boolean stationary;
    Pomdp pomdp;
    
    public PlainPomdpProjector(Pomdp pomdp) {
    	this.pomdp=pomdp;
    	if (pomdp.isTransitionStationary()){
    		stationary=true;
    		init();
    	}
    	else{
    		stationary=false;
    	}
    }

    private void init() {
    	System.out.println("Computing TAU");
    	tau = new SparseMatrix[pomdp.observations()][pomdp.actions()];
    	OT = new SparseMatrix[pomdp.actions()];
    	blocker = new boolean[pomdp.observations()][pomdp.actions()];
    	for (int a = 0; a < pomdp.actions(); a++) {
    		SparseMatrix T = pomdp.getTransitionMatrix().getMatrix(a,0);
    		SparseMatrix O = pomdp.getObservationMatrix().getMatrix(a);
    		//System.out.println(O.getRow(0));
    		OT[a]=O.mult(T);
    		//Utils.error("STOP");
			SparseVector probs = OT[a].mult(1,SparseVector.getHomogene(pomdp.states(),1.0/((double)pomdp.states())));
			//probs.normalize();
			//System.out.println(probs.norm(1));
			for (int o = 0; o < pomdp.observations(); o++) {
				if (probs.get(o)!=0.0){
					blocker[o][a]=true;
				}
				SparseMatrix oDiag=SparseMatrix.getDiagonal(O.getRow(o));
				/*SparseMatrix oDiag = new SparseMatrix(pomdp.states(), pomdp.states());
				for (int s = 0; s < pomdp.states(); s++) {
					oDiag.assign(s, s, pomdp.getObservationMatrix().getValue(o,s,a));
				}*/
		// System.out.println(oDiag.toString());
				tau[o][a] = oDiag.mult(T);
	    	}
		}
    }

   
    
    public BeliefState projectBelief(BeliefState b, int a, int o,int i) {
    	SparseVector vect;
    	if (!blocker[o][a]) return(null);
    	
   		vect = getTau(a,o,i).mult(1,(SparseBeliefState)b);	
    	vect.normalize();
    	SparseBeliefState bel = SparseBeliefState.transform(vect);
		if (bel.getMask()==null){
			return null;
			//System.out.println("V = "+getTau(a,o,i).getColumn(69));
			//System.out.println("B = "+b);
			//Utils.warning("PROJBELIEF: Belief with complete mask Uhh?");
			//System.out.println("BP = "+bel);
			//Utils.error("Stopped... something is wrong...");
		}
    	return (bel);
    }
    
    private SparseMatrix getTau(int a, int o,int i) {
    	if (stationary)
    		return (tau[o][a]);
    	else{
    		//System.out.println("WRONG");
    		SparseMatrix oDiag=SparseMatrix.getDiagonal(pomdp.getObservationMatrix().getMatrix(a).getRow(o));
    	//SparseMatrix oDiag = new SparseMatrix(pomdp.states(), pomdp.states());
		//for (int s = 0; s < pomdp.states(); s++) {
		//	oDiag.assign(s, s, pomdp.getObservationMatrix().getValue(o,s,a));
		//}
		return (oDiag.mult(pomdp.getTransitionMatrix().getMatrix(a,i)));
    	}
    }
    
    public SparseMatrix getOT(int a){
    	return OT[a];
    }
    
    public AlphaVector projectAlpha(AlphaVector alpha, int a, int o,int i) {
    	if (!blocker[o][a]) return(null);
    	if (alpha instanceof SparseAlphaVector){
    		SparseAlphaVector nalpha=(SparseAlphaVector) alpha;
    		SparseVector vect = new SparseVector(pomdp.states());
    	//System.out.println(getTau(a,o,i));
    		vect.add(getTau(a,o,i).transmult(pomdp.gamma(), nalpha));
    		return (new SparseAlphaVector(vect, a));
    	}
    	else{
    		DenseAlphaVector nalpha=(DenseAlphaVector) alpha;
    		DenseVector vect = new DenseVector(pomdp.states());
    	//System.out.println(getTau(a,o,i));
    		vect.add(getTau(a,o,i).transmult(pomdp.gamma(), nalpha));
    		return (new DenseAlphaVector(vect, a));
    	}
    }

	//public int getRandomAction() {
	//	return pomdp.getRandomAction();
	//}

	public int sampleObservation(BeliefState b, int a, int i) {
		//SparseVector obsProb=new SparseVector(pomdp.observations());
		return OT[a].transmult(1,(SparseBeliefState)b).sample();
		/*for (int o=0;o<pomdp.observations();o++){
			obsProb.set(o,getTau(a,o,i).mult((SparseBeliefState)b).norm(1.0));
		}
		//System.out.println(obsProb);
		return obsProb.sample();
		*/
	}

	public boolean isBlocked(int a, int o) {
		return !blocker[o][a];
	}

	public SparseVector observationProbabilities(SparseBeliefState b, int a,
			int i) {
		//System.out.println(OT[a].getRow(0));
		//System.out.println(OT[a].getColumn(0));
		// Transmult?
		SparseVector vec = OT[a].mult(1,b);
		vec.normalize();
		//System.out.println(vec.norm(1));
		return vec;
	}



}
