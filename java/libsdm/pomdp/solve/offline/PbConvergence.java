/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: ValueConvergence.java
 * Description: 
 * Copyright (c) 2010, 2011 Mauricio Araya
 --------------------------------------------------------------------------- */

package libsdm.pomdp.solve.offline;

import libsdm.common.Criterion;
import libsdm.common.Iteration;
import libsdm.pomdp.BeliefValueFunction;

public class PbConvergence extends Criterion {

	double epsilon;
	int convCriteria;

	// Horrible hack... :S
	static final int MIN_ITERATIONS = 5;

	public boolean check(Iteration i) {
		PbAlgorithm vi = (PbAlgorithm) i;
		if (vi.getOldValueFunction() == null || vi.getValueFunction() == null)
			return false;
		BeliefValueFunction newv = vi.getValueFunction();
		double conv = vi.performance();
		if (verbose) {
			System.out.println("Eval(" + i.count() + ") = " + conv + " ("
					+ newv.size() + " vectors)");
			// System.out.println(newv);
		}
		//TODO: Check Hack
		if (conv <= epsilon && i.count() > MIN_ITERATIONS) {
			if (verbose)
				System.out.println("[STOP] Point Based convergence (" + conv
						+ " < " + epsilon + ")");
			return (true);
		}
		return false;
	}

	@Override
	public boolean valid(Iteration vi) {
		if (vi instanceof PbAlgorithm) {
			return true;
		}
		return false;
	}

	public PbConvergence(double epsilon) {
		this.epsilon = epsilon;
		this.setVerbose(false);
	}

}
