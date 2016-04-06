package libsdm.pomdp.solve.online;

import libsdm.common.IterationStats;
import libsdm.pomdp.BeliefValueFunction;
import libsdm.pomdp.Pomdp;
import libsdm.pomdp.solve.offline.AlphaConvergence;
import libsdm.pomdp.solve.offline.BlindPolicy;
import libsdm.pomdp.solve.offline.FastInformedBound;
import libsdm.pomdp.solve.offline.QmdpBound;

public class PomdpOnlinePlanning extends OnlinePomdpAlgorithm {
	
	static final double EPSILON_ACT_TH=1e-06;
	private AEMS2 heuristic;
	private BeliefValueFunction uBound;
	private AndOrTree aoTree;
	private BeliefValueFunction lBound;
	private HeuristicSearchOrNode rootNode;
	private int mtexpand;

	public PomdpOnlinePlanning(Pomdp pomdp,int mtexpand,int horizon,boolean verbose){
		startInit();
		this.mtexpand=mtexpand;
		this.pomdp=pomdp;
		
		BlindPolicy blindCalc = new BlindPolicy(pomdp,horizon);
		AlphaConvergence conv = new AlphaConvergence(EPSILON_ACT_TH);
		conv.setSort(false);
		blindCalc.addStopCriterion(conv);
		blindCalc.setVerbose(verbose);
		blindCalc.run();
		lBound    = blindCalc.getValueFunction();
		FastInformedBound fib = new FastInformedBound(pomdp, EPSILON_ACT_TH);
		fib.addStopCriterion(conv);
		fib.setVerbose(verbose);
		fib.run();
		//QmdpBound qmdpCalc = new QmdpBound(pomdp,horizon);
		//qmdpCalc.setVerbose(verbose);
		//qmdpCalc.run();
		uBound=pomdp.getEmptyValueFunction();
		uBound.push(fib.getAlphaVector());
		heuristic=new AEMS2(pomdp);
		//reset();
		endInit();
	}
	
	@Override
	public IterationStats iterate(int observation) {
		startIteration();
		//System.out.println(current_action);
		//System.out.println(rootNode);
		//System.out.println(rootNode.getChild(current_action));
		current_belief=pomdp.nextBeliefState(current_belief, current_action, observation,count());
		aoTree.moveTree(rootNode.getChild(current_action).getChild(observation));
		rootNode = aoTree.getRoot();
		current_action=get_best_action();
		endIteration();
		return getStats();
	}
	
	public int get_best_action(){
		long init_time=System.currentTimeMillis();
		while (System.currentTimeMillis() < init_time + this.mtexpand){
			// expand best node
			//System.out.println("enter");
            aoTree.expand(rootNode.bStar);
            // update its ancestors
            aoTree.updateAncestors(rootNode.bStar);
            
            if (aoTree.actionIsEpsOptimal(aoTree.currentBestAction(), EPSILON_ACT_TH))
                break;
            //System.out.println("time");
		}
		int max_a = aoTree.currentBestAction();
		return max_a;
	}
	
	public void reset(){
		current_belief=pomdp.getInitialBeliefState();
		rootNode = new HeuristicSearchOrNode();
		rootNode.init(current_belief, -1, null);
        aoTree = new AndOrTree(pomdp,rootNode, lBound, uBound,heuristic);
        aoTree.init(current_belief);
        rootNode = aoTree.getRoot();
        current_action=get_best_action();
        System.out.println("Reset Called");
		//System.out.println(current_action);
		//System.out.println(rootNode);
		//System.out.println(rootNode.getChild(current_action));
	}
	
}
