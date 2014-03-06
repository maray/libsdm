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


/** Abstract online iteration definition.
 * This class defines the iterate and act methods for online iterations.
 * @author Mauricio Araya
 * @year 2010-2013
 */
public abstract class OnlineIteration extends Iteration {
	
	/** Performs an iteration given some evidence.
	 * The evidence is the new information for this iteration. For example
	 * in an MDP is a state, and in a POMDP is an observation.
	 * TODO: In general the evidence may be any object, for now is only a simple integer.
	 * 
	 * @param evidence the new evidence for this iteration
	 * @return the current iteration statistics
	 */
	public abstract IterationStats iterate(int evidence);
	
	/** The action for this iteration.
	 * @return the action selected by the algorithm for this iteration.
	 */
	public abstract int act();
	
	@Override
	public void reset(){
		super.reset();
	}
	
	/** Starts an infinite iteration.
	 * Is infinite, because online iterations can be stopped at any time.
	 */
	public void startInit(){
		super.startInit(0);
	}
	
}
