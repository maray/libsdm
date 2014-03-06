package libsdm.pomdp.solve.offline;

import libsdm.common.OfflineIteration;
import libsdm.common.Utils;
import libsdm.pomdp.BeliefValueFunction;
import libsdm.pomdp.Pomdp;

public abstract class AlphaIteration extends OfflineIteration {

	protected BeliefValueFunction current;
	protected BeliefValueFunction old;
	protected Pomdp pomdp;
	boolean finite;
	protected BeliefValueFunction[] memory;
	
	protected void startInit(Pomdp pomdp,int horizon){
		super.startInit(horizon);
		this.pomdp=pomdp;
	}
	
	protected void endInit() {
			if (horizon<1){
				finite=false;
				if (!pomdp.isRewardStationary())
					Utils.error("Infinite VI not compatible with non-stationary rewards");
				if (!pomdp.isTransitionStationary())
					Utils.error("Infinite VI not compatible with non-stationary transitions");
			}
			else{
				finite=true;
				memory=new BeliefValueFunction[horizon];
			}
			iterationStats = new AlphaVectorStats();
			super.endInit();
	    }

	    public Pomdp getPomdp() {
	    	return pomdp;
	    }

	    protected void endIteration() {
	    	if (current != null) {
	    		((AlphaVectorStats) iterationStats).iteration_vector_count
			    	.add(new Integer(current.size()));
	    		if (finite){
	    			memory[horizon - count() - 1]=current.copy();
	    		}
			}
	    	if (verbose){
	    		System.out.println("Iteration "+count());
	    		//System.out.println(current);
	    	}
	    	super.endIteration();
	    }

	    public BeliefValueFunction getValueFunction() {
	    	return current;
	    }

	    public BeliefValueFunction getOldValueFunction() {
	    	return old;
	    }

		protected void reset() {
			((AlphaVectorStats) iterationStats).iteration_vector_count.clear();
			super.reset();
			old = null;
		}

		
		public BeliefValueFunction getValueFunction(int i) {
			if (finite)
				return memory[i];
			else
				return current;
		}
		
		public String toString(){
			String retval=super.toString();
			for (int i=0;i< count();i++){
				retval+="vc("+i+")\t"+((AlphaVectorStats) iterationStats).iteration_vector_count.get(i)+"\n";
			}
			return retval;
		}

		public boolean isFinite() {
			return finite;
		}

		public int getHorizon() {
			return horizon;
		}
}

