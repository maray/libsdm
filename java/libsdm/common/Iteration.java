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

/** Abstract iteration definition.
 * This class defines the basic methods common to all iteration schemes.
 * @author Mauricio Araya
 * @year 2010-2013
 */
public abstract class Iteration {

    protected IterationStats iterationStats;
    protected ArrayList<Criterion> stopCriterias;
    protected long tempTime;
    protected int horizon;
    protected boolean verbose;
    
    /** Get iteration statistics. 
     * 
     * @return a {@link IterationStats} structure.
     */
    public IterationStats getStats() {
    	return (iterationStats);
    }
    
    /** Verify if any of the stopping criteria were met. 
     * 
     * @return true if at least one of the criteria was met.
     */
    public boolean finished() {
	for (Criterion sc : stopCriterias) {
	    if (sc.check(this))
		return true;
	}
	return false;
    }

    
    /** Adds a stopping criterion for the iteration. 
     * This function also checks the compatibility of the criterion with the iteration type. 
     * @param a {@link Criterion} object
     */
    public void addStopCriterion(Criterion sc) {
	if (sc.valid(this))
	    stopCriterias.add(sc);
	else
		Utils.warning("Incompatible Performance Criterion Added");
    }

    /** Starts the initialization timer.
     * This method MUST be called before the actual code of the initialization.
     * 
     */
    protected void startInit(int horizon) {
    	tempTime = System.currentTimeMillis();
    	stopCriterias = new ArrayList<Criterion>();
    	if (horizon>=1)
    		addStopCriterion(new MaxIterationsCriterion(horizon));
    	if (iterationStats==null)
    		iterationStats = new IterationStats();
    	verbose=false;
    	this.horizon=horizon;
    }
    
    /** Ends the intialization.
     * This method MUST be called after the actual code of the initialization.
     */
    protected void endInit() {
    	iterationStats.init_time = System.currentTimeMillis() - tempTime;
      }
    
    /** Starts the iteration timer.
     * This method MUST be called before the actual code of the iteration.
     * 
     */
    protected void startIteration() {
    	tempTime = System.currentTimeMillis();
    }
    
    
    /** Ends the iteration.
     * This method MUST be called after the actual code of the iteration.
     */
    protected void endIteration() {
    	iterationStats.register(System.currentTimeMillis() - tempTime);
        }
        
    
    /** Resets the iteration for a new use.
     *
     */
    protected void reset() {
    	iterationStats.iteration_time.clear();
    	iterationStats.iterations=0;
    }
    
    
    /** Set or unset verbose output for the algorithm and criteria.
     * 
     * @param verbose 
     */
	public void setVerbose(boolean verbose){
		for (Criterion cri : this.stopCriterias){
			cri.setVerbose(verbose);
		}
		this.verbose=verbose;
	}
    
    public int count(){
    	return iterationStats.iterations;
    }
    


}