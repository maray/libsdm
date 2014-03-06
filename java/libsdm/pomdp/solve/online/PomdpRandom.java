package libsdm.pomdp.solve.online;

import libsdm.common.IterationStats;
import libsdm.common.Utils;
import libsdm.pomdp.Pomdp;

public class PomdpRandom extends OnlinePomdpAlgorithm {
	

	public PomdpRandom(Pomdp pomdp){
		startInit();
		this.pomdp=pomdp;
		current_action=Utils.gen.nextInt(pomdp.actions());
		//System.out.println(current_action);
		endInit();
	}
	
	@Override
	public IterationStats iterate(int observation) {
		startIteration();
		current_action=Utils.gen.nextInt(pomdp.actions());
		endIteration();
		return getStats();
	}
	
	public void reset(){
		current_action=Utils.gen.nextInt(pomdp.actions());	
		super.reset();
	}
	
	
}
