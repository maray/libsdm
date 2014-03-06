package libsdm.test;

import libsdm.common.MaxTimeCriterion;
import libsdm.pomdp.Pomdp;
import libsdm.pomdp.parser.FileParser;
import libsdm.pomdp.solve.offline.AlphaVectorStats;
import libsdm.pomdp.solve.offline.HSVI;
import libsdm.pomdp.solve.offline.PBVI;
import libsdm.pomdp.solve.offline.PbAlgorithm;
import libsdm.pomdp.solve.offline.PbConvergence;
import libsdm.pomdp.solve.offline.Perseus;

public class PointBasedTest {
	 public static void main(String[] args) throws Exception {
		 boolean showVF=false;
		 //Pomdp pomdp = FileParser.loadPomdp("experiments/data/POMDP-files/hallway/hallway.POMDP", FileParser.PARSE_CASSANDRA_POMDP);
		 Pomdp pomdp = FileParser.loadPomdp("experiments/data/POMDP-files/hallway/hallway2.POMDP", FileParser.PARSE_CASSANDRA_POMDP,false);
		 //Pomdp pomdp = FileParser.loadPomdp("experiments/data/POMDP-files/rocksample/RockSample_7_8.pomdp", FileParser.PARSE_CASSANDRA_POMDP);
		 //Pomdp pomdp = FileParser.loadPomdp("experiments/data/POMDP-files/tiger/tiger.95.POMDP", FileParser.PARSE_CASSANDRA_POMDP);
		 //Pomdp pomdp = FileParser.loadPomdp("experiments/data/POMDP-files/coffee/coffee.90.POMDP", FileParser.PARSE_CASSANDRA_POMDP);
		 //Pomdp pomdp = FileParser.loadPomdp("experiments/data/POMDP-files/tag/tagAvoid.POMDP", FileParser.PARSE_CASSANDRA_POMDP);
			
		 
		 int bsize=1000;
		 int maxtime=120000; //ms
		 double epsi = 0.01;
		 
		 Perseus perseus= new Perseus(pomdp, bsize, 0);
		 perseus.addStopCriterion(new PbConvergence(epsi));
		 perseus.addStopCriterion(new MaxTimeCriterion(maxtime));
		 
		
		 
		 System.out.println("=== Test 1: Infinite horizon PERSEUS (random) (Hallway2,e="+epsi+",|B|="+bsize+")");
		 perseus.setVerbose(true);
		 perseus.run();
		 System.out.println(((AlphaVectorStats)perseus.getStats()).toString());
		 if (showVF){
		 System.out.println(perseus.getValueFunction().toString());
		 System.out.println("===\n");
		 }
		 
		 perseus.setCollect(PbAlgorithm.EXPAND_EXPLORATORY);
		 perseus.reset();
		 
		 System.out.println("=== Test 2: Infinite horizon PERSEUS (l1) (Hallway2,e="+epsi+",|B|="+bsize+")");
		 perseus.run();
		 System.out.println(((AlphaVectorStats)perseus.getStats()).toString());
		 if (showVF){
			 System.out.println(perseus.getValueFunction().toString());
			 System.out.println("===\n");
			 }
			 
		 PBVI pbvi = new PBVI(pomdp, 10, 100, 0);
		 pbvi.addStopCriterion(new PbConvergence(epsi));
		 pbvi.addStopCriterion(new MaxTimeCriterion(maxtime));
		 
		 System.out.println("=== Test 3: Infinite horizon PBVI (Hallway2,e="+epsi+")");
		 pbvi.setVerbose(true);
		 pbvi.run();
		 System.out.println(((AlphaVectorStats)pbvi.getStats()).toString());
		 if (showVF){
			 System.out.println(pbvi.getValueFunction().toString());
			 System.out.println("===\n");
			 }
			 
		 int h=20;
		 pbvi = new PBVI(pomdp, 1, 100, h);
		 pbvi.setCollect(PbAlgorithm.EXPAND_EXPLORATORY);
		 pbvi.addStopCriterion(new MaxTimeCriterion(maxtime));
		 
		 System.out.println("=== Test 4: Finite horizon PBVI (exploratory) (Hallway2,h="+h+")");
		 pbvi.setVerbose(true);
		 pbvi.run();
		 System.out.println(((AlphaVectorStats)pbvi.getStats()).toString());
		 if (showVF){
			 System.out.println(pbvi.getValueFunction().toString());
			 System.out.println("===\n");
			 }
			
		 
		 
		// HSVI hsvi = new HSVI(pomdp, epsi);
		// hsvi.addStopCriteria(new MaxTimeCriterion(maxtime));
		 
		 //System.out.println("=== Test 5: Infinite horizon HSVI (exploratory) (Hallway2,e="+epsi+")");
		 //hsvi.setVerbose(true);
		 //hsvi.run();
		 //System.out.println(((AlphaVectorStats)hsvi.getStats()).toString());
		 
	 }
}	

