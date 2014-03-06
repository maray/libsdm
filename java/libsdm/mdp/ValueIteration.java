package libsdm.mdp;

import libsdm.common.IterationStats;
import libsdm.common.OfflineIteration;
import libsdm.common.Utils;

public class ValueIteration extends OfflineIteration {

	boolean async;
	boolean finite;
	ValueFunction current;
	ValueFunction old;
	protected Mdp mdp;
	private ValueFunction []memory;
	private ValueFunction init;
	
	public ValueIteration(Mdp mdp,ValueFunction init,int horizon,boolean async){
		startInit(horizon);
		if (horizon<1){
			finite=false;
			memory=null;
		}
		else {
			finite=true;
			memory=new ValueFunction[horizon];
		}
		this.mdp=mdp;
		this.async=async;
		if (finite && async)
			Utils.error("Finite horizon not compatible with asynchronous VI");	
		if (!finite && !mdp.isRewardStationary())
			Utils.error("Infinite horizon not compatible with non-stationary rewards");
		if (!finite && !mdp.isTransitionStationary() )
			Utils.error("Infinite horizon not compatible with non-stationary transitions");
		this.init=init;
		reset();
		endInit();
	}
	
	protected void reset(){
		this.current=init;
	}
	
	@Override
	public IterationStats iterate() {
		startIteration();
		old=current;
		if (verbose){
			System.out.println("VI: iteration "+count());
			//System.out.println(current);
		}		
		current=mdp.backup(old,async,horizon-count() - 1 );
		if (finite){
			memory[horizon-count() - 1]=current.copy();
		}
		endIteration();
		return iterationStats;
	}

	public ValueFunction getValueFunction() {
		return current;
	}
	
	
	public ValueFunction getValueFunction(int i) {
		if (!finite)
			return current;
		else
			return memory[i];
	}
	
	public ValueFunction getOldValueFunction() {
		return old;
	}


	
}

