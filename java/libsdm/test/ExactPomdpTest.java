package libsdm.test;

import libsdm.pomdp.Pomdp;
import libsdm.pomdp.parser.FileParser;
import libsdm.pomdp.solve.offline.AlphaIteration;
import libsdm.pomdp.solve.offline.AlphaConvergence;
import libsdm.pomdp.solve.offline.BatchEnumeration;
import libsdm.pomdp.solve.offline.BlindPolicy;
import libsdm.pomdp.solve.offline.IncrementalPruning;
import libsdm.pomdp.solve.offline.QmdpBound;

public class ExactPomdpTest {
	 public static void main(String[] args) throws Exception {
		 
		 Pomdp pomdp = FileParser.loadPomdp("experiments/data/POMDP-files/tiger/tiger.95.POMDP", FileParser.PARSE_CASSANDRA_POMDP,false);
		 
		 // Test 1
		 pomdp.setGamma(1.0);
		 //Test 1
		 double delta=0.001;
		 int h=10;
		 AlphaIteration iter = new BatchEnumeration(pomdp,delta, h);
		 iter.setVerbose(false);
		 
		 System.out.println("=== Test 1: Finite Horizon Batch Enumeration (Tiger,h="+h+",g=1.0)");
		 iter.run();
		 System.out.println(iter.getStats().toString());
		 for (int i=0;i<h;i++){
			 System.out.println("VF "+i);
			 System.out.println(iter.getValueFunction(i).toString());
		 }
		 System.out.println("===\n");
		
		//Test 2
		 iter = new IncrementalPruning(pomdp,delta, h);
		 iter.setVerbose(false);
		 
		 System.out.println("=== Test 2: Finite Horizon Incremental Pruning (Tiger,h="+h+",g=1.0)");
		 iter.run();
		 System.out.println(iter.getStats().toString());
		 for (int i=0;i<h;i++){
			 System.out.println("VF "+i);
			 System.out.println(iter.getValueFunction(i).toString());
		 }
		 //System.out.println("VF END");
		 //System.out.println(iter.getValueFunction().toString());
		 System.out.println("===\n");
		 
		//Test 3
		 double epsilon=0.01;
		 pomdp.setGamma(0.75);
		 iter = new IncrementalPruning(pomdp,delta, 0);
		 iter.addStopCriterion(new AlphaConvergence(epsilon));
		 
		 System.out.println("=== Test 3: Infinite Horizon Incremental Pruning (Tiger,e="+epsilon+",g=0.75)");
		 iter.setVerbose(true);
		 iter.run();
		 System.out.println(iter.getStats().toString());
		 System.out.println("VF END");
		 System.out.println(iter.getValueFunction().toString());
		 System.out.println("===\n");
		 
		//Test 4
		 iter = new BlindPolicy(pomdp, 0);
		 AlphaConvergence conv = new AlphaConvergence(epsilon);
		 conv.setSort(false);
		 iter.addStopCriterion(conv);
		 
		 System.out.println("=== Test 4: (Lower Bound) Infinite Horizon Blind Policy (Tiger,e="+epsilon+",g=0.75)");
		 iter.setVerbose(false);
		 iter.run();
		 System.out.println(iter.getStats().toString());
		 System.out.println("VF END");
		 System.out.println(iter.getValueFunction().toString());
		 System.out.println("===\n");
		 
		 //Test 5
		 QmdpBound qmdp = new QmdpBound(pomdp,epsilon);
		 
		 System.out.println("=== Test 5: (Upper Bound) Infinite Horizon Qmdp Policy (Tiger,e="+epsilon+",g=0.75)");
		 qmdp.setVerbose(false);
		 qmdp.run();
		 System.out.println(qmdp.getStats().toString());
		 System.out.println("VF END");
		 System.out.println(qmdp.getValueFunction().toString());
		 System.out.println("===\n");
	
		 
	 }
}	

