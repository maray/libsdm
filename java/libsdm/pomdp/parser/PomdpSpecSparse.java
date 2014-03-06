/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: PomdpSpecStandard.java
 * Description: Object that contains all .POMDP file params
 * Copyright (c) 2009, 2010 Diego Maniloff 
 * Copyright (c) 2010 Mauricio Araya
 --------------------------------------------------------------------------- */

package libsdm.pomdp.parser;

// imports
import java.io.Serializable;
import java.util.ArrayList;
 
import libsdm.common.SparseMatrix;
import libsdm.common.SparseVector;

public class PomdpSpecSparse implements Serializable {

    // serial id
    static final long serialVersionUID = 1L;

    public boolean compReward;

    // discount factor
    public double discount;

    // number of states
    public int nrSta;

    // state list in case given as such
    public ArrayList<String> staList;

    // number of actions
    public int nrAct;

    // action list in case given as such
    public ArrayList<String> actList;

    // number of observations
    public int nrObs;

    // list of observations in case given as such
    public ArrayList<String> obsList;

    // start state
    public SparseVector startState;

    // transition matrices - a x s x s'
    // T: <action> : <start-state> : <end-state> %f
    public SparseVector T[][];

    // observation matrices - a x s' x o
    // O : <action> : <end-state> : <observation> %f
    public SparseVector O[][];

    // reward vectors - a x s
    // R: <action> : <start-state> : * : * %f
    public SparseVector R[];

    public SparseMatrix fullR[][];

} // PomdpSpecStandard
