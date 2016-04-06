package libsdm.test;

import libsdm.common.DenseVector;
import libsdm.pomdp.Pomdp;
import libsdm.pomdp.PomdpSimulator;
import libsdm.pomdp.parser.FileParser;
import libsdm.pomdp.solve.online.PomdpOnlinePlanning;

public class OnlineTest {
	 public static void main(String[] args) throws Exception {
		 //Pomdp pomdp = FileParser.loadPomdp("experiments/data/POMDP-files/hallway/hallway2.POMDP", FileParser.PARSE_CASSANDRA_POMDP,false);
		 //Pomdp pomdp = FileParser.loadPomdp("experiments/data/POMDP-files/tiger/tiger.95.POMDP", FileParser.PARSE_CASSANDRA_POMDP,false);
		 
		 Pomdp pomdp = FileParser.loadPomdp("experiments/data/POMDP-files/rock/RockSample_7_8.POMDP", FileParser.PARSE_CASSANDRA_POMDP,true);
		 //Pomdp pomdp = FileParser.loadPomdp("experiments/data/POMDP-files/tagAvoid.POMDP", FileParser.PARSE_CASSANDRA_POMDP,false);

		 System.out.println("Init...");

		 PomdpOnlinePlanning online = new PomdpOnlinePlanning(pomdp, 1000, 0,true);
		 System.out.println("Simulating...");
		 online.setVerbose(true);
		 PomdpSimulator sim = new PomdpSimulator(pomdp);
		 sim.setVerbose(true);
		 DenseVector results = sim.simulate(online,1000,null);
		 System.out.println(results);
		 
	 }
}	

