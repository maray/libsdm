package libsdm.pomdp.rho;

import libsdm.common.SparseVector;
import libsdm.common.Utils;
import libsdm.mdp.TransitionMatrix;
import libsdm.pomdp.BeliefReward;
import libsdm.pomdp.ObservationMatrix;
import libsdm.pomdp.Pomdp;
import libsdm.pomdp.plain.DensePomdp;

public class CameraClean {
	static public final int PROB_CAMERA_CLEAN = 1;
//	static public final int PROB_CAMERA_CLEAN_SURVEILLANCE = 2;

	static public final int RTYPE_STATE = 1;
	static public final int RTYPE_ENTROPY = 2;
	static public final int RTYPE_LINEAR = 3;
	static public final int RTYPE_VARIANCE = 4;


	static public int CC_encode(int photo, int clean, int spos, int tpos,int cells){
		return photo * 2 * cells * cells +  clean * cells * cells + spos * cells + tpos;
	}
	
	static public Pomdp getPomdp(int reward_type, int cells,
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
	
	
}
