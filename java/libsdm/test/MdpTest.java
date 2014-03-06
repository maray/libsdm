package libsdm.test;

import libsdm.mdp.Mdp;
import libsdm.mdp.ValueConvergence;
import libsdm.mdp.ValueIteration;

public class MdpTest {
	 public static void main(String[] args) throws Exception {
		 
		 // Test 1
		 Mdp model=ChainProblem.getMdp(5, ChainProblem.NORMAL, 1.0, 0.8);
		 int h=10;
		 
		 System.out.println("=== Test 1: Finite Horizon VI (Chain,h="+h+",g=1.0)");
		 ValueIteration iter=new ValueIteration(model, null, h, false);
		 iter.setVerbose(false);
		 iter.run();
		 System.out.println(iter.getStats().toString());
		 for (int i=0;i<h;i++){
			 System.out.println("QF "+i);
			 System.out.println(iter.getValueFunction(i).toString());
		 }
		 System.out.println("QF END");
		 System.out.println(iter.getValueFunction().toString());
		 System.out.println("===\n");
		 
	
		 
		// Test 2
		 model.setGamma(0.95);
		 double epsilon=0.01;
		 System.out.println("=== Test 2: Infinite Horizon Synchronic VI (Chain,g=0.95,e=0.01)");
		 iter=new ValueIteration(model, null, 0, false);
		 iter.addStopCriterion(new ValueConvergence(epsilon, ValueConvergence.MAXDIST));
		 iter.setVerbose(false);
		 iter.run();
		 System.out.println(iter.getStats().toString());
		 System.out.println("QF END");
		 System.out.println(iter.getValueFunction().toString());
		 System.out.println("===\n");

		 // Test 3
		 System.out.println("=== Test 3: Infinite Horizon Asynchronic VI (Chain,g=0.95,e=0.01)");
		 iter=new ValueIteration(model, null, 0, true);
		 iter.addStopCriterion(new ValueConvergence(epsilon, ValueConvergence.MAXDIST));
		 iter.setVerbose(false);
		 iter.run();
		 System.out.println(iter.getStats().toString());
		 System.out.println("QF END");
		 System.out.println(iter.getValueFunction().toString());
		 System.out.println("===\n");	 
	 }
}	

