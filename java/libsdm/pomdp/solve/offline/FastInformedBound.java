/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: BlindPolicy.java
 * Description: 
 * Copyright (c) 2010 Mauricio Araya
 --------------------------------------------------------------------------- */

package libsdm.pomdp.solve.offline;

import libsdm.common.IterationStats;
import libsdm.pomdp.AlphaVector;
import libsdm.pomdp.BeliefValueFunction;
import libsdm.pomdp.Pomdp;

public class FastInformedBound extends AlphaIteration {


	public FastInformedBound(Pomdp pomdp,double qmdp_epsilon) {
		startInit(pomdp,0);
		QmdpBound ibound=new QmdpBound(pomdp,qmdp_epsilon);
		ibound.setVerbose(false);
		ibound.run();
		//System.out.println(ibound.getAlphaVector());
		current=pomdp.getEmptyValueFunction();
		for (int a=0;a<pomdp.actions();a++)
			current.push(ibound.getAlphaVector().copy());
		endInit();
    }

    @Override
    public IterationStats iterate() {
    	startIteration();
    	old = current;
    	current=pomdp.getEmptyValueFunction();
    	int time=horizon - count() - 1;
    	for (int a = 0; a < pomdp.actions(); a++) {
    		AlphaVector beta = pomdp.getEmptyAlpha();
    		for (int o=0;o<pomdp.observations();o++){
    			if (pomdp.isBlocked(a, o)) continue;
    			BeliefValueFunction vf = pomdp.getEmptyValueFunction();
    			for (int i = 0; i < pomdp.actions(); i++){
        			vf.push(pomdp.projectAlpha(old.getAlpha(i), a, o,time));
    			}
    			//System.out.println(vf);
    			AlphaVector alpha=vf.getUpperBound();
    			//System.out.println(alpha);
        		/*AlphaVector minibeta = pomdp.getEmptyAlpha();
    			for (int s=0;s<pomdp.states();s++){
   					SparseVector vect = Tmat.get(s, a, 0).copy();
					vect.elementMult(Omat.getRow(o, a));
					double pmax=Double.NEGATIVE_INFINITY;
    				for (int i = 0; i < pomdp.actions(); i++){
    					AlphaVector alph= old.getAlpha(i);
    					double myval=vect.dot((SparseVector) alph);
    					if (myval > pmax)
    						pmax=myval;
    				}
    				minibeta.setValue(s, pmax);
    			}
    			*/
    			beta.add(alpha);
    		}
    		//beta.scale(pomdp.gamma());
    		beta.add(pomdp.getRewardFunction().getBorderValues(a, 0));
    		//System.out.println(beta);
    		current.push(beta);
    	}
	//System.out.println(current);
    	endIteration();
    	return iterationStats;
    }


	@Override
	public void reset() {
		current = pomdp.getLowerBound();
		super.reset();
	}

}