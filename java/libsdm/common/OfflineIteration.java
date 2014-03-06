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


/** Abstract offline iteration definition.
 * This class defines the iterate and run methods for offline iterations.
 * @author Mauricio Araya
 * @year 2010-2013
 */
public abstract class OfflineIteration extends Iteration {	
  
	
    /** This method performs an iteration.
     * @return the current iteration statistics
     */
    public abstract IterationStats iterate();
    
    
    /** Run all iterations until a criterion makes it stop.
     * @return the final iteration's statistics
     */
    public IterationStats run() {
    	while (!finished()) {
    	    iterate();
    	}
    	return iterationStats;
        }

    @Override
	protected void reset() {
		super.reset();
	}
	
}
