/*
 * Copyright (C) 2010-2011 Diego Maniloff
 * Copyright (C) 2010-2011 Mauricio Araya
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

/**
 * Abstract POMDP specification. 
 * Generic methods needed for an POMDP to be defined, 
 * queried and solved. This code is an adaptation of the early work of 
 * LibPOMDP {@linktourl https://github.com/dmaniloff/libpomdp}.
 * @author Mauricio Araya, Diego Maniloff
 * @year 2009-2012
 */

package libsdm.pomdp;

import java.util.Random;
import libsdm.common.SparseVector;
import libsdm.mdp.Mdp;

public interface Pomdp extends Mdp {

	// GETTERS
	//Observation Methods
	/// nrObs: total # of observations
    public int observations();
    
    /// observation names
    public String getObservationString(int o);

    
    // Rho function info
    // / R(b,a): scalar value
   // public double expectedImmediateReward(BeliefState b, int a);
    public BeliefValueFunction getRewardValueFunction(int a,int i);
 
    //One step belief state propagation
    /// tao(b,a,o)
    
    
    /// initial belief state
    public BeliefState getInitialBeliefState();

	//public ObservationMatrix getObservationFunction();
	
	public ObservationMatrix getObservationMatrix();

	public BeliefReward getRewardFunction();
	
	
	// CREATORS
	public BeliefValueFunction getEmptyValueFunction();

	public AlphaVector getEmptyAlpha();

	public AlphaVector getHomogeneAlpha(double bestVal);

	public AlphaVector getEmptyAlpha(int a);

	public AlphaVector getRewardAlpha(int a, BeliefState bel, int time);
	
	// OPERATIONS
	
	public BeliefValueFunction getLowerBound();
	
	public double getRewardMaxMin();
	
    public SparseVector observationProbabilities(BeliefState b, int a,int i);
  
	public AlphaVector blindProjection(AlphaVector vec, int a, int time);
	
	public BeliefState nextBeliefState(BeliefState b, int a, int o,int i);
	
	public AlphaVector projectAlpha(AlphaVector alpha, int a, int o, int time);

	public boolean isBlocked(int a, int o);
	
	
	// Samplings
	
    public int sampleObservation(BeliefState b, int a,int i);
	
    public int sampleNextObservation(int nstate, int action, Random gen);


	
    
} // Pomdp