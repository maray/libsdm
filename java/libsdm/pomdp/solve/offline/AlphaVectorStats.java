/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: AlphaVectorStats.java
 * Description: 
 * Copyright (c) 2010, 2011 Mauricio Araya
 --------------------------------------------------------------------------- */

package libsdm.pomdp.solve.offline;

import java.util.ArrayList;

import libsdm.common.IterationStats;

public class AlphaVectorStats extends IterationStats {

    public long total_lp_time;
    public ArrayList<Integer> iteration_vector_count;
    
    public AlphaVectorStats() {
	super();
	iteration_vector_count = new ArrayList<Integer>();
	total_lp_time = 0;
    }

    public void registerLp(long iTime) {
	total_lp_time += iTime;
    }

    /*
     * public int register(long iTime,int nVects) {
     * iteration_vector_count.add(new Integer(nVects)); register(iTime);
     * return(iterations); }
     */

    public String toString() {
	String retval = super.toString();
	retval += "last vector count  = ";
	Integer i = iteration_vector_count
		.get(iteration_vector_count.size() - 1);
	retval += i + "\n";
	return retval;
    }

    
}
