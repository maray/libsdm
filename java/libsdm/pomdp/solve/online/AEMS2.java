/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: aems2.java
 * Description: implementation of the heuristic interface for
 *              AEMS2 / Hansen's policy search - please refer to
 *              the README references [2] and [4] in the root dir
 * Copyright (c) 2009, 2010 Diego Maniloff
 --------------------------------------------------------------------------- */

package libsdm.pomdp.solve.online;

import libsdm.common.IterationStats;
import libsdm.common.Utils;
import libsdm.common.ValueSelector;
import libsdm.pomdp.Pomdp;

public class AEMS2  extends OnlinePomdpAlgorithm implements ExpandHeuristic{

	/// main property is the Pomdp spec
	private Pomdp problem;

	/// set the gen only once for every instance
	//private Random gen = new Random(System.currentTimeMillis());

	/// constructor
	public AEMS2 (Pomdp prob) {
		this.problem = prob;
		
	}

	/// H(b)
	@Override
	public double h_b(HeuristicSearchOrNode o) {
		// 	if(o.u - o.l < 0) System.err.println("bad H(b) at ornode" + o.getobs()  +
		// 					     " parent is action " + o.getParent().getAct()
		// 					     + " hb is: "+ (o.u - o.l));
		// 	System.err.println("upper is" + o.u);
		// 	System.err.println("lower is" + o.l);
		return o.u - o.l;
	}

	/// H(b,a)
	/// given that this value depends on the
	/// other branches as well we compute it
	/// at the orNode level
	@Override
	public double[] h_ba(HeuristicSearchOrNode o) {
		//ValueSelector UbA = new ValueSelector(true);
		double UbA[] = new double[problem.actions()];
		double Hba[]=new double[problem.actions()];
		for(HeuristicSearchAndNode a : o.getChildren())
			UbA[a.getAct()]= a.u;
		//	UbA[a.getAct()] = a.u;
		//Hba = DoubleArray.fill(problem.actions(), 0.0);
		// compute maximum upper bound
		// set the chosen action's Hba value to 1
		int aStar = Utils.argmax(UbA);
		Hba[aStar] = 1.0;
		// save this value
		o.aStar = aStar;
		// return
		return Hba;
	}

	/// H(b,a) update version
	/// does this work?? needs to be tested...
	//    public double[] h_baUpdate(orNode o, int a) {
	//	double challenge = o.children[a].u;
	//	int argmax = Utils.argmax(new double[] {o.children[o.aStar].u,
	//						 challenge});
	//	if(0==argmax) {
	//	    return o.h_ba;
	//	} else {
	//	    o.h_ba[o.aStar] = 0.0;
	//	    o.h_ba[a]       = 1.0;
	//	    o.aStar         = a;
	//	    return o.h_ba;
	//	}
	//    } // h_baUpdate

	/// H(b,a,o) = \gamma * P(o|b,a)
	@Override
	public double h_bao(HeuristicSearchOrNode o) {
		return problem.gamma() * o.getBeliefState().getPoba();
	}

	/// H*(b,a) = \max_o {H(b,a,o) H*(tao(b,a,o))}
	@Override
	public double hANDStar(HeuristicSearchAndNode a) {
		//System.out.println(a.getChild(a.oStar));
		return a.getChild(a.oStar).h_bao * a.getChild(a.oStar).hStar;
	}

	/// H*(b) = H(b,a*) H*(b,a*)
	@Override
	public double hORStar(HeuristicSearchOrNode o) {
		return o.h_ba[o.aStar] * o.getChild(o.aStar).hStar;
	}

	/// o* = argmax_o {H(b,a,o) H*(tao(b,a,o))}
	@Override
	public int oStar(HeuristicSearchAndNode a) {
		double HbaoHostar[] = new double[problem.observations()];
		//for(orNode o : a.children) HbaoHostar[o.getobs()] = o.h_bao * o.hStar;
		//return Utils.argmax(HbaoHostar);

		// STILL TO THINK ABOUT:
		//int nullCount=0;
		//for(orNode o : a.children)
		for(int o=0; o<problem.observations(); o++) {
			if(a.getChild(o) != null) {
				HbaoHostar[o] = a.getChild(o).h_bao *
						a.getChild(o).hStar;
			} else {
				HbaoHostar[o] = Double.NEGATIVE_INFINITY; // do this to preserve the argmax
				//nullCount++;
			}
			//System.out.println("o"+o+"="+HbaoHostar[o]);
			
		}
		// System.out.println("nullcount inside oStar is "+ nullCount);
		// 	System.out.println(DoubleArray.toString(HbaoHostar));
		// 	int argmax = Utils.argmax(HbaoHostar);
		// 	if (argmax < 0 || argmax >= problem.getnrObs())
		// 	    System.err.println("armax out of bounds");
		// 	if (HbaoHostar[argmax] == -1) {
		// 	    System.err.println("Hba[argmax]=-1");
		// 	    //System.exit(0);
		// 	}
		//return argmax;
		return Utils.argmax(HbaoHostar);
	}

	/// update version with challenge obs
	/// INCORRECT
	// public int oStarUpdate(andNode a, int o) {
	// 	double challenge = a.children[o].h_bao * a.children[o].hStar;
	// 	int argmax = Utils.argmax(new double[] {a.children[a.oStar].h_bao * a.children[a.oStar].hStar,
	// 						 challenge});
	// 	if(0==argmax)
	// 	    return a.oStar;
	// 	else
	// 	    return o;
	//     }

	/// a* = argmax_a {H(b,a) H*(b,a)}
	/// given the way H(b,a) is computed in AEMS2, we don't
	/// need to compute the actual argmax, we just return the
	/// stored value from the computation of H(b,a)
	@Override
	public int aStar(HeuristicSearchOrNode o) {
		return o.aStar;
	}

	@Override
	public IterationStats iterate(int evidence) {
		// TODO Auto-generated method stub
		return null;
	}

} // aems2