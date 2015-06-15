/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: AndNode.java
 * Description: class for an AND node in an AND-OR tree
 * Copyright (c) 2009, 2010 Diego Maniloff  
 --------------------------------------------------------------------------- */

package libsdm.pomdp.solve.online;

import libsdm.common.SparseVector;

public abstract class AndNode {

    // should these be private too?
    protected OrNode parent_;
    protected OrNode children_[];
    private   int    act_;

    /// initialization
    public void init(int action, OrNode parent) {
	parent_           = parent;
	act_              = action;
    }

    public abstract OrNode getParent();

    public abstract OrNode getChild(int i); 

    public abstract OrNode[] getChildren();

    public int getAct() {
	return act_;
    }

	public abstract void initChildren(int nrObs, SparseVector pOba);

}