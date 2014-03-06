package libsdm.pomdp.parser;

import libsdm.mdp.TransitionMatrix;
import libsdm.pomdp.ObservationMatrix;
import libsdm.pomdp.Pomdp;
import libsdm.pomdp.PomdpReward;
import libsdm.pomdp.plain.DensePomdp;
import libsdm.pomdp.plain.SparsePomdp;


public class FileParser {

    public static final int PARSE_CASSANDRA_POMDP = 0;
    public static final int PARSE_SPUDD = 1;

    public static Pomdp loadPomdp(String filename, int filetype,boolean sparseAlpha)
	    throws Exception {
	Pomdp newPomdp;
	switch (filetype) {
	case PARSE_CASSANDRA_POMDP:
	    PomdpSpecSparse data = DotPomdpParserSparse.parse(filename);
	    String actStr[] = null;
	    if (data.actList != null)
	    	actStr = (String[]) data.actList.toArray(new String[data.actList.size()]);
	    String obsStr[] = null;
	    if (data.obsList != null)
	    	obsStr = (String[]) data.obsList.toArray(new String[data.obsList.size()]);
	    String staStr[] = null;
	    if (data.staList != null)
	    	staStr = (String[]) data.staList.toArray(new String[data.staList.size()]);
	    if (sparseAlpha)
	    	newPomdp = new SparsePomdp(new ObservationMatrix(data.O),new TransitionMatrix(data.T), new PomdpReward(data.R), data.nrSta, data.nrAct, data.nrObs, data.discount, staStr, actStr, obsStr, data.startState);
	    else
	    	newPomdp = new DensePomdp(new ObservationMatrix(data.O),new TransitionMatrix(data.T), new PomdpReward(data.R), data.nrSta, data.nrAct, data.nrObs, data.discount, staStr, actStr, obsStr, data.startState);
	    break;
	case PARSE_SPUDD:
	    //newPomdp = new FactorPomdp(filename);
		newPomdp=null;
	    break;
	default:
	    throw new Exception("No such filetype (Not Implemented Yet)\n");
	}

	return newPomdp;

    }

    /*
    public static BeliefMdp loadBeliefMdp(String filename, int filetype)
	    throws Exception {
	if (filetype == PARSE_SPUDD)
	    throw new Exception(
		    "Cannot create a belief over a ADD representation (Not Implemented Yet)\n");
	Pomdp pomdp = loadPomdp(filename, filetype);
	BeliefMdp bPomdp;
	bPomdp = new SparseBeliefMdp((SparseMomdp) pomdp);
	return bPomdp;
    }
    */
}

