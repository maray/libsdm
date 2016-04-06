package libsdm.pomdp;

import libsdm.common.SparseVector;
import libsdm.mdp.RewardMatrix;
import libsdm.mdp.TransitionMatrix;
import libsdm.pomdp.plain.SparseAlphaVector;
import libsdm.pomdp.plain.SparseBeliefState;
import libsdm.pomdp.plain.SparseBeliefValueFunction;

public class PomdpReward implements BeliefReward  {

	//protected boolean compReward;
	protected RewardMatrix mdpReward;
	
	protected boolean stationary; 
	protected SparseAlphaVector func[];
	protected TransitionMatrix tr;
	//protected SparseMatrix fullR[][];
	
	public PomdpReward(SparseVector[] r){
		//compReward=false;
		stationary=true;
		this.mdpReward=new RewardMatrix(r);
		initReward(r);
	}
	
	/*public PomdpReward(SparseVector[] r,SparseMatrix fullR[][]){
		//compReward=true;
		stationary=true;
		this.mdpReward=new RewardMatrix(fullR);
		//this.fullR=fullR;
		initReward(r);
	}*/
	
	public PomdpReward(RewardMatrix mdpReward,TransitionMatrix tr, int states, int actions){
		this.mdpReward=mdpReward;
		this.tr=tr;
		//compReward=true;
		if (mdpReward.stationary() && tr.stationary()){
			stationary=true;
			SparseVector[] r;
			r=new SparseVector[states]; 
			for (int a=0; a<actions;a++){
				r[a]=mdpReward.get(a, 0, tr);
			}
			initReward(r);
		}
		else
		{	
			stationary=false;
		}
	}
	
	protected void initReward(SparseVector[] r) {
		//int states=r[0].size();
		int actions=r.length;
		func = new SparseAlphaVector[actions];
		//rewardMax=new double[actions];
		//rewardMin=new double[actions];
		
		for(int a=0;a<actions;a++){
			func[a]=SparseAlphaVector.transform(r[a]);
			func[a].setAction(a);
		}
		/*
		totalMin = Double.POSITIVE_INFINITY;
		totalMax = Double.NEGATIVE_INFINITY;
		for (int a = 0; a < actions; a++) {
		    rewardMin[a]=Double.POSITIVE_INFINITY;;
		    rewardMax[a]=Double.NEGATIVE_INFINITY;;
		    for (int s=0;s<states;s++){
		    	if (func[a].get(s) > rewardMax[a])
		    		rewardMax[a]=func[a].get(s);
		    	if (func[a].get(s) < rewardMin[a])
		    		rewardMin[a]=func[a].get(s);
		    }
		    if (rewardMax[a] > totalMax){
		    	totalMax=rewardMax[a];
		    }
		    if (rewardMin[a] < totalMin){
		    	totalMin=rewardMin[a];
		    }
		}
		*/
	}
		
	//public SparseAlphaVector getVector(int a) {
	//	return func[a].copy();
	//}

	public double eval(BeliefState b, int a,int i) {
		if (stationary)
			return func[a].dot((SparseBeliefState)b);
		else
			return ((SparseBeliefState)b).dot(mdpReward.get(a, i, tr));
	}

	public double min(int a) {
		return mdpReward.min(a);
	}

	public double max(int a) {
		return mdpReward.max(a);
	}
	
	public double min() {
		return mdpReward.min();
	}

	public double max() {
		return mdpReward.max();
	}

	public double get(int state, int action, int nstate,int i) {
		//if (compReward){
		//	return fullR[action][state].get(nstate,0);
		//}
		return mdpReward.get(state, action,nstate, i);
	}

	/*public void approx(PointSet fullBset) {
		Utils.warning("A PWLC approximation of a state-based reward function is useless...");
	}*/

	public BeliefValueFunction getValueFunction(int a,int i) {
		SparseBeliefValueFunction vf = new SparseBeliefValueFunction(); 
		if (stationary){
			vf.push(func[a].copy());
		}else
		{
			vf.push(SparseAlphaVector.transform(mdpReward.get(a, i, tr),a));
		}
		return vf;
	}

	public RewardMatrix getMdpReward() {
		return (RewardMatrix) mdpReward;
	}

	public SparseVector get(int s, int a,int i) {
		
		//if (compReward){
		//	return fullR[a][s].getColumn(0);
		//}
		return mdpReward.get(s, a, i);	
	}

	public boolean stationary() {
		return stationary;
	}

	public AlphaVector getTangentAlpha(int a,BeliefState bel,int i) {
		return func[a];
	}
	
	public SparseAlphaVector getBorderValues(int a,int i) {
		return func[a];
	}

}
