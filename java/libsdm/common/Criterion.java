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

/** Abstract stopping criterion definition.
 * This class defines which are the basic methods of stopping criteria.
 * @author Mauricio Araya
 * @year 2011-2013
 */
public abstract class Criterion {
	
	protected boolean verbose;
	
	/** Check if the stopping criteria holds.
	 * 
	 * @param iter The {@link Iteration} object to check 
	 * @return true if the stopping criteria holds
	 */
    public abstract boolean check(Iteration iter);

    /** Check if the criteria could be used over the given iteration class.
     * 
     * @param iter The {@link Iteration} object for which this criteria is trying to be used on
     * @return true if the criteria is valid for iter
     */
    public abstract boolean valid(Iteration iter);

    /** Set the criterion to produce or not a verbose output.
     * 
     * @param verbose 
     */
	public void setVerbose(boolean verbose){
		this.verbose=verbose;
	}
	
	/** Check if the criterion produce verbose output.
	 * @return true if the criterion produce verbose output
	 */
	public boolean isVerbose() {
		return verbose;
	}
}
