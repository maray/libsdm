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

/** Maximum Time Criterion.
 * This criterion can be used by any iteration algorithm.
 * Please note that this criterion will allow the algorithm to finish the current iteration
 * before stopping. 
 * @author Mauricio Araya
 */
public class MaxTimeCriterion extends Criterion {

	long max_time;
	
	/** Constructor
	 * 
	 * @param maxtime maximum time allowed for the algorithm in milliseconds.
	 */
	public MaxTimeCriterion(long maxtime) {
		verbose=false;
		this.max_time = maxtime;
	}

	@Override
	public boolean check(Iteration i) {
		if (i.getStats().totalTime() < max_time){
			return false;
		}
		if (verbose) 
			System.out.println("[STOP] Max Time Reached ("+i.getStats().totalTime()+" > "+ max_time +")");
		return true;
	}

	@Override
	public boolean valid(Iteration vi) {
		return true;
	}

}
