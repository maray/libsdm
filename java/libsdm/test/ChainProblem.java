package libsdm.test;

import libsdm.common.SparseVector;
import libsdm.mdp.Mdp;
import libsdm.mdp.RewardMatrix;
import libsdm.mdp.SparseMdp;
import libsdm.mdp.TransitionMatrix;

public class ChainProblem {
	
	static int size=5;	

	public static final int NORMAL=0;
	public static final int DETERM=1;
	public static final int MIXED=2;
	
	public static Mdp getMdp(int sta,int type,double gamma,double prob){
		size=sta;
		TransitionMatrix model;
		switch(type){
		case DETERM:
			model=create_det_chain_transition();
			break;
		case MIXED:
			model=create_mixed_chain_transition();
			break;
		default:
		case NORMAL:
			model=create_normal_chain_transition(prob);
			break;

		}
		return new SparseMdp(model,create_chain_reward(), size, 2, gamma, null, null, 0);	
	}
		
	public static RewardMatrix create_chain_reward() {
		SparseVector[][] re=new SparseVector[size][2];
		for (int a=0;a<2;a++){
			for (int s=0;s<size;s++){
				re[s][a]=new SparseVector(size);
				re[s][a].assign(0,0.2);
			}
			re[size-1][a].assign(size-1,1);
		}
		return new RewardMatrix(re);
	}
	
	public static TransitionMatrix create_det_chain_transition() {
		TransitionMatrix model = new TransitionMatrix(size,2); 
		//SparseMatrix[] mat=new SparseMatrix[2];  
		for (int s=0;s<size;s++){
			SparseVector a0 = new SparseVector(size);
			SparseVector a1 = new SparseVector(size);
			a0.assign(0,0);
			a1.assign(0,1);
			if (s!=size-1){
				a0.assign(s+1,1);
				a1.assign(s+1,0);
			}
			else{
				a0.assign(size-1,1);
				a1.assign(size-1,0);
			}
			model.set(s,0,a0);
			model.set(s,1,a1);
		}
		return model;
	}
	
	public static TransitionMatrix create_mixed_chain_transition() {
		TransitionMatrix model = new TransitionMatrix(size,2); 
		for (int s=0;s<size;s++){
			SparseVector a0 = new SparseVector(size);
			SparseVector a1 = new SparseVector(size);
			a0.assign(0,0.5);
			a1.assign(0,1.0);
			if (s!=size-1){
				a0.assign(s+1,0.5);
			}
			else{
				a0.assign(size-1,0.5);
			}
			model.set(s,0,a0);
			model.set(s,1,a1);
		}
		return model;
	}

	public static TransitionMatrix create_normal_chain_transition(double prob) {
		TransitionMatrix model = new TransitionMatrix(size,2); 
		//SparseMatrix[] mat=new SparseMatrix[2];  
		for (int s=0;s<size;s++){
			SparseVector a0 = new SparseVector(size);
			SparseVector a1 = new SparseVector(size);
			a0.assign(0,1-prob);
			a1.assign(0,prob);
			if (s!=size-1){
				a0.assign(s+1,prob);
				a1.assign(s+1,1-prob);
			}
			else{
				a0.assign(size-1,prob);
				a1.assign(size-1,1-prob);
			}
			model.set(s,0,a0);
			model.set(s,1,a1);
		}
		return model;
	}
	
}
