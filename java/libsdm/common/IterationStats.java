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

import java.util.ArrayList;

/** Basic iteration statistics.
 * @author Mauricio Araya
 * @year 2010-2013
 */
public class IterationStats {

	protected long init_time;
	protected ArrayList<Long> iteration_time;
	protected int iterations;

	public IterationStats() {
		iteration_time = new ArrayList<Long>();
		iterations = 0;
	}
	
	/** Saves a new iteration time.
	 * 
	 * @param iTime the time that the iteration took.
	 * @return the next iteration number
	 */
	public int register(long iTime) {
		iteration_time.add(new Long(iTime));
		iterations++;
		return (iterations);
	}
	
	public String toString() {
		String retval = "ITERATION STATISTICS\n";
		retval += "-----------------\n";
		retval += "iterations = " + iterations + "\n";
		retval += "initialization time = " + init_time + "\n";
		retval += "iteration time  = ";
		long sum = 0;
		for (Long l : iteration_time) {
			sum += l.longValue();
		}
		retval += sum + "\n";
		return retval;

	}
	
	/** Compute the total time of the algorithm.
	 *  
	 * @return the initialization time plus the time for each iteration
	 */
	public long totalTime(){
		long sum=init_time;
		for (Long l : iteration_time) {
			sum += l.longValue();
		}
		return sum;
	}

}