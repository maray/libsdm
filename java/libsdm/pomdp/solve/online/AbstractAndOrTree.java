package libsdm.pomdp.solve.online;

import libsdm.pomdp.BeliefValueFunction;
import libsdm.pomdp.Pomdp;

public abstract class AbstractAndOrTree {

    private Pomdp problem;
    private BeliefValueFunction offlineLower;
    private BeliefValueFunction offlineUpper;
    private OrNode root;

    public AbstractAndOrTree(Pomdp problem,
			     OrNode r,
			     BeliefValueFunction l,
			     BeliefValueFunction u) {
        this.problem = problem;
        this.offlineLower = l;
        this.offlineUpper = u;
        this.root = r;
    }

    public Pomdp getProblem() {
        return problem;
    }

    public OrNode getRoot() {
        return root;
    }

    public void setRoot(OrNode newRoot) {
        root = newRoot;
    }
    
    public void moveTree(OrNode newroot) {
        setRoot(newroot);
        root.disconnect();
    }

    public BeliefValueFunction getLB() {
        return offlineLower;
    }

    public void setLB(BeliefValueFunction lb) {
        offlineLower = lb;
    }

    public BeliefValueFunction getUB() {
        return offlineUpper;
    }

    public void setUB(BeliefValueFunction ub) {
        offlineUpper = ub;
    }

}