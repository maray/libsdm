/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: AndOrTree.java
 * Description: data structure to hold the AND-OR tree for online search
 *              The constructor takes a heuristic object H that enables the
 *              implementation of different heuristic search methods
 * Copyright (c) 2009, 2010 Diego Maniloff
 --------------------------------------------------------------------------- */

package libsdm.pomdp.solve.online;

import java.io.PrintStream;

import libsdm.common.SparseVector;
import libsdm.common.Utils;
import libsdm.mdp.ValueFunction;
import libsdm.pomdp.BeliefState;
import libsdm.pomdp.BeliefValueFunction;
import libsdm.pomdp.Pomdp;

public class AndOrTree extends AbstractAndOrTree {

	// ------------------------------------------------------------------------
	// properties
	// ------------------------------------------------------------------------

	// expansion heuristic
	protected ExpandHeuristic expH;

	// ------------------------------------------------------------------------
	// methods
	// ------------------------------------------------------------------------

	/// constructor
	public AndOrTree(Pomdp prob,
			HeuristicSearchOrNode root,
			BeliefValueFunction L,
			BeliefValueFunction U,
			ExpandHeuristic h) {
		super(prob, root, L, U);
		this.expH = h;
	}

	/// initializer
	public void init(BeliefState startingBelief) {
		getRoot().init(startingBelief, -1, null);
		getRoot().u = getUB().value(startingBelief);
		getRoot().l = getLB().value(startingBelief);
		// should have separate plan ids to avoid this!
	}

	/**
	 * expand(HeuristicSearchOrNode en):
	 * one-step expansion of |A||O| HeuristicSearchOrNodes
	 */
	public void expand(HeuristicSearchOrNode en){
		// make sure this node hasn't been expanded before
		if (en.getChildren() != null) {
			System.err.println("node not on fringe");
			return;
		}
		// declarations
		HeuristicSearchAndNode a;
		HeuristicSearchOrNode  o;
		// poba vector for each action
		SparseVector pOba;
		// save this node's old bounds
		double old_l = en.l;
		double old_u = en.u;
		// allocate space for the children AND nodes
		en.initChildren(getProblem().actions());
		// iterate through the AND nodes
		for (int action = 0; action < getProblem().actions(); action++) {
			a = en.getChild(action);
			// initialize this node, precompute Rba
			a.init(action, en, getProblem().getRewardFunction().eval(en.getBeliefState(), action,-1));
			// pre-compute observation probabilities for the children of this node
			pOba = getProblem().observationProbabilities(en.getBeliefState(), action,0);
			// allocate space for the children OR nodes, the ones with poba == 0
			// are left null
			a.initChildren(getProblem().observations(), pOba);
			// iterate through new fringe OR nodes
			//System.out.println("act "+action);
			//System.out.println(pOba);
			for (int observation = 0; observation < getProblem().observations(); observation++) {
				o = a.getChild(observation);
				
			    //System.out.println("obs "+observation+" poba="+pOba.get(observation));
				// ZERO-PROB OBSERVATIONS:
				// here we should continue the loop and avoid re-computing V^L and V^U
				// for belief nodes with poba == 0
				if (pOba.get(observation) == 0) {
					//a.children[observation] = null; - already done!
					//observation++;
					//System.out.println("obs 0="+observation);
					continue;
				}
				// initialize this node, set its poba
				o.init(getProblem().nextBeliefState(en.getBeliefState(), action, observation,0), observation, a);
				o.getBeliefState().setPoba(pOba.get(observation));
				// compute upper and lower bounds for this node
				o.u = getUB().value(o.getBeliefState());
				o.l = getLB().value(o.getBeliefState());
				// H(b)
				o.h_b = expH.h_b(o);
				// H(b,a,o)
				o.h_bao = expH.h_bao(o);
				// H*(b) will be H(b) upon creation
				o.hStar = o.h_b;
				// bStar is a reference to itself since o is a fringe node
				o.bStar = o;
				// increase subtree size of en accordingly
				en.setSubTreeSize(en.getSubTreeSize() + 1);
			} // HeuristicSearchOrNode loop

			// update values in a
			// L(b,a) = R(b,a) + \gamma \sum_o P(o|b,a)L(tao(b,a,o))
			a.l = ANDpropagateL(a);
			a.u = ANDpropagateU(a);
			// observation in the path to the next node to expand
			a.oStar = expH.oStar(a);
			//System.out.println(a.oStar);
			// H*(b,a)
			a.hStar = expH.hANDStar(a);
			// b*(b,a) - propagate ref of b*
			a.bStar = a.getChild(a.oStar).bStar;
		}  // andNode loop

		
		// update values in en
		en.l = ORpropagateL(en);
		en.u = ORpropagateU(en);
		// update H(b)
		en.h_b = expH.h_b(en);
		// H(b,a)
		en.h_ba = expH.h_ba(en);
		// best action
		// a_b = argmax_a {H(b,a) * H*(b,a)}
		en.aStar = expH.aStar(en);
		// value of best heuristic in the subtree of en
		// H*(b) = H(b,a_b) * H*(b,a_b)
		en.hStar = expH.hORStar(en);
		// update reference to best fringe node in the subtree of en
		en.bStar = en.getChild(en.aStar).bStar;
		// one-step improvement for debugging purposes
		en.oneStepDeltaLower = en.l - old_l;
		en.oneStepDeltaUpper = en.u - old_u;
		//this.orprint(en, System.out);
		if(en.oneStepDeltaLower < 0) System.err.println("Hmmmmmmmmmmm");
	} // expand


	/**
	 * updateAncestors(HeuristicSearchOrNode n):
	 * update the ancestors of a given HeuristicSearchOrNode
	 */
	public void updateAncestors(HeuristicSearchOrNode n) {
		// make sure this is not the call after expanding the root
		if (null == n.getChildren()) return;
		//System.out.println("update");
		// if array.length does not count nulls, then we could use that here...
		// could also just keep n untouched, and use o from the beginning...
		int subTreeSizeDelta = n.getSubTreeSize();

		HeuristicSearchAndNode a;
		HeuristicSearchOrNode  o;

		while(n != getRoot()) { // reference comparison
			// get the AND parent node
			a = n.getParent();
			// update the andNode that is parent of n
			a.l = ANDpropagateL(a);
			a.u = ANDpropagateU(a);
			// best observation
			a.oStar = expH.oStar(a);
			// H*(b,a)
			a.hStar = expH.hANDStar(a);
			// b*(b,a) - propagate ref of b*
			a.bStar = a.getChild(a.oStar).bStar;

			// get the OR parent of the parent
			o = a.getParent();
			// update the HeuristicSearchOrNode that is parent of the parent
			o.l = ORpropagateL(o);
			o.u = ORpropagateU(o);
			// H(b,a)
			o.h_ba = expH.h_ba(o);
			// best action
			o.aStar = expH.aStar(o);
			// value of best heuristic in the subtree of en
			o.hStar = o.h_ba[o.aStar] * o.getChild(o.aStar).hStar;
			// update reference to best fringe node in the subtree of en
			o.bStar = o.getChild(o.aStar).bStar;
			// increase subtree size accordingly
			o.setSubTreeSize(o.getSubTreeSize() + subTreeSizeDelta);
			// iterate
			n = o;
		}
	} // updateAncestors

	/// L(b,a) = R(b,a) + \gamma \sum_o P(o|b,a) L(tao(b,a,o))
	protected double ANDpropagateL(HeuristicSearchAndNode a) {
		double Lba = 0;
		for(HeuristicSearchOrNode o : a.getChildren()) {
			// o.belief.getpoba() == 0 for null HeuristicSearchOrNodes anyway
			if(o != null) Lba += o.getBeliefState().getPoba() * o.l;
		}
		return a.rba + getProblem().gamma() * Lba;
	}

	/// U(b,a) = R(b,aobservationProbabilities) + \gamma \sum_o P(o|b,a) U(tao(b,a,o))
	protected double ANDpropagateU(HeuristicSearchAndNode a) {
		double Uba = 0;
		for(HeuristicSearchOrNode o : a.getChildren()) {
			// o.belief.getpoba() == 0 for null HeuristicSearchOrNodes anyway
			if(o != null) Uba += o.getBeliefState().getPoba() * o.u;
		}
		return a.rba + getProblem().gamma() * Uba;
	}

	/// L(b) = max{max_a L(b,a),L(b)}
	protected double ORpropagateL(HeuristicSearchOrNode o) {
		double maxLba = Double.NEGATIVE_INFINITY;
		for(HeuristicSearchAndNode a : o.getChildren()) {
			if(a.l > maxLba)
				maxLba = a.l;
		}
		// compare to current bound
		return Math.max(maxLba, o.l);
	}

	/// U(b) = min{max_a U(b,a),U(b)}
	protected double ORpropagateU(HeuristicSearchOrNode o) {
		double maxUba = Double.NEGATIVE_INFINITY;
		for(HeuristicSearchAndNode a : o.getChildren()) {
			if(a.u > maxUba)
				maxUba = a.u;
		}
		// compare to current bound
		return Math.min(maxUba, o.u);
	}


	@Override
	public HeuristicSearchOrNode getRoot() {
		return (HeuristicSearchOrNode) super.getRoot();
	}

	// /// if I understood the gc's behaviour
	// /// correctly, moving the root of the tree
	// /// and setting the new root's parent to null
	// /// automagically deletes all the useless subtrees
	// public void moveTree(HeuristicSearchOrNode newroot) {
	// 	this.root = newroot;
	// 	this.root.disconnect();
	// }

	/// return best action given the current state
	/// of expansion in the whole tree
	/// this function is randomized!!!!
	public int currentBestAction() {
		// construct array with L(b,a)
		double Lba[] = new double[getProblem().actions()];
		for(HeuristicSearchAndNode a : getRoot().getChildren())
			Lba[a.getAct()] = a.l;
		return Utils.argmax(Lba);
	}

	/// check if an action is epsilon-optimal given the current
	/// state of the search tree - this assumes act was previously
	/// obtained from currentBestAction....
	/// there' probably a better way to do this, maybe by maintaining
	/// the best action for every belief node in the tree and updating it
	public boolean actionIsEpsOptimal(int act, double OPT_EPSILON) {
		// first condition
		if (Math.abs(getRoot().u - getRoot().l) < OPT_EPSILON) return true;
		// second condition
		boolean opt1 = true;
		for(HeuristicSearchAndNode a : getRoot().getChildren()) {
			if ((a.getAct() != act) && (getRoot().l < a.u)) {
				opt1 = false;
				break;
			}
		}
		return opt1;
	}


	// DO NOT USE
	//    public boolean currentBestActionIsOptimal(double OPT_EPSILON) {
	//	boolean opt1 = true;
	//	boolean opt2 = false;
	//	int bestA = currentBestAction();
	//	for(andNode a : getRoot().children) {
	//	    if ((getRoot().l < a.u) && (a.getAct() != bestA)) { // BUGG
	//		opt1 = false;
	//		break;
	//	    }
	//	}
	//	if (Math.abs(getRoot().u - getRoot().l) < OPT_EPSILON)
	//	    opt2 = true;
	//	// either condition is enough
	//	return opt1 || opt2;
	//    }


	/// return best action according to the subtree
	/// rooted at on
	/// this function is randomized!!!!
	//    public int currentBestActionAtNode(HeuristicSearchOrNode on) {
	//	// construct array with L(b,a)
	//	double Lba[] = new double[getProblem().getnrAct()];
	//	for(andNode a : on.children)
	//	    Lba[a.getAct()] = a.l;
	//	return Utils.argmax(Lba);
	//    }

	// public ValueFunction getLB() {
	// 	return offlineLower;
	// }

	// public ValueFunction getUB() {
	// 	return offlineUpper;
	// }

	/// output a dot-formatted file to print the tree
	/// starting from a given HeuristicSearchOrNode
	public void printdot(String filename) {
		HeuristicSearchOrNode root = getRoot();
		PrintStream out = null;
		try {
			out = new
					PrintStream(filename);
		}catch(Exception e) {
			System.err.println(e.toString());
		}
		//out = System.out;
		// print file headers
		out.println("strict digraph T {");
		// print node
		orprint(root,out);
		// print closing
		out.println("}");
	}

	/// print HeuristicSearchOrNode
	private void orprint(HeuristicSearchOrNode o, PrintStream out) {
		// print this node
		@SuppressWarnings("unused")
		String b = "";
		b = "b=[\\n " +
				o.getBeliefState().getPoint() +
				"]\\n";
		out.format(o.hashCode() + "[label=\"" +
				//b +
				"U(b)= %.2f\\n" +
				"L(b)= %.2f\\n" +
				"H(b)= %.2f" +
				"\"];\n", o.u, o.l, o.h_b);
		// every or node has a reference to be best node in its subtree
		out.println(o.hashCode() + "->" + o.bStar.hashCode() +
				"[label=\"b*\",weight=0,color=blue];");
		// check it's not in the fringe before calling andprint
		if (o.getChildren() == null) return;
		// print outgoing edges from this node
		for(HeuristicSearchAndNode a : o.getChildren()) {
			out.print(o.hashCode() + "->" + a.hashCode() +
					"[label=\"" +
					"H(b,a)=" + o.h_ba[a.getAct()] +
					"\"];");
		}
		out.println();
		// recurse
		for(HeuristicSearchAndNode a : o.getChildren()) andprint(a, out);
	}

	/// print andNode
	protected void andprint(HeuristicSearchAndNode  a, PrintStream out) {
		// print this node
		out.format(a.hashCode() + "[label=\"" +
				"a=" + getProblem().getActionString(a.getAct()) + "\\n" +
				"U(b,a)= %.2f\\n" +
				"L(b,a)= %.2f" +
				"\"];\n", a.u, a.l);
		// print outgoing edges for this node
		for(HeuristicSearchOrNode o : a.getChildren()) {
			if (!(o==null))
				out.format(a.hashCode() + "->" + o.hashCode() +
						"[label=\"" +
						"obs: " + getProblem().getObservationString(o.getObs()) + "\\n" +
						"P(o|b,a)= %.2f\\n" +
						"H(b,a,o)= %.2f" +
						"\"];\n",
						o.getBeliefState().getPoba(),
						o.h_bao);
		}
		out.println();
		// recurse
		for(HeuristicSearchOrNode o : a.getChildren()) if(!(o==null)) orprint(o,out);
	}

	
} // AndOrTree