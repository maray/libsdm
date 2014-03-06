/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: Pomdp.java
 * Description: interface to represent Pomdp problem specifications
 * Copyright (c) 2009, 2010 Diego Maniloff 
 --------------------------------------------------------------------------- */

package libsdm.mdp;

import java.util.Random;

public interface Mdp {
    
    //public RewardFunction getRewardFunction();

    /// total # of states
    public int states();

    /// total # of actions
    public int actions();

    /// \gamma
    public double gamma();

    /// action names
    public String getActionString(int a);

    /// state names
    public String getStateString(int s);

	public int getRandomAction();	
	
	public void changeRewardFunction(RewardFunction R);

	public ValueFunction backup(ValueFunction old,boolean async,int i);
	
//	public ValueFunction lowerBound();

	public int initialState();

	public void setGamma(double d);

	public boolean isRewardStationary();

	public boolean isTransitionStationary();

	public int sampleNextState(int state, int action, Random gen, int i);
	
	public RewardFunction getRewardFunction();
	
	public TransitionMatrix getTransitionMatrix();

} // Mdp