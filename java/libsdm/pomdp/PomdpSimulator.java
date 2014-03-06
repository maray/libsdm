package libsdm.pomdp;

import java.util.Random;

import libsdm.common.DenseVector;
import libsdm.pomdp.solve.online.OnlinePomdpAlgorithm;

public class PomdpSimulator {
	private boolean verbose;
	private Pomdp pomdp;
	BeliefState bel;
	//private IntegerVector action_vector;

	//public IntegerVector getActs() {
	//	return action_vector;
	//}

	public BeliefState getBel() {
		return bel;
	}

	public void setBel(BeliefState bel) {
		this.bel = bel;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public PomdpSimulator(Pomdp pomdp){
		this.pomdp=pomdp;
		verbose=false;
	}
	
	public DenseVector simulate(OnlinePomdpAlgorithm algo,int horizon, long seed, int[] endStates){
		Random gen = new Random(seed);
		algo.reset();
		int action=-1;
		//double total_reward=0.0;
		DenseVector res=new DenseVector(horizon);
		//action_vector=new IntegerVector(horizon);
		bel=pomdp.getInitialBeliefState();
		int state=bel.sample(gen);
		int observation=-1;
		int i=0;
		BeliefReward rewfunc = (BeliefReward) pomdp.getRewardFunction();
		boolean end=false;
		for (i=0;i<horizon;i++){
			if (end)
				break;
			action=algo.act();
			//System.out.println(i);
			if (verbose){
				System.out.print("** Sim t="+i+" ");
				String ss = pomdp.getStateString(state);
				if (ss!=null)
					System.out.print("s="+ss+" ");
				else
					System.out.print("s="+state+" ");
				if (observation!=-1){
					String so = pomdp.getObservationString(observation);
					if (so!=null)
						System.out.print("o="+so+" ");
					else
						System.out.print("o="+observation+" ");
				}
				String sa = pomdp.getActionString(action);
				if (sa!=null)
					System.out.println("=> a="+sa);
				else
					System.out.println("=> a="+action);
				//System.out.print("Belief = ");
				//System.out.println(bel);
			}
			int nstate=pomdp.sampleNextState(state,action,gen,i);
			observation=pomdp.sampleNextObservation(nstate,action,gen);
			//System.out.println(observation);
			//System.out.println(action);
			bel=pomdp.nextBeliefState(bel, action, observation,i);
			if (rewfunc instanceof PomdpReward)
				res.set(i,rewfunc.get(state, action, nstate,i));
			else
				res.set(i,rewfunc.eval(bel, action, i+1));
			//action_vector.set(i,action);
			algo.iterate(observation);
			if (verbose)
				System.out.println("reward="+res.get(i));
			state=nstate;
			if (endStates==null) continue;
			for (int k=0;k<endStates.length;k++){
				if (state==endStates[k])
					end=true;
			}
		}
		//res.resize(i);
		return res;
	}
	
	public DenseVector simulate(OnlinePomdpAlgorithm algo,int horizon, int[] endStates){
		return(simulate(algo,horizon,System.currentTimeMillis(),endStates));
	}
	
	
	public DenseVector simulate(OnlinePomdpAlgorithm algo,int horizon){
		return(simulate(algo,horizon,null));
	}
}
