package libsdm.test;

import libsdm.common.MaxTimeCriterion;
import libsdm.pomdp.Pomdp;
import libsdm.pomdp.parser.FileParser;
import libsdm.pomdp.solve.offline.AlphaVectorStats;
import libsdm.pomdp.solve.offline.HSVI;
import libsdm.pomdp.solve.offline.PbConvergence;

public class HsviTest {
	 public static void main(String[] args) throws Exception {
		 Pomdp pomdp = FileParser.loadPomdp("experiments/data/POMDP-files/hallway/hallway2.POMDP", FileParser.PARSE_CASSANDRA_POMDP,false);
		 //Pomdp pomdp = FileParser.loadPomdp("experiments/data/POMDP-files/rocksample/RockSample_5_5.POMDP", FileParser.PARSE_CASSANDRA_POMDP);
		 //Pomdp pomdp = FileParser.loadPomdp("experiments/data/POMDP-files/tag/tagAvoid.POMDP", FileParser.PARSE_CASSANDRA_POMDP);
		 //Pomdp pomdp = FileParser.loadPomdp("experiments/data/POMDP-files/tiger/tiger.95.POMDP", FileParser.PARSE_CASSANDRA_POMDP);
		 //Pomdp pomdp = FileParser.loadPomdp("experiments/data/POMDP-files/tiger/tiger-grid.POMDP", FileParser.PARSE_CASSANDRA_POMDP,false);
		 //pomdp.setGamma(0.9);
		 int maxtime=1200000; //ms
		 double epsi = 0.01;
		 
		 HSVI hsvi = new HSVI(pomdp, epsi);
		 hsvi.addStopCriterion(new MaxTimeCriterion(maxtime));
		 hsvi.addStopCriterion(new PbConvergence(epsi));
		 System.out.println("=== Test 5: Infinite horizon HSVI (Hallway2,e="+epsi+")");
		 hsvi.setVerbose(true);
		 hsvi.run();
		 System.out.println(((AlphaVectorStats)hsvi.getStats()).toString());
		 System.out.println(hsvi.getValueFunction().toString());
		 System.out.println("===\n");
		 
	 }
}	

