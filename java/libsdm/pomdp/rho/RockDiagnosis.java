package libsdm.pomdp.rho;

import libsdm.common.SparseVector;
import libsdm.mdp.TransitionMatrix;
import libsdm.pomdp.BeliefReward;
import libsdm.pomdp.ObservationMatrix;
import libsdm.pomdp.Pomdp;
import libsdm.pomdp.plain.DensePomdp;

public class RockDiagnosis {


	static public final int RTYPE_STATE = 1;
	static public final int RTYPE_ENTROPY = 2;
	static public final int RTYPE_LINEAR = 3;
	static public final int RTYPE_VARIANCE = 4;


	
	public static int getRDConfigState(int config,int rock,int metals){
		int power=(int)Math.pow(metals,rock);
		config=config/power;
		return config%metals;
	}
	
	static String PrintRDconfig(int config,int metals,int rocks){
		String retval="";
		for (int r=0;r<rocks;r++){
			int mstate=getRDConfigState(config,r,metals);
			retval=retval+Integer.toString(mstate);
		}
		return retval;
	}

	static int RD_encode(int x,int y,int config,int xsize,int rocks_configs){
		return (y*xsize + x)*rocks_configs + config;
	}

	
	static public Pomdp getPomdp(int type,int xsize,int ysize,int metals,int rocks,double rocks_xpos[],double rocks_ypos[],double eta,int horizon){
		int rocks_configs=(int)Math.pow(metals, rocks);
		int grid_positions=xsize*ysize;
		int states=grid_positions * rocks_configs;
		String stateLabels[] = new String[states];	
		for (int x=0;x<xsize;x++){
			for (int y=0;y<ysize;y++){
				for (int config=0;config<rocks_configs;config++){
					int pos=RD_encode(x,y,config,xsize,rocks_configs);
					stateLabels[pos] = "X" + Integer.toString(x) + "-Y" + Integer.toString(y) + "-R" + PrintRDconfig(config,metals,rocks) ;
				}
			}
		}
		int actions = 4+rocks;
		String actionLabels[] = new String[actions]; 
		actionLabels[0]="north";
		actionLabels[1]="east";
		actionLabels[2]="south";
		actionLabels[3]="west";
		for (int r=0;r<rocks;r++){
			actionLabels[4+r]="check-" + Integer.toString(r);
		}
		int observations = metals+1;
		String observationLabels[] = new String[observations];
		for (int m=0;m<metals;m++){
			observationLabels[m]="m-"+Integer.toString(m);
		}
		observationLabels[metals]="none";
		SparseVector initialBelief = new SparseVector(states);
		SparseVector[][] transMat = new SparseVector[states][actions];
		SparseVector[][] obsMat = new SparseVector[states][actions];
		for (int s = 0; s < states; s++) {
			for (int a = 0; a < actions; a++) {
				transMat[s][a] = new SparseVector(states);
				obsMat[s][a] = new SparseVector(observations);
			}
		}
		// PRIOR
		for (int config = 0; config < rocks_configs; config++) {
			int pos = RD_encode(0, 0, config, xsize, rocks_configs);
			double bel_prob = 1.0 / ((double) rocks_configs);
			initialBelief.assign(pos, bel_prob);
		}
		// Transition Function
		for (int config = 0; config < rocks_configs; config++) {
			for (int x=0;x<xsize;x++){
				for (int y=0;y<xsize;y++){
					int from=RD_encode(x,y,config,xsize,rocks_configs);
					for (int a=0;a<actions;a++){
						int xTo=x;
						int yTo=y;
						switch(a){
						case 0: // NORTH
							yTo--;
							break;
						case 1: // EAST
							xTo++;
							break;
						case 2: // SOUTH
							yTo++;
							break;
						case 3: // WEST
							xTo--;
							break;
						}
						// Borders
						if (xTo<0 || xTo>=xsize)
							xTo=x;
						if (yTo<0 || yTo>=ysize)
							yTo=y;
						transMat[from][a].assign(RD_encode(xTo,yTo,config,xsize,rocks_configs),1.0);
					}
				}
			}
		}
		// Observation Function
		for (int config = 0; config < rocks_configs; config++) {
			for (int x=0;x<xsize;x++){
				for (int y=0;y<xsize;y++){
					int from=RD_encode(x,y,config,xsize,rocks_configs);
					for (int a=0;a<actions;a++){
						if (a<4){
							obsMat[from][a].assign(metals,1.0); // NONE result
						}
						else{
							int rock=a-4;
							double distance=Math.sqrt(Math.pow(x-rocks_xpos[rock],2) + Math.pow(y-rocks_ypos[rock],2));
							double effi=Math.exp(-distance);
							double vis_prob=effi*1.0/((double)metals) + ((double)(metals-1))/((double)metals);
							int myMetal=getRDConfigState(config,rock,metals);
							for (int m=0;m<metals;m++){
								if (m==myMetal)
									obsMat[from][a].assign(m,vis_prob);
								else
									obsMat[from][a].assign(m,(1-vis_prob)/((double)metals - 1));
							}
						}
					}
				}
			}
		}
		DensePomdp cc;
		ObservationMatrix obs = new ObservationMatrix(obsMat);
		TransitionMatrix trans = new TransitionMatrix(transMat);
		if (eta==0){
			cc = new DensePomdp(obs, trans, (BeliefReward) new RockDiagnosisReward(type,rocks_configs,states,actions,horizon), states, actions,
				observations, 0.95, stateLabels, actionLabels,
				observationLabels, initialBelief);
		}
		else
		{
			cc = new DensePomdp(obs, trans, (BeliefReward) new RockDiagnosisRewardBias(type,rocks_configs,states,actions,eta,horizon), states, actions,
					observations, 0.95, stateLabels, actionLabels,
					observationLabels, initialBelief);
		}
	
		return (cc);
	}
	
}
