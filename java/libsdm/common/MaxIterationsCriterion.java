/*
 * Copyright (C) 2010-2013 Mauricio Araya
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package libsdm.common;

/** Maximum Iterations Criterion.
 * This criterion can be used by any iteration algorithm. 
 * @author Mauricio Araya
 */
public class MaxIterationsCriterion extends Criterion {

	int max_iter;
	
	/** Constructor
	 * 
	 * @param maxIter maximum iterations allowed.
	 */
	public MaxIterationsCriterion(int maxIter) {
		verbose=false;
		this.max_iter = maxIter;
	}

	@Override
	public boolean check(Iteration i) {
		if (i.getStats().iterations < max_iter){
			return false;
		}
		if (verbose) 
			Utils.log("[STOP] Max Iterations Reached ("+ max_iter +")\n");
		return true;
	}

	@Override
	public boolean valid(Iteration vi) {
		return true;
	}


}
