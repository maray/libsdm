package libsdm.pomdp.plain;

import java.util.ArrayList;
import java.util.Arrays;

import libsdm.common.SparseVector;
import libsdm.common.Utils;
import libsdm.pomdp.AlphaVector;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;

import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import org.gnu.glpk.SWIGTYPE_p_double;
import org.gnu.glpk.SWIGTYPE_p_int;
import org.gnu.glpk.glp_prob;
import org.gnu.glpk.glp_smcp;

public class LpSolver {
	
	public final static int PROG_MATLAB = 0;
	public final static int PROG_GLPK_JAVA = 1;
	public final static int PROG_GLPK_CMD = 2;
	private static int program=PROG_GLPK_JAVA;
    static long totalLpTime=0;
	
    
    static public SparseBeliefState solve(AlphaVector selVect, ArrayList<AlphaVector> newv, double delta){
    	switch(program){
    	case PROG_MATLAB:
    		try {
				return(solve_MATLAB(selVect,newv,delta));
			} catch (Exception e) {
				
				e.printStackTrace();
				Utils.error("Yeps... something wrong");
			}
    	case PROG_GLPK_CMD:
    		return(solve_GLPK_CMD(selVect,newv,delta));
    	default: // PROG_GLPK_JAVA
    		return(solve_GLPK_JAVA(selVect,newv,delta));
    	}
    }
   
	private static SparseBeliefState solve_MATLAB(AlphaVector selVect,
			ArrayList<AlphaVector> newv, double delta) throws Exception {
		SparseBeliefState bel = null;
		
	    //Create a proxy, which we will use to control MATLAB
	    
		MatlabProxyFactory factory = new MatlabProxyFactory();
	    MatlabProxy proxy = factory.getProxy();

	    //Create and print a 2D double array
	    double[][] A = new double[newv.size()][];
	   // double [] v;
	    //System.out.println("Original: ");
	    for(int i = 0; i < A.length; i++)
	    {
	    	
	    	AlphaVector vec = newv.get(i).copy();
	    	vec.diff(selVect);
	    	//System.out.println(vec);
	        A[i]=vec.getArray();
	    }
	        
	    //Send the array to MATLAB, transpose it, then retrieve it and convert it to a 2D double array
	    MatlabTypeConverter processor = new MatlabTypeConverter(proxy);
	    processor.setNumericArray("A", new MatlabNumericArray(A, null));
	    
	    proxy.eval("A(:,end+1) = ones(size(A,1),1);");
	    proxy.eval("b=zeros(1,size(A,1));");
	    proxy.eval("f = zeros(1,size(A,2));f(1,end)=-1; ");
	    proxy.eval("Aeq= ones(1,size(A,2));Aeq(1,end)=0;");
	    proxy.eval("beq=1; ");
	    proxy.eval("lb=zeros(1,size(A,2));lb(1,end)=-Inf; ");
	    proxy.eval("ub=ones(1,size(A,2));ub(1,end)=Inf; ");
	    proxy.eval("options = optimset('Display','off');");
	    proxy.eval("[x,fval,exitflag]=linprog(f,A,b,Aeq,beq,lb,ub,0,options);");
	    double result = ((double[]) proxy.getVariable("exitflag"))[0];
	    double fval = -((double[]) proxy.getVariable("fval"))[0];
	    //System.out.println("f="+fval);
	    if (result==1.0 && fval >= delta){
	    	double[][] Array = processor.getNumericArray("x").getRealArray2D();
	    	//bel=new BeliefState(selVect.size());
	    	int idx=0;
	    	double[] data=new double[selVect.size()];
	    	int[] index=new int[selVect.size()];
	    	for (int i=0;i<selVect.size();i++){
	    		if (Array[i][0]!=0){
					data[idx]=Array[i][0];
	    			index[idx]=i;
	    			idx++;
	    		}
	    	}
	    	bel=SparseBeliefState.transform(new SparseVector(index,data,idx)); 
	    	//System.out.println(bel);
	    }
	    
	    //double[][] transposedArray = processor.getNumericArray("array").getRealArray2D();
	        
	     //Print the returned array, now transposed
	     //System.out.println("Transposed: ");
	     //for(int i = 0; i < transposedArray.length; i++)
	     //{
	      //   System.out.println(Arrays.toString(transposedArray[i]));
	     //}
	    //Disconnect the proxy from MATLAB
	    proxy.disconnect();
	    //Utils.error("False stop");
		return bel;
	}


	private static SparseBeliefState solve_GLPK_CMD(AlphaVector selVect,
			ArrayList<AlphaVector> newv, double delta) {
		Utils.error("Command-line GLPK not implemented yet.");
		return null;
	}


	public static int getProgram() {
		return program;
	}

	public static void setProgram(int program) {
		LpSolver.program = program;
	}

	public static long getTotalLpTime() {
		return totalLpTime;
	}

	public static void resetTotalLpTime() {
		LpSolver.totalLpTime = 0;
	}


	static private SparseBeliefState solve_GLPK_JAVA(AlphaVector selVect,
		    ArrayList<AlphaVector> newv, double delta){
		//int time_limit=1000;
		SparseBeliefState bel = null;
		int states=selVect.size();
		glp_prob lp;
		glp_smcp parm;
		SWIGTYPE_p_int ind;
		SWIGTYPE_p_double val;
		lp = GLPK.glp_create_prob();
		GLPK.glp_set_prob_name(lp, "FindRegion");
		// Define Solution Vector
		GLPK.glp_add_cols(lp, states + 1);
		for (int i = 0; i < states; i++) {
		    GLPK.glp_set_col_kind(lp, i + 1, GLPKConstants.GLP_CV);
		    GLPK.glp_set_col_bnds(lp, i + 1, GLPKConstants.GLP_DB, 0.0, 1.0);
		}
		GLPK.glp_set_col_kind(lp, states + 1, GLPKConstants.GLP_CV);
		GLPK.glp_set_col_bnds(lp, states + 1, GLPKConstants.GLP_FR,
			Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		// Define Constraints
		GLPK.glp_add_rows(lp, newv.size() + 1);
		ind = GLPK.new_intArray(states + 2);
		for (int j = 0; j < states + 2; j++) {
		    GLPK.intArray_setitem(ind, j + 1, j + 1);
		}
		val = GLPK.new_doubleArray(states + 2);
		for (int i = 0; i < newv.size(); i++) {
		    GLPK.glp_set_row_bnds(lp, i + 1, GLPKConstants.GLP_LO, 0.0,
			    Double.POSITIVE_INFINITY);
		    AlphaVector testVect = newv.get(i);
		    for (int j = 0; j < states; j++) {
			GLPK.doubleArray_setitem(val, j + 1, selVect.get(j) - testVect.get(j));
		    }
		    GLPK.doubleArray_setitem(val, states + 1, -1.0);
		    GLPK.glp_set_mat_row(lp, i + 1, states + 1, ind, val);
		}
		// ind=GLPK.new_intArray(states+2);
		// for (int j=0;j<states+2;j++){
		// GLPK.intArray_setitem(ind,j+1,j+1);
		// }
		// val=GLPK.new_doubleArray(states+2);
		GLPK.glp_set_row_bnds(lp, newv.size() + 1, GLPKConstants.GLP_FX, 1.0,
			1.0);
		for (int j = 0; j < states; j++) {
		    GLPK.doubleArray_setitem(val, j + 1, 1.0);
		}
		GLPK.doubleArray_setitem(val, states + 1, 0.0);
		GLPK.glp_set_mat_row(lp, newv.size() + 1, states + 1, ind, val);
		// Define Objective
		GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MAX);
		GLPK.glp_set_obj_coef(lp, states + 1, 1.0);
		// GLPK.glp_write_lp(lp, null, "yi.lp");
		parm = new glp_smcp();
		//parm.setTm_lim(time_limit);
		GLPK.glp_init_smcp(parm);
		parm.setMsg_lev(GLPKConstants.GLP_MSG_OFF);
		long inTime = System.currentTimeMillis();
		int ret = GLPK.glp_simplex(lp, parm);
		totalLpTime += System.currentTimeMillis() - inTime;
		if (ret == 0) {
		    double val1 = GLPK.glp_get_obj_val(lp);
		    double val2 = GLPK.glp_get_col_prim(lp, states + 1);
		    // System.out.println("vals ("+val1+" "+val2+")");
		    if (val1 > delta && val2 > delta) {
		    	int idx=0;
		    	double[] data=new double[selVect.size()];
		    	int[] index=new int[selVect.size()];
		    	for (int i=1;i<=states;i++){
		    		if (GLPK.glp_get_col_prim(lp, i)!=0){
						data[idx]=GLPK.glp_get_col_prim(lp, i);
		    			index[idx]=i-1;
		    			idx++;
		    		}
		    	}
		    	bel=SparseBeliefState.transform(new SparseVector(index,data,idx)); 
		    }
		}
		GLPK.glp_delete_prob(lp);
		GLPK.glp_free_env();
		return (bel);
	}



}
