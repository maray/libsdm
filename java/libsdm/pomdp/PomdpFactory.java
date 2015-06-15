package libsdm.pomdp;

import libsdm.common.SparseVector;
import libsdm.common.Utils;
import libsdm.mdp.TransitionMatrix;
import libsdm.pomdp.plain.DensePomdp;
import libsdm.pomdp.reward.EntropyReward;
import libsdm.pomdp.reward.LinearReward;
import libsdm.pomdp.reward.RDReward;
import libsdm.pomdp.reward.RDRewardBias;
import libsdm.pomdp.reward.VarianceReward;

public class PomdpFactory {
	static public final int PROB_CAMERA_CLEAN = 1;
//	static public final int PROB_CAMERA_CLEAN_SURVEILLANCE = 2;

	static public final int RTYPE_STATE = 1;
	static public final int RTYPE_ENTROPY = 2;
	static public final int RTYPE_LINEAR = 3;
	static public final int RTYPE_VARIANCE = 4;

	static public Pomdp getProblem(int problem, int reward_type) {
		switch (problem) {
//		case PROB_CAMERA_CLEAN_DIAGNOSIS:
//			return getCameraCleanDiagnosis(reward_type, 0.8, 0.55, 0);
		case PROB_CAMERA_CLEAN:
			return getCameraClean(reward_type, 3, 0.8, 0.55, 0.1, 0,false);
		default:
			Utils.error("Unknown Problem...");
			return null;
		}
	}

	static public Pomdp getProblem(int problem) {
		return getProblem(problem, RTYPE_STATE);
	}

	static public int CC_encode(int photo, int clean, int spos, int tpos,int cells){
		return photo * 2 * cells * cells +  clean * cells * cells + spos * cells + tpos;
	}
	
	static public Pomdp getCameraClean(int reward_type, int cells,
			double cprob, double dprob, double moveprob, int horizon, boolean localization) {
		BeliefReward rew = null;

		int states = 2 * 2 * cells * cells ;
		String stateLabels[] = new String[states];
		
		for (int photo=0;photo<2;photo++){
			for (int clean=0;clean<2;clean++){
				String photo_string;
				String clean_string;
				if (photo==0)
					photo_string="nophoto";
				else
					photo_string="photo";
				if (clean==0)
					clean_string="dirty";
				else
					clean_string="clean";
				for (int spos = 0; spos < cells; spos++) {
					for (int tpos = 0; tpos < cells; tpos++) {
						stateLabels[CC_encode(photo,clean,spos,tpos,cells)] = photo_string + "-" + clean_string + "-s" + Integer.toString(spos)
							+ "-t" + Integer.toString(tpos);
					}
				}
			}
		}
		int actions = 3;
		String actionLabels[] = { "clean", "shoot", "move" };
		int observations = 2;
		String observationLabels[] = { "0", "1" };

		switch (reward_type) {
		case RTYPE_STATE:
			Utils.error("Camera Clean does not support State-Based Rewards");
			return null;
		case RTYPE_ENTROPY:
			rew = new EntropyReward(states, actions, horizon);
			break;
		case RTYPE_LINEAR:
			rew = new LinearReward(states, actions, horizon);
			break;
		case RTYPE_VARIANCE:
			rew = new VarianceReward(states, actions, horizon);
			break;
		}
		//INIT
		SparseVector initialBelief = new SparseVector(states);
		SparseVector[][] transMat = new SparseVector[states][actions];
		SparseVector[][] obsMat = new SparseVector[states][actions];
		for (int s = 0; s < states; s++) {
			for (int a = 0; a < actions; a++) {
				transMat[s][a] = new SparseVector(states);
				obsMat[s][a] = new SparseVector(observations);
			}
		}
		//PRIOR
		if (localization){
			for (int spos=0;spos<cells;spos++){
				int to=CC_encode(0,1,spos,0,cells);
				initialBelief.assign(to, 1.0/((double)cells));
			}
		}
		else {
			for (int tpos = 0; tpos < cells; tpos++){
				int to=CC_encode(0,1,0,tpos,cells);
				initialBelief.assign(to, 1.0/((double)cells));
			}
		}

		int from;
		int to;
		for (int photo = 0; photo < 2; photo++) {
			for (int clean = 0; clean < 2; clean++) {
				for (int a = 0; a < actions; a++) {
					for (int spos = 0; spos < cells; spos++) {
						for (int tpos = 0; tpos < cells; tpos++) {
							// Transition Model
							from = CC_encode(photo, clean, spos, tpos, cells);
							int spos_to = spos;
							int photo_to = photo;
							int clean_to = clean;
							if (a == 0) { // Clean
								photo_to = 0;
								clean_to = 1;
							}
							if (a == 1) { // Shoot
								photo_to = 1;
								clean_to = 0;
								if (photo == 0)
									clean_to = clean;
							}
							if (a == 2) { // Move
								if (photo==1)
									clean_to=0;
								photo_to = 0;
								spos_to = (spos + 1) % cells;
							}
							// move target to next cell
							to = CC_encode(photo_to, clean_to, spos_to,
									(tpos + 1) % cells, cells);
							transMat[from][a].assign(to, moveprob);
							// target stay still and no photo
							to = CC_encode(photo_to, clean_to, spos_to, tpos,
									cells);
							transMat[from][a].assign(to, 1 - moveprob);
							double prob = 0.5; // no photo
							if (photo == 1 && clean == 1) { // clean photo
								if (spos == tpos)
									prob = cprob;
								else
									prob = 1 - cprob;
							}
							if (photo == 1 && clean == 0) { // dirty photo
								if (spos == tpos)
									prob = dprob;
								else
									prob = 1 - dprob;
							}
							obsMat[from][a].assign(0, 1 - prob);
							obsMat[from][a].assign(1, prob);
						}
					}
				}
			}
		}
		ObservationMatrix obs = new ObservationMatrix(obsMat);
		TransitionMatrix trans = new TransitionMatrix(transMat);
		DensePomdp cc = new DensePomdp(obs, trans, rew, states, actions,
				observations, 0.95, stateLabels, actionLabels,
				observationLabels, initialBelief);
		return (cc);
	}
	
	
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

	
	static public Pomdp getRockDiagnosis(int type,int xsize,int ysize,int metals,int rocks,double rocks_xpos[],double rocks_ypos[],double eta,int horizon){
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
			cc = new DensePomdp(obs, trans, (BeliefReward) new RDReward(type,rocks_configs,states,actions,horizon), states, actions,
				observations, 0.95, stateLabels, actionLabels,
				observationLabels, initialBelief);
		}
		else
		{
			cc = new DensePomdp(obs, trans, (BeliefReward) new RDRewardBias(type,rocks_configs,states,actions,eta,horizon), states, actions,
					observations, 0.95, stateLabels, actionLabels,
					observationLabels, initialBelief);
		}
	
		return (cc);
	}
	
}
