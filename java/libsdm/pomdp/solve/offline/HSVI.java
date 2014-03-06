package libsdm.pomdp.solve.offline;

import java.util.ArrayList;

import libsdm.common.IterationStats;
import libsdm.common.DenseVector;
import libsdm.common.SparseVector;
import libsdm.common.Utils;
import libsdm.common.ValueSelector;
import libsdm.pomdp.AlphaVector;
import libsdm.pomdp.BeliefState;
import libsdm.pomdp.Pomdp;

public class HSVI extends PbAlgorithm {

	private double epsilon;

	private DenseVector border;
	static double delta = 0.00001;
	
	
	private ArrayList<Double> valueArray;


	public HSVI(Pomdp pomdp, double epsilon) {
		startInit(pomdp,0);
		this.epsilon = epsilon;
		delta=epsilon;
		current = pomdp.getEmptyValueFunction();
		
		AlphaVector init_lb = getLowestAlpha(0);
		//current.push(init_lb);
		for (int a = 0; a< pomdp.actions();a++){
			current.push(same_action_heuristic(init_lb,a));
		}
		bset=null;
		initialBorders();
		endInit();
	}

	private AlphaVector same_action_heuristic(AlphaVector initLb, int a) {
		AlphaVector old=null;
		AlphaVector retval=initLb;
		boolean iter = true;
		while (iter){
			old=retval;
			retval =  pomdp.blindProjection(old, a,0);
			//System.out.println(retval);
			if (old.gap(retval) < epsilon)
				iter=false;
		}
		return retval;
	}

	public void initialBorders() {
		
		FastInformedBound fib = new FastInformedBound(pomdp, epsilon);
		AlphaConvergence conv = new AlphaConvergence(epsilon);
		conv.setSort(false);
		fib.addStopCriterion(conv);
		fib.setVerbose(false);
		fib.run();
		
		border=new DenseVector(pomdp.states());
		for (int s = 0; s < pomdp.states(); s++) {
			//border.set(s,val.getValue(s));
			double mmax=Double.NEGATIVE_INFINITY;
			for (int a = 0; a < pomdp.actions(); a++){
				AlphaVector mv=fib.getValueFunction().getAlpha(a);
				double val=mv.get(s);
				if (val > mmax)
					mmax=val;
			}
			border.set(s,mmax);
			//border.set(s, pomdp.getRewardFunction().max()/(1-pomdp.gamma()));
			//System.out.println("Border "+s+" = " + mmax);
		}
		beliefArray = new ArrayList<BeliefState>();
		valueArray = new ArrayList<Double>();
		
	}

	@Override
	public IterationStats iterate() {
		startIteration();
		old = current;

		BoundUncertaintyExplore(pomdp.getInitialBeliefState(), 0);
		if (verbose){
			System.out.println("[DONE]");
		}
		current.prune(delta);
		//current.dominationCheck(delta);
		UbPrune();
		if (verbose){
			System.out.println("UB PS = "+beliefArray.size());
			System.out.println("LB VS = "+current.size());
		}
		endIteration();
		
		return iterationStats;
	}

	private void UbPrune() {
		//double delta=0.00001;
		ArrayList<BeliefState> newbeliefArray = new ArrayList<BeliefState>();
		ArrayList<Double> newvalueArray = new ArrayList<Double>();
		for (int i=0;i<beliefArray.size();i++){
			BeliefState b = beliefArray.get(i);
			double tvalue=valueArray.get(i); 
			boolean dominated = false;
			double x0=b.getPoint().dot(border);
			for (int j=0;j<newbeliefArray.size();j++){
				BeliefState bp = newbeliefArray.get(j);
				double val = newvalueArray.get(j);
				int[] mi = bp.getMask();
				double minRatio=Double.POSITIVE_INFINITY;
				for (int k=0;k<mi.length;k++){
					int index=mi[k];
					double ratio=b.getPoint().get(index)/bp.getPoint().get(index);
					if (ratio < minRatio)
						minRatio=ratio;
				}
				double xi=x0 + minRatio*(val - bp.getPoint().dot(border));
				if (xi <= tvalue + delta){
					//if (verbose){
						//System.out.println("belief "+i+" dominated by newbelief "+j);
						//System.out.println("tvalue = "+tvalue);
						//System.out.println("xi = "+xi);
					//}
					dominated=true;
					break;
				}
			}
			if (!dominated){
				newbeliefArray.add(b);
				newvalueArray.add(tvalue);
			}
		}
		beliefArray=newbeliefArray;
		valueArray=newvalueArray;	
	}
	
	private void BoundUncertaintyExplore(BeliefState b, int i) {
		if (UbEval(b) - current.value(b) < epsilon/(Math.pow(pomdp.gamma(),i))) {
			if (verbose) System.out.println();
			return;
		}
		if (verbose){
			//System.out.print(".");
			System.out.flush();
		}
		int a=UbAmax(b);
		int o=UbGapmax(b,a,i);
		System.out.print("a"+a+"-o"+o+" ");
		BoundUncertaintyExplore(pomdp.nextBeliefState(b, a, o, 0),i+1);
		AlphaVector alpha = hsviBackup(b, current);
		//System.out.println("Alpha="+alpha);
		//if (b.getCorner()!=-1){
			//System.out.println("Bel="+b);
			//System.out.println("Old Alpha="+current.getBestAlpha(b));
			//System.out.println("New Alpha="+alpha);
			//System.out.println("Old Up="+UbEval(b));
		//}
		UbUpdate(b);
		//if (b.getCorner()!=-1){
		//	System.out.println("New Up="+UbEval(b));
		//}	
		if (alpha!=null){
			current.push(alpha);
		//	System.out.println("alpha*b ="+alpha.eval(b));
		}
			if (verbose){
			//System.out.print("\b");
			System.out.print("*");
			System.out.flush();
		}

		//else{
		//	System.out.println("alpha*b = NULL");
		//}

		
		//UbPrune();
		// Upper bound Update...
	}

	private double UbEval(BeliefState b) {
		double x0 = b.getPoint().dot(border);
		double mval=Double.POSITIVE_INFINITY;
		boolean silent=true;
		if (!silent){
			System.out.println("***EVAL***");
			System.out.println("b = "+b);
		}
		//int corner=b.getCorner();
		//if (corner!=-1){
		//	return border.get(corner);
		//}
		if (beliefArray.size()==0)
			return x0;
		for (int i=0;i<beliefArray.size();i++){
			BeliefState bp = beliefArray.get(i);
			double val = valueArray.get(i);
			//int []mi = bp.getMask();
			if (!silent){
				System.out.println("bp = "+b.toString());
				//System.out.println("mi = "+mi.toString());
				//System.out.println("mi.size = "+mi.size());
				System.out.println("val = "+val);
			}
			double minRatio=b.minratio(bp);
			
			/*double minRatio=Double.POSITIVE_INFINITY;
			for (int j=0;j<mi.length;j++){
				int index=mi[j];
				double ratio=b.prob(index)/bp.prob(index);
				if (!silent){
					System.out.println("j="+j);
					System.out.println("index="+index);
					System.out.println("ratio="+ratio);
				}
				if (ratio < minRatio)
					minRatio=ratio;
			}
			*/
			double xi=x0 + minRatio*(val - bp.getPoint().dot(border));
			if (!silent){
				System.out.println("-> Minratio="+minRatio);
				System.out.println("-> Xi="+xi);
			}
			//System.out.println("xi="+xi);
			if (xi < mval)
				mval=xi;
		}
		//System.out.println("mval="+mval);
		if (!silent)
			System.out.println("*** UBEVAL(b) "+mval);
		return mval;
	}

	
	private void UbUpdate(BeliefState b) {
		//System.out.println("\nb="+b);
		double maxev=Double.NEGATIVE_INFINITY;
		//System.out.println(b);
		for (int a=0;a<pomdp.actions();a++){	
			//System.out.println();
			SparseVector obsprob = pomdp.observationProbabilities(b, a, 0);
			double [] obsArr=obsprob.getArray();
			//AlphaVector alpha = pomdp.getRewardAlpha(a, b, 0);
			//System.out.println("Alpha q"+alpha);
			double val=pomdp.getRewardAlpha(a, b, 0).eval(b);
			//System.out.println("Ua="+a+" reward="+val);
			//System.out.println("a"+a+ " = "+val);
			for (int o=0;o<pomdp.observations();o++){
				BeliefState bp = pomdp.nextBeliefState(b, a, o, 0);
				
				if (bp!=null){
					//SparseVector vect = pao.getTau(a,o,0).mult((SparseBeliefState)b);
					//double contri=UbEval(bp);
					//System.out.println("o ="+o+" val="+contri);
					//System.out.println(" -> bp="+bp);
					val+=pomdp.gamma()*obsArr[o]*UbEval(bp);
					//val+=pomdp.gamma()*UbEval(bp);
					//System.out.println(" -> "+pomdp.gamma()+"*"+obsArr[o]+"*"+UbEval(bp));
				}
			}
			//System.out.println(" = "+val);
			if (val > maxev)
				maxev=val;
			//System.out.println("obs "+obsprob);
			//System.out.println("Obs Array = "+obsArr[0]+obsArr[1]+obsArr[2]+obsArr[3]);
		}
		//System.out.println("b="+b);
		//System.out.println("maxev "+maxev);
		int corner=b.getCorner();
		if (corner!=-1 ){
			/*if (border.get(corner) < maxev){
				System.out.println("Corner Update with no Improvement");
				System.out.println("DEBUG: b="+b);
				for (int a=0;a<pomdp.actions();a++){	
					System.out.println("DEBUG: a="+a);
					SparseVector obsprob = pomdp.observationProbabilities(b, a, 0);
					double [] obsArr=obsprob.getArray();
					double val=pomdp.getRewardAlpha(a, b, 0).eval(b);
					System.out.println("DEBUG: val_0 = "+val);
					for (int o=0;o<pomdp.observations();o++){
						BeliefState bp = pomdp.nextBeliefState(b, a, o, 0);		
						if (bp!=null){
							double contri=UbEval(bp);
							System.out.println("DEBUG: o ="+o+" val="+contri);
							System.out.println("DEBUG: -> bp="+bp);
							val+=pomdp.gamma()*obsArr[o]*UbEval(bp);
							System.out.println("DEBUG: -> "+pomdp.gamma()+"*"+obsArr[o]+"*"+UbEval(bp));
						}
					}
					System.out.println("DEBUG: = "+val);
					if (val > maxev)
						maxev=val;
					//System.out.println("obs "+obsprob);
					//System.out.println("Obs Array = "+obsArr[0]+obsArr[1]+obsArr[2]+obsArr[3]);
				}
				System.out.println("Corner "+corner+" updated from "+border.get(corner)+" --> "+maxev);
			}
			*/
			//if (border.get(corner) > maxev)
				border.set(corner, maxev);
			}
		else{
			//System.out.println("Normal Update");
			//if (!domCheck(b,maxev)){
				beliefArray.add(b);
				valueArray.add(maxev);
			//}
		}
		//if (verbose){
			//System.out.println("Added "+maxev);
			//System.out.println("of Belief "+b.toString());
		//}
		
	}

	

	private int UbGapmax(BeliefState b, int a,int i) {
		ValueSelector vs = new ValueSelector(ValueSelector.MAX);
		SparseVector obsprob = pomdp.observationProbabilities(b, a, 0);
		double [] obsArr=obsprob.getArray();
		for (int o=0;o<pomdp.observations();o++){
			BeliefState bp = pomdp.nextBeliefState(b, a, o, 0);
			if (bp!=null){
				if (bp.getMask()==null){
	    			Utils.warning("GAPMAX: Belief with complete mask Uhh?");
	    			//System.out.println(bp);
				}
				//try{
					double val = obsArr[o]*(UbEval(bp) - current.value(bp) - epsilon/(Math.pow(pomdp.gamma(),i+1)));
					//System.out.println("o="+o+" val="+val);
					//System.out.println("dec = "+obsArr[o]+"("+UbEval(bp)+" - "+current.value(bp)+" - "+epsilon/(Math.pow(pomdp.gamma(),i))+")");
					vs.put(new Integer(o), val);
					//} 
				//catch (java.lang.NullPointerException e){
				//	System.out.println(current.size());
				//	Utils.error("Damit");
				//	}
				}
		}
		return ((Integer)vs.getRandom());
	}

	private int UbAmax(BeliefState b) {
		ValueSelector vs = new ValueSelector(ValueSelector.MAX);
		
		for (int a=0;a<pomdp.actions();a++){
			SparseVector obsprob = pomdp.observationProbabilities(b, a, 0);
			double [] obsArr=obsprob.getArray();
			//AlphaVector obsprob = pomdp.observationProbabilities(b, a, 0);
			double val=pomdp.getRewardAlpha(a, b, 0).eval(b);
			for (int o=0;o<pomdp.observations();o++){
				BeliefState bp = pomdp.nextBeliefState(b, a, o, 0);
				if (bp!=null){
					//SparseVector vect = pao.getTau(a,o,0).mult((SparseBeliefState)b);
					//val+=pomdp.gamma()*vect.norm(1)*UbEval(bp,beliefArray,valueArray);
					val+=pomdp.gamma()*obsArr[o]*UbEval(bp);
				}
			}
			//System.out.println("a="+a+" value="+val);
			vs.put(a, val);
		}
		//System.out.println("Amax ="+vs.getBest());
		return ((Integer)vs.getRandom());
	}

	@Override
	public double performance() {
		BeliefState b = pomdp.getInitialBeliefState();
		double ub=UbEval(b);
		double lb=current.value(b);
		if (verbose)
			System.out.println("BOUNDS = [ "+ub+" , "+lb+" ]");
		return ub-lb;
	}


	@Override
	public void reset() {
		current = pomdp.getEmptyValueFunction();
		current.push(getLowestAlpha(horizon));
		bset = null;
		old=null;
		initialBorders();
		super.reset();
	}

}
