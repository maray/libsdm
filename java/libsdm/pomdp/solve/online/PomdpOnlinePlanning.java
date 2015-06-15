package libsdm.pomdp.solve.online;

import libsdm.common.IterationStats;
import libsdm.pomdp.BeliefValueFunction;
import libsdm.pomdp.Pomdp;
import libsdm.pomdp.solve.offline.BlindPolicy;
import libsdm.pomdp.solve.offline.QmdpBound;

public class PomdpOnlinePlanning extends OnlinePomdpAlgorithm {
	
	static final double EPSILON_ACT_TH=1e-03;
	private AEMS2 heuristic;
	private BeliefValueFunction uBound;
	private AndOrTree aoTree;
	private BeliefValueFunction lBound;
	private HeuristicSearchOrNode rootNode;
	private int mtexpand;

	public PomdpOnlinePlanning(Pomdp pomdp,int mtexpand,int horizon){
		startInit();
		this.mtexpand=mtexpand;
		this.pomdp=pomdp;
		
		BlindPolicy blindCalc = new BlindPolicy(pomdp,horizon);
		blindCalc.run();
		lBound    = blindCalc.getValueFunction();
		QmdpBound qmdpCalc = new QmdpBound(pomdp,horizon);
		qmdpCalc.run();
		uBound=pomdp.getEmptyValueFunction();
		uBound.push(qmdpCalc.getAlphaVector());
		heuristic=new AEMS2(pomdp);
		current_belief=pomdp.getInitialBeliefState();
		current_action=get_best_action();
		//System.out.println(current_action);
		endInit();
	}
	
	@Override
	public IterationStats iterate(int observation) {
		startIteration();
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
            aoTree.expand(rootNode.bStar);
            // update its ancestors
            aoTree.updateAncestors(rootNode.bStar);
            if (aoTree.actionIsEpsOptimal(aoTree.currentBestAction(), EPSILON_ACT_TH))
                break;
		}
		int max_a = aoTree.currentBestAction();
		return max_a;
	}
	
	public void reset(){
		current_belief=pomdp.getInitialBeliefState();
        aoTree = new AndOrTree(pomdp,null, lBound, uBound,heuristic);
        aoTree.init(current_belief);
        rootNode = aoTree.getRoot();
        super.reset();
	}
	
}
