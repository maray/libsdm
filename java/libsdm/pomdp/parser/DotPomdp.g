/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: DotPomdp.g
 * Description: ANTLRv3 grammar specification to parse a .POMDP file in
 *              Cassandra's format. Not all features are supported yet.
 *              Sparse matrices and arrays use the MTJ matrix package.
 * Copyright (c) 2009, 2010 Diego Maniloff 
 * W3: http://www.cs.uic.edu/~dmanilof
 --------------------------------------------------------------------------- */

grammar DotPomdp;


/*------------------------------------------------------------------
 * TOKENS
 *------------------------------------------------------------------*/
tokens {
    DISCOUNTTOK     = 'discount' ;
    VALUESTOK       = 'values' ;
    STATESTOK       = 'states' ;
    ACTIONSTOK      = 'actions' ;
    OBSERVATIONSTOK = 'observations' ;
    TTOK            = 'T' ;
    OTOK            = 'O' ;
    RTOK            = 'R' ;
    UNIFORMTOK      = 'uniform' ;
    IDENTITYTOK     = 'identity' ;
    REWARDTOK       = 'reward' ; 
    COSTTOK         = 'cost' ;
    STARTTOK        = 'start' ;
    INCLUDETOK      = 'include' ; 
    EXCLUDETOK      = 'exclude' ;
    RESETTOK        = 'reset' ;
    COLONTOK        = ':' ;
    ASTERICKTOK     = '*' ;
    PLUSTOK         = '+' ;
    MINUSTOK        = '-' ;
}

/*------------------------------------------------------------------
 * LEXER INITIALIZATIONS
 *------------------------------------------------------------------*/
@lexer::header {
    package libsdm.pomdp.parser;}

/*------------------------------------------------------------------
 * PARSER INITIALIZATIONS
 *------------------------------------------------------------------*/
@header {
    package libsdm.pomdp.parser;
    import libsdm.common.Utils;
    import libsdm.common.SparseVector;
    import libsdm.common.SparseMatrix;
}

@members {
    // main method
    public static void main(String[] args) throws Exception {
        DotPomdpLexer lex = new DotPomdpLexer(new ANTLRFileStream(args[0]));
       	CommonTokenStream tokens = new CommonTokenStream(lex);
        DotPomdpParser parser = new DotPomdpParser(tokens);

        try {
            parser.dotPomdp();
        } catch (RecognitionException e)  {
            e.printStackTrace();
        }
    }

	
	private int matrixContext;
	
	private static final int MC_TRANSITION = 0;
	private static final int MC_TRANSITION_ROW = 1;
	private static final int MC_OBSERVATION = 2;
	private static final int MC_OBSERVATION_ROW = 3;
    // main structure
    private PomdpSpecSparse dotPomdpSpec = new PomdpSpecSparse();

    // threshold for sums of distros
    final double THRESHOLD = 1e-5;

    // return main structure
    public PomdpSpecSparse getSpec() {
        return dotPomdpSpec;
    }

    // simple debug mesg
    private void err(String msg) {
        System.err.println(msg);
    }
}

/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/
STRING  
    :   ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-')*
    ;

INT     
    :   '0' | ('1'..'9') ('0'..'9')*
    ;

FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;

COMMENT
    :   '#' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    |   '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    |   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;} // can also use skip()?
    ;

WS  
    :   ( ' '
    |   '\t'
    |   '\r'
    |   '\n'
        ) {$channel=HIDDEN;}
    ;


fragment
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/
dotPomdp
    :
        {
      		System.out.println("PARSER: Parsing preamble...");
        }            
      preamble
        {
        	dotPomdpSpec.compReward=false;
        	System.out.println("PARSER: Summary -> states "+dotPomdpSpec.nrSta);
        	System.out.println("                -> observations "+dotPomdpSpec.nrObs);
        	System.out.println("                -> actions "+dotPomdpSpec.nrAct);
           
            dotPomdpSpec.T = new SparseVector[dotPomdpSpec.nrSta][dotPomdpSpec.nrAct];
            dotPomdpSpec.O = new SparseVector[dotPomdpSpec.nrSta][dotPomdpSpec.nrAct];
            for(int s=0; s<dotPomdpSpec.nrSta; s++){
            	for(int a=0; a<dotPomdpSpec.nrAct; a++){
            	  dotPomdpSpec.T[s][a] = new SparseVector(dotPomdpSpec.nrSta);
                  dotPomdpSpec.O[s][a] = new SparseVector(dotPomdpSpec.nrObs);
                  }
               }
           
            dotPomdpSpec.R = new SparseVector[dotPomdpSpec.nrAct];
            for(int a=0; a<dotPomdpSpec.nrAct; a++)
               dotPomdpSpec.R[a] = new SparseVector(dotPomdpSpec.nrSta); 
            System.out.println("PARSER: Parsing starting state/belief...");   
        }
      start_state 
        {
            // make sure the start state is a distribution
            
            //System.out.println("Successfully parsed start state");
            if (dotPomdpSpec.startState.norm(1.0) - 1.0 > THRESHOLD)
                err("Start state not a distribution" + dotPomdpSpec.startState.norm(1));
            System.out.println("PARSER: Parsing parameters...");
        }
      param_list 
        {
            // there should be a check for the parameter distros here...
            // System.out.println("Successfully parsed parameters");
            if (dotPomdpSpec.compReward==true){
            	System.out.println("PARSER: Compressing rewards...");
            	//Create the R(a,s) type of reward (not very efficient, but only one time)
				for (int a=0;a<dotPomdpSpec.nrAct;a++){
					//R[a]=new SparseVector(dotPomdpSpec.nrSta);
					double rval[]=new double[dotPomdpSpec.nrSta];
					for (int s=0;s<dotPomdpSpec.nrSta;s++){
						//SparseMatrix prod=new SparseMatrix(dotPomdpSpec.nrSta,dotPomdpSpec.nrSta);
						//prod=dotPomdpSpec.O[a].transBmult(dotPomdpSpec.fullR[a][s]);
						double value=0;
						for (int sp=0;sp<dotPomdpSpec.nrSta;sp++){						
							value+=(dotPomdpSpec.fullR[a][s].getRow(sp)).dot(dotPomdpSpec.O[s][a])*dotPomdpSpec.T[s][a].get(sp);
						}
						rval[s]=value;
						
					}
					dotPomdpSpec.R[a]=new SparseVector(rval);
				}
            }
            System.out.println("PARSER: [DONE]");
            
        }
    ;

preamble        
    : param_type*        
    ;

param_type      
    : discount_param
    | value_param
    | state_param
    | action_param
    | obs_param
    ;


discount_param  
    : DISCOUNTTOK COLONTOK FLOAT
      {// set discount factor in global problem struct
       dotPomdpSpec.discount = Double.parseDouble($FLOAT.text);}
    ;

value_param     
    : VALUESTOK COLONTOK value_tail
    ;

value_tail      
    : REWARDTOK
    | COSTTOK
         {err("PARSER: Costs are not supported... sure that you want to use costs?");}
    ;

state_param     
    : STATESTOK COLONTOK state_tail
    ;

state_tail      
    : INT
        // we only get the total # of states
        {dotPomdpSpec.nrSta   = Integer.parseInt($INT.text);}
    | ident_list
        // we get a list of states, convert to array
        {dotPomdpSpec.staList = $ident_list.list;
         dotPomdpSpec.nrSta   = dotPomdpSpec.staList.size();}
    ;

action_param     
    : ACTIONSTOK COLONTOK action_tail
    ;

action_tail      
    : INT
        // we only get the total # of actions
        {dotPomdpSpec.nrAct   = Integer.parseInt($INT.text);}
    | ident_list
        // we get a list of actions
        {dotPomdpSpec.actList = $ident_list.list;
         dotPomdpSpec.nrAct   = dotPomdpSpec.actList.size();}
    ;

obs_param 
    : OBSERVATIONSTOK COLONTOK obs_param_tail
    ;

obs_param_tail 
    : INT
        // we only get the total # of observations
        {dotPomdpSpec.nrObs   = Integer.parseInt($INT.text);}
    | ident_list
        // we get a list of observations
        {dotPomdpSpec.obsList = $ident_list.list;
         dotPomdpSpec.nrObs   = dotPomdpSpec.obsList.size();}
    ;

start_state     
    : STARTTOK COLONTOK prob_vector
        // we'll focus on this case for now, just a sparse vector
        {
            //System.out.println("ENTERED the first case for start state");
            dotPomdpSpec.startState = $prob_vector.vector;
        }
    | STARTTOK COLONTOK STRING
        {err("PARSER: MDPs are not supported yet, only POMDPs");}
    | STARTTOK INCLUDETOK COLONTOK start_state_list
         {err("PARSER: Include and exclude features are not supported yet");}
    | STARTTOK EXCLUDETOK COLONTOK start_state_list
         {err("PARSER: Include and exclude features are not supported yet");}
    |  /* empty */
    	{
    	// Empty start state means uniform belief
    	dotPomdpSpec.startState=new SparseVector(SparseVector.getUniform(dotPomdpSpec.nrSta));
    	}
    ;

start_state_list    
    : state+
    ;

param_list     
    : param_spec*
    ;

param_spec     
    : trans_prob_spec
    | obs_prob_spec 
    | reward_spec
    ;

trans_prob_spec     
    : TTOK COLONTOK trans_spec_tail
    ;

trans_spec_tail     
    : paction COLONTOK s_1=state COLONTOK s_2=state prob // this would not detect probs>1
        // triple loop with lists
        {
            // if($prob.p > 0.0) //Some files relies in rewriting... bad thing... 
                for(int a : $paction.l)
                    for(int s1 : $s_1.l)
                        for(int s2 : $s_2.l)
                            dotPomdpSpec.T[s1][a].assign(s2, $prob.p);
        }
    | paction COLONTOK state u_matrix 
        {
        	matrixContext=MC_TRANSITION_ROW;
        	for(int a : $paction.l)	
        		for (int s : $state.l)
        			for (int i=0;i<dotPomdpSpec.nrSta;i++)
        				dotPomdpSpec.T[s][a].assign(i,$u_matrix.m.get(i,0));
        }
    | paction ui_matrix
        // full matrix specification, set if for each action 
        {
        matrixContext=MC_TRANSITION;
        for(int a : $paction.l)
        	for(int s=0;s<dotPomdpSpec.nrSta;s++) 
        		dotPomdpSpec.T[s][a] = ($ui_matrix.m).getColumn(s);
        }
    ;

obs_prob_spec  
    : OTOK COLONTOK obs_spec_tail
    ;

obs_spec_tail  
    : paction COLONTOK state COLONTOK obs prob
        // triple loop with lists
        {
        //if($prob.p > 0.0) // rewriting... puff 
            for(int a : $paction.l)
                for(int s2 : $state.l)
                    for(int o : $obs.l)
                        dotPomdpSpec.O[s2][a].assign(o, $prob.p);
        }
    | paction COLONTOK state u_matrix
        	{
        	matrixContext=MC_OBSERVATION_ROW;
        	for(int a : $paction.l)	
        		for (int s : $state.l)
        			for (int i=0;i<dotPomdpSpec.nrObs;i++)
        				dotPomdpSpec.O[s][a].assign(i,$u_matrix.m.get(i,0));
        	}
    | paction u_matrix
        // full matrix specification, set if for each action 
        {
        	matrixContext=MC_OBSERVATION;
        	for(int a : $paction.l) 
        		for(int s=0;s<dotPomdpSpec.nrSta;s++)
        			dotPomdpSpec.O[s][a] = ($u_matrix.m).getColumn(s);
        }
    ;

reward_spec    
    : RTOK COLONTOK reward_spec_tail
    ;

reward_spec_tail 
    : paction COLONTOK s_1=state COLONTOK s_2=state COLONTOK obs number
        {   
        	if(!dotPomdpSpec.compReward && $s_2.text.compareTo(Character.toString('*'))!=0 ||
                        $obs.text.compareTo(Character.toString('*'))!=0){
                System.out.println("PARSER: full reward representation detected");        
            	// Compressed rewards do not apply any more :(, trying full rewards
            	dotPomdpSpec.compReward=true;
                // Creating Huge Reward Matrix (4D)
                dotPomdpSpec.fullR=new SparseMatrix[dotPomdpSpec.nrAct][dotPomdpSpec.nrSta];    
            	for(int a=0; a<dotPomdpSpec.nrAct; a++) 
            		for(int s=0; s<dotPomdpSpec.nrSta; s++){ 
                		dotPomdpSpec.fullR[a][s] = new SparseMatrix(dotPomdpSpec.nrSta,dotPomdpSpec.nrObs);
                		// Now we have to copy the date from R to fullR
                		SparseVector colV=SparseVector.getHomogene(dotPomdpSpec.nrSta,dotPomdpSpec.R[a].get(s));
                		//new SparseVector(dotPomdpSpec.nrSta);
                		//for (int sp=0;sp<dotPomdpSpec.nrSta;sp++)
                		//	colV.assign(sp,dotPomdpSpec.R[a].get(s));	
                		for (int o=0;o<dotPomdpSpec.nrObs;o++)
                			dotPomdpSpec.fullR[a][s].assignColumn(o,colV);
                	}
            }
        	if (!dotPomdpSpec.compReward){
        	    if($number.n != 0.0)
        			for(int a : $paction.l)
                		for(int s1 : $s_1.l)
                    		dotPomdpSpec.R[a].assign(s1, $number.n); 
        	}
        	else{         
            	if($number.n != 0.0)
            		for(int a : $paction.l)
                		for(int s1 : $s_1.l)
                			for(int s2 : $s_2.l)
                				for(int o : $obs.l) 
                    				dotPomdpSpec.fullR[a][s1].assign(s2,o,$number.n);                   
        	}
        }
    | paction COLONTOK state COLONTOK state num_matrix
        {
        err("unsupported feature COLONTOK state COLONTOK state num_matrix");}
    | paction COLONTOK state num_matrix
        {err("unsupported feature COLONTOK state num_matrix");}
    ;

ui_matrix returns [SparseMatrix m]     
    : UNIFORMTOK 
    	{$m = SparseMatrix.getUniform(dotPomdpSpec.nrSta,dotPomdpSpec.nrSta);}
    | IDENTITYTOK 
        {$m = SparseMatrix.getIdentity(dotPomdpSpec.nrSta);}
    | prob_matrix
    	{$m = $prob_matrix.m;}
    ;

u_matrix returns [SparseMatrix m]
    : UNIFORMTOK
    	{
    	switch (matrixContext){
    	case MC_OBSERVATION: 
    		$m = SparseMatrix.getUniform(dotPomdpSpec.nrSta,dotPomdpSpec.nrObs);
    		break;
    	case MC_TRANSITION:
    		$m = SparseMatrix.getUniform(dotPomdpSpec.nrSta,dotPomdpSpec.nrSta);
    		break;
    	case MC_TRANSITION_ROW:
    		$m = SparseMatrix.getUniform(1,dotPomdpSpec.nrSta);
    		break;
 		case MC_OBSERVATION_ROW:
    		$m = SparseMatrix.getUniform(1,dotPomdpSpec.nrObs);
    		break;
    	default:
    		err("PARSER: wrong matrix context... umh? (UNIFORMTOK)");
    		break;
    	}
    	}
    | RESETTOK
    	{err("PARSER: the reset feature is not supported yet");}
    | prob_matrix
    	{$m = $prob_matrix.m;}
    ;

prob_matrix returns [SparseMatrix m]
    : 
     {
     int index = 0;
     int i_max,j_max;
     	switch (matrixContext){
    	case MC_OBSERVATION:
    	 	i_max = dotPomdpSpec.nrObs;
    	 	j_max = dotPomdpSpec.nrSta;
   			break;
    	case MC_TRANSITION:
    	 	i_max = dotPomdpSpec.nrSta;
    	 	j_max = dotPomdpSpec.nrSta;
    		break;
    	case MC_TRANSITION_ROW:
    	 	i_max = dotPomdpSpec.nrSta;
    	 	j_max = 1;
    		break;
 		case MC_OBSERVATION_ROW:
 		    i_max = dotPomdpSpec.nrObs;
    	 	j_max = 1;
    		break;
    	default:
    		err("PARSER: wrong matrix context... umh? (prob_matrix)");
    		j_max=0;
    		i_max=0;
    		break;
    	}  
     $m = new SparseMatrix(i_max,j_max);
     } 
        (prob 
        {
        	if ($prob.p > 0.0) $m.assign(index \% i_max,index / i_max,$prob.p);
            index++;
        }
        )+
    ;

prob_vector returns [SparseVector vector]
    : 
        // initialization here is OK
        {int index = 0; $vector = new SparseVector(dotPomdpSpec.nrSta);} 
        (prob 
        {
            // action here - the check for 0 actually doesn't matter
            if ($prob.p > 0.0) $vector.assign(index, $prob.p);
            index++;
        }
        )+
    ;

num_matrix     
    :      {
     int index = 0;
     //int i_max;
     } 
        (number 
        {
            index++;
        }
        )+
    ;

state returns [ArrayList<Integer> l = new ArrayList<Integer>()]
    :
        INT 
        {$l.add(Integer.parseInt($INT.text));}
    | 
        STRING
        {$l.add(dotPomdpSpec.staList.indexOf($STRING.text));}
    | 
        ASTERICKTOK
        {for(int s=0; s<dotPomdpSpec.nrSta; s++) $l.add(s);}
    ;   

paction returns [ArrayList<Integer> l = new ArrayList<Integer>()]
    :
        INT 
        {$l.add(Integer.parseInt($INT.text));}
    | 
        STRING
        {$l.add(dotPomdpSpec.actList.indexOf($STRING.text));}
    | 
        ASTERICKTOK
        {for(int a=0; a<dotPomdpSpec.nrAct; a++) $l.add(a);}
    ;

obs returns [ArrayList<Integer> l = new ArrayList<Integer>()]    
    :
        INT 
        {$l.add(Integer.parseInt($INT.text));}
    | 
        STRING
        {$l.add(dotPomdpSpec.obsList.indexOf($STRING.text));}
    | 
        ASTERICKTOK
        {for(int o=0; o<dotPomdpSpec.nrObs; o++) $l.add(o);}
    ;

ident_list returns [ArrayList<String> list]     
    : 
        {$list = new ArrayList<String>();}
        (STRING
        {$list.add($STRING.text);}
        )+
    ;

prob returns [double p]      
    : INT
        {$p = Double.parseDouble($INT.text);}
    | FLOAT
        {$p = Double.parseDouble($FLOAT.text);}  
    ;

number returns [double n]          
    : optional_sign INT
        {$n = $optional_sign.s * Double.parseDouble($INT.text);} 
    | optional_sign FLOAT
        {$n = $optional_sign.s * Double.parseDouble($FLOAT.text);} 
    ;

optional_sign returns [int s]
    : PLUSTOK
        {$s = 1;}
    | MINUSTOK
        {$s = -1;}
    |  /* empty */
        {$s = 1;}
    ;
