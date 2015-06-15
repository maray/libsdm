// $ANTLR 3.2 debian-7ubuntu3 /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g 2015-06-15 13:00:16

    package libsdm.pomdp.parser;
    import libsdm.common.Utils;
    import libsdm.common.SparseVector;
    import libsdm.common.SparseMatrix;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

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
public class DotPomdpParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "DISCOUNTTOK", "VALUESTOK", "STATESTOK", "ACTIONSTOK", "OBSERVATIONSTOK", "TTOK", "OTOK", "RTOK", "UNIFORMTOK", "IDENTITYTOK", "REWARDTOK", "COSTTOK", "STARTTOK", "INCLUDETOK", "EXCLUDETOK", "RESETTOK", "COLONTOK", "ASTERICKTOK", "PLUSTOK", "MINUSTOK", "STRING", "INT", "EXPONENT", "FLOAT", "COMMENT", "WS"
    };
    public static final int IDENTITYTOK=13;
    public static final int EXPONENT=26;
    public static final int ACTIONSTOK=7;
    public static final int OTOK=10;
    public static final int COSTTOK=15;
    public static final int STARTTOK=16;
    public static final int DISCOUNTTOK=4;
    public static final int MINUSTOK=23;
    public static final int FLOAT=27;
    public static final int INT=25;
    public static final int VALUESTOK=5;
    public static final int INCLUDETOK=17;
    public static final int EOF=-1;
    public static final int OBSERVATIONSTOK=8;
    public static final int ASTERICKTOK=21;
    public static final int WS=29;
    public static final int EXCLUDETOK=18;
    public static final int TTOK=9;
    public static final int COLONTOK=20;
    public static final int PLUSTOK=22;
    public static final int UNIFORMTOK=12;
    public static final int REWARDTOK=14;
    public static final int COMMENT=28;
    public static final int RTOK=11;
    public static final int STATESTOK=6;
    public static final int STRING=24;
    public static final int RESETTOK=19;

    // delegates
    // delegators


        public DotPomdpParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public DotPomdpParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return DotPomdpParser.tokenNames; }
    public String getGrammarFileName() { return "/home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g"; }


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



    // $ANTLR start "dotPomdp"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:133:1: dotPomdp : preamble start_state param_list ;
    public final void dotPomdp() throws RecognitionException {
        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:134:5: ( preamble start_state param_list )
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:135:9: preamble start_state param_list
            {

                  		System.out.println("PARSER: Parsing preamble...");
                    
            pushFollow(FOLLOW_preamble_in_dotPomdp796);
            preamble();

            state._fsp--;


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
                    
            pushFollow(FOLLOW_start_state_in_dotPomdp814);
            start_state();

            state._fsp--;


                        // make sure the start state is a distribution
                        
                        //System.out.println("Successfully parsed start state");
                        if (dotPomdpSpec.startState.norm(1.0) - 1.0 > THRESHOLD)
                            err("Start state not a distribution" + dotPomdpSpec.startState.norm(1));
                        System.out.println("PARSER: Parsing parameters...");
                    
            pushFollow(FOLLOW_param_list_in_dotPomdp833);
            param_list();

            state._fsp--;


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

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "dotPomdp"


    // $ANTLR start "preamble"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:196:1: preamble : ( param_type )* ;
    public final void preamble() throws RecognitionException {
        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:197:5: ( ( param_type )* )
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:197:7: ( param_type )*
            {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:197:7: ( param_type )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>=DISCOUNTTOK && LA1_0<=OBSERVATIONSTOK)) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:197:7: param_type
            	    {
            	    pushFollow(FOLLOW_param_type_in_preamble869);
            	    param_type();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "preamble"


    // $ANTLR start "param_type"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:200:1: param_type : ( discount_param | value_param | state_param | action_param | obs_param );
    public final void param_type() throws RecognitionException {
        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:201:5: ( discount_param | value_param | state_param | action_param | obs_param )
            int alt2=5;
            switch ( input.LA(1) ) {
            case DISCOUNTTOK:
                {
                alt2=1;
                }
                break;
            case VALUESTOK:
                {
                alt2=2;
                }
                break;
            case STATESTOK:
                {
                alt2=3;
                }
                break;
            case ACTIONSTOK:
                {
                alt2=4;
                }
                break;
            case OBSERVATIONSTOK:
                {
                alt2=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }

            switch (alt2) {
                case 1 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:201:7: discount_param
                    {
                    pushFollow(FOLLOW_discount_param_in_param_type901);
                    discount_param();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:202:7: value_param
                    {
                    pushFollow(FOLLOW_value_param_in_param_type909);
                    value_param();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:203:7: state_param
                    {
                    pushFollow(FOLLOW_state_param_in_param_type917);
                    state_param();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:204:7: action_param
                    {
                    pushFollow(FOLLOW_action_param_in_param_type925);
                    action_param();

                    state._fsp--;


                    }
                    break;
                case 5 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:205:7: obs_param
                    {
                    pushFollow(FOLLOW_obs_param_in_param_type933);
                    obs_param();

                    state._fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "param_type"


    // $ANTLR start "discount_param"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:209:1: discount_param : DISCOUNTTOK COLONTOK FLOAT ;
    public final void discount_param() throws RecognitionException {
        Token FLOAT1=null;

        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:210:5: ( DISCOUNTTOK COLONTOK FLOAT )
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:210:7: DISCOUNTTOK COLONTOK FLOAT
            {
            match(input,DISCOUNTTOK,FOLLOW_DISCOUNTTOK_in_discount_param953); 
            match(input,COLONTOK,FOLLOW_COLONTOK_in_discount_param955); 
            FLOAT1=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_discount_param957); 
            // set discount factor in global problem struct
                   dotPomdpSpec.discount = Double.parseDouble((FLOAT1!=null?FLOAT1.getText():null));

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "discount_param"


    // $ANTLR start "value_param"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:215:1: value_param : VALUESTOK COLONTOK value_tail ;
    public final void value_param() throws RecognitionException {
        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:216:5: ( VALUESTOK COLONTOK value_tail )
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:216:7: VALUESTOK COLONTOK value_tail
            {
            match(input,VALUESTOK,FOLLOW_VALUESTOK_in_value_param987); 
            match(input,COLONTOK,FOLLOW_COLONTOK_in_value_param989); 
            pushFollow(FOLLOW_value_tail_in_value_param991);
            value_tail();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "value_param"


    // $ANTLR start "value_tail"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:219:1: value_tail : ( REWARDTOK | COSTTOK );
    public final void value_tail() throws RecognitionException {
        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:220:5: ( REWARDTOK | COSTTOK )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==REWARDTOK) ) {
                alt3=1;
            }
            else if ( (LA3_0==COSTTOK) ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:220:7: REWARDTOK
                    {
                    match(input,REWARDTOK,FOLLOW_REWARDTOK_in_value_tail1014); 

                    }
                    break;
                case 2 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:221:7: COSTTOK
                    {
                    match(input,COSTTOK,FOLLOW_COSTTOK_in_value_tail1022); 
                    err("PARSER: Costs are not supported... sure that you want to use costs?");

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "value_tail"


    // $ANTLR start "state_param"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:225:1: state_param : STATESTOK COLONTOK state_tail ;
    public final void state_param() throws RecognitionException {
        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:226:5: ( STATESTOK COLONTOK state_tail )
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:226:7: STATESTOK COLONTOK state_tail
            {
            match(input,STATESTOK,FOLLOW_STATESTOK_in_state_param1055); 
            match(input,COLONTOK,FOLLOW_COLONTOK_in_state_param1057); 
            pushFollow(FOLLOW_state_tail_in_state_param1059);
            state_tail();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "state_param"


    // $ANTLR start "state_tail"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:229:1: state_tail : ( INT | ident_list );
    public final void state_tail() throws RecognitionException {
        Token INT2=null;
        ArrayList<String> ident_list3 = null;


        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:230:5: ( INT | ident_list )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==INT) ) {
                alt4=1;
            }
            else if ( (LA4_0==STRING) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:230:7: INT
                    {
                    INT2=(Token)match(input,INT,FOLLOW_INT_in_state_tail1082); 
                    dotPomdpSpec.nrSta   = Integer.parseInt((INT2!=null?INT2.getText():null));

                    }
                    break;
                case 2 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:233:7: ident_list
                    {
                    pushFollow(FOLLOW_ident_list_in_state_tail1109);
                    ident_list3=ident_list();

                    state._fsp--;

                    dotPomdpSpec.staList = ident_list3;
                             dotPomdpSpec.nrSta   = dotPomdpSpec.staList.size();

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "state_tail"


    // $ANTLR start "action_param"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:239:1: action_param : ACTIONSTOK COLONTOK action_tail ;
    public final void action_param() throws RecognitionException {
        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:240:5: ( ACTIONSTOK COLONTOK action_tail )
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:240:7: ACTIONSTOK COLONTOK action_tail
            {
            match(input,ACTIONSTOK,FOLLOW_ACTIONSTOK_in_action_param1150); 
            match(input,COLONTOK,FOLLOW_COLONTOK_in_action_param1152); 
            pushFollow(FOLLOW_action_tail_in_action_param1154);
            action_tail();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "action_param"


    // $ANTLR start "action_tail"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:243:1: action_tail : ( INT | ident_list );
    public final void action_tail() throws RecognitionException {
        Token INT4=null;
        ArrayList<String> ident_list5 = null;


        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:244:5: ( INT | ident_list )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==INT) ) {
                alt5=1;
            }
            else if ( (LA5_0==STRING) ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:244:7: INT
                    {
                    INT4=(Token)match(input,INT,FOLLOW_INT_in_action_tail1177); 
                    dotPomdpSpec.nrAct   = Integer.parseInt((INT4!=null?INT4.getText():null));

                    }
                    break;
                case 2 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:247:7: ident_list
                    {
                    pushFollow(FOLLOW_ident_list_in_action_tail1204);
                    ident_list5=ident_list();

                    state._fsp--;

                    dotPomdpSpec.actList = ident_list5;
                             dotPomdpSpec.nrAct   = dotPomdpSpec.actList.size();

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "action_tail"


    // $ANTLR start "obs_param"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:253:1: obs_param : OBSERVATIONSTOK COLONTOK obs_param_tail ;
    public final void obs_param() throws RecognitionException {
        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:254:5: ( OBSERVATIONSTOK COLONTOK obs_param_tail )
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:254:7: OBSERVATIONSTOK COLONTOK obs_param_tail
            {
            match(input,OBSERVATIONSTOK,FOLLOW_OBSERVATIONSTOK_in_obs_param1241); 
            match(input,COLONTOK,FOLLOW_COLONTOK_in_obs_param1243); 
            pushFollow(FOLLOW_obs_param_tail_in_obs_param1245);
            obs_param_tail();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "obs_param"


    // $ANTLR start "obs_param_tail"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:257:1: obs_param_tail : ( INT | ident_list );
    public final void obs_param_tail() throws RecognitionException {
        Token INT6=null;
        ArrayList<String> ident_list7 = null;


        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:258:5: ( INT | ident_list )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==INT) ) {
                alt6=1;
            }
            else if ( (LA6_0==STRING) ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:258:7: INT
                    {
                    INT6=(Token)match(input,INT,FOLLOW_INT_in_obs_param_tail1263); 
                    dotPomdpSpec.nrObs   = Integer.parseInt((INT6!=null?INT6.getText():null));

                    }
                    break;
                case 2 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:261:7: ident_list
                    {
                    pushFollow(FOLLOW_ident_list_in_obs_param_tail1290);
                    ident_list7=ident_list();

                    state._fsp--;

                    dotPomdpSpec.obsList = ident_list7;
                             dotPomdpSpec.nrObs   = dotPomdpSpec.obsList.size();

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "obs_param_tail"


    // $ANTLR start "start_state"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:267:1: start_state : ( STARTTOK COLONTOK prob_vector | STARTTOK COLONTOK STRING | STARTTOK INCLUDETOK COLONTOK start_state_list | STARTTOK EXCLUDETOK COLONTOK start_state_list | );
    public final void start_state() throws RecognitionException {
        SparseVector prob_vector8 = null;


        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:268:5: ( STARTTOK COLONTOK prob_vector | STARTTOK COLONTOK STRING | STARTTOK INCLUDETOK COLONTOK start_state_list | STARTTOK EXCLUDETOK COLONTOK start_state_list | )
            int alt7=5;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==STARTTOK) ) {
                switch ( input.LA(2) ) {
                case COLONTOK:
                    {
                    int LA7_3 = input.LA(3);

                    if ( (LA7_3==STRING) ) {
                        alt7=2;
                    }
                    else if ( (LA7_3==INT||LA7_3==FLOAT) ) {
                        alt7=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 7, 3, input);

                        throw nvae;
                    }
                    }
                    break;
                case INCLUDETOK:
                    {
                    alt7=3;
                    }
                    break;
                case EXCLUDETOK:
                    {
                    alt7=4;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 7, 1, input);

                    throw nvae;
                }

            }
            else if ( (LA7_0==EOF||(LA7_0>=TTOK && LA7_0<=RTOK)) ) {
                alt7=5;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:268:7: STARTTOK COLONTOK prob_vector
                    {
                    match(input,STARTTOK,FOLLOW_STARTTOK_in_start_state1331); 
                    match(input,COLONTOK,FOLLOW_COLONTOK_in_start_state1333); 
                    pushFollow(FOLLOW_prob_vector_in_start_state1335);
                    prob_vector8=prob_vector();

                    state._fsp--;


                                //System.out.println("ENTERED the first case for start state");
                                dotPomdpSpec.startState = prob_vector8;
                            

                    }
                    break;
                case 2 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:274:7: STARTTOK COLONTOK STRING
                    {
                    match(input,STARTTOK,FOLLOW_STARTTOK_in_start_state1362); 
                    match(input,COLONTOK,FOLLOW_COLONTOK_in_start_state1364); 
                    match(input,STRING,FOLLOW_STRING_in_start_state1366); 
                    err("PARSER: MDPs are not supported yet, only POMDPs");

                    }
                    break;
                case 3 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:276:7: STARTTOK INCLUDETOK COLONTOK start_state_list
                    {
                    match(input,STARTTOK,FOLLOW_STARTTOK_in_start_state1384); 
                    match(input,INCLUDETOK,FOLLOW_INCLUDETOK_in_start_state1386); 
                    match(input,COLONTOK,FOLLOW_COLONTOK_in_start_state1388); 
                    pushFollow(FOLLOW_start_state_list_in_start_state1390);
                    start_state_list();

                    state._fsp--;

                    err("PARSER: Include and exclude features are not supported yet");

                    }
                    break;
                case 4 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:278:7: STARTTOK EXCLUDETOK COLONTOK start_state_list
                    {
                    match(input,STARTTOK,FOLLOW_STARTTOK_in_start_state1409); 
                    match(input,EXCLUDETOK,FOLLOW_EXCLUDETOK_in_start_state1411); 
                    match(input,COLONTOK,FOLLOW_COLONTOK_in_start_state1413); 
                    pushFollow(FOLLOW_start_state_list_in_start_state1415);
                    start_state_list();

                    state._fsp--;

                    err("PARSER: Include and exclude features are not supported yet");

                    }
                    break;
                case 5 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:281:6: 
                    {

                        	// Empty start state means uniform belief
                        	dotPomdpSpec.startState=new SparseVector(SparseVector.getUniform(dotPomdpSpec.nrSta));
                        	

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "start_state"


    // $ANTLR start "start_state_list"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:287:1: start_state_list : ( state )+ ;
    public final void start_state_list() throws RecognitionException {
        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:288:5: ( ( state )+ )
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:288:7: ( state )+
            {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:288:7: ( state )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==ASTERICKTOK||(LA8_0>=STRING && LA8_0<=INT)) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:288:7: state
            	    {
            	    pushFollow(FOLLOW_state_in_start_state_list1463);
            	    state();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "start_state_list"


    // $ANTLR start "param_list"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:291:1: param_list : ( param_spec )* ;
    public final void param_list() throws RecognitionException {
        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:292:5: ( ( param_spec )* )
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:292:7: ( param_spec )*
            {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:292:7: ( param_spec )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( ((LA9_0>=TTOK && LA9_0<=RTOK)) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:292:7: param_spec
            	    {
            	    pushFollow(FOLLOW_param_spec_in_param_list1486);
            	    param_spec();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "param_list"


    // $ANTLR start "param_spec"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:295:1: param_spec : ( trans_prob_spec | obs_prob_spec | reward_spec );
    public final void param_spec() throws RecognitionException {
        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:296:5: ( trans_prob_spec | obs_prob_spec | reward_spec )
            int alt10=3;
            switch ( input.LA(1) ) {
            case TTOK:
                {
                alt10=1;
                }
                break;
            case OTOK:
                {
                alt10=2;
                }
                break;
            case RTOK:
                {
                alt10=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }

            switch (alt10) {
                case 1 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:296:7: trans_prob_spec
                    {
                    pushFollow(FOLLOW_trans_prob_spec_in_param_spec1509);
                    trans_prob_spec();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:297:7: obs_prob_spec
                    {
                    pushFollow(FOLLOW_obs_prob_spec_in_param_spec1517);
                    obs_prob_spec();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:298:7: reward_spec
                    {
                    pushFollow(FOLLOW_reward_spec_in_param_spec1526);
                    reward_spec();

                    state._fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "param_spec"


    // $ANTLR start "trans_prob_spec"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:301:1: trans_prob_spec : TTOK COLONTOK trans_spec_tail ;
    public final void trans_prob_spec() throws RecognitionException {
        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:302:5: ( TTOK COLONTOK trans_spec_tail )
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:302:7: TTOK COLONTOK trans_spec_tail
            {
            match(input,TTOK,FOLLOW_TTOK_in_trans_prob_spec1548); 
            match(input,COLONTOK,FOLLOW_COLONTOK_in_trans_prob_spec1550); 
            pushFollow(FOLLOW_trans_spec_tail_in_trans_prob_spec1552);
            trans_spec_tail();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "trans_prob_spec"


    // $ANTLR start "trans_spec_tail"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:305:1: trans_spec_tail : ( paction COLONTOK s_1= state COLONTOK s_2= state prob | paction COLONTOK state u_matrix | paction ui_matrix );
    public final void trans_spec_tail() throws RecognitionException {
        DotPomdpParser.state_return s_1 = null;

        DotPomdpParser.state_return s_2 = null;

        double prob9 = 0.0;

        ArrayList<Integer> paction10 = null;

        ArrayList<Integer> paction11 = null;

        DotPomdpParser.state_return state12 = null;

        SparseMatrix u_matrix13 = null;

        ArrayList<Integer> paction14 = null;

        SparseMatrix ui_matrix15 = null;


        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:306:5: ( paction COLONTOK s_1= state COLONTOK s_2= state prob | paction COLONTOK state u_matrix | paction ui_matrix )
            int alt11=3;
            alt11 = dfa11.predict(input);
            switch (alt11) {
                case 1 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:306:7: paction COLONTOK s_1= state COLONTOK s_2= state prob
                    {
                    pushFollow(FOLLOW_paction_in_trans_spec_tail1574);
                    paction10=paction();

                    state._fsp--;

                    match(input,COLONTOK,FOLLOW_COLONTOK_in_trans_spec_tail1576); 
                    pushFollow(FOLLOW_state_in_trans_spec_tail1580);
                    s_1=state();

                    state._fsp--;

                    match(input,COLONTOK,FOLLOW_COLONTOK_in_trans_spec_tail1582); 
                    pushFollow(FOLLOW_state_in_trans_spec_tail1586);
                    s_2=state();

                    state._fsp--;

                    pushFollow(FOLLOW_prob_in_trans_spec_tail1588);
                    prob9=prob();

                    state._fsp--;


                                // if(prob9 > 0.0) //Some files relies in rewriting... bad thing... 
                                    for(int a : paction10)
                                        for(int s1 : (s_1!=null?s_1.l:null))
                                            for(int s2 : (s_2!=null?s_2.l:null))
                                                dotPomdpSpec.T[s1][a].assign(s2, prob9);
                            

                    }
                    break;
                case 2 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:315:7: paction COLONTOK state u_matrix
                    {
                    pushFollow(FOLLOW_paction_in_trans_spec_tail1616);
                    paction11=paction();

                    state._fsp--;

                    match(input,COLONTOK,FOLLOW_COLONTOK_in_trans_spec_tail1618); 
                    pushFollow(FOLLOW_state_in_trans_spec_tail1620);
                    state12=state();

                    state._fsp--;

                    pushFollow(FOLLOW_u_matrix_in_trans_spec_tail1622);
                    u_matrix13=u_matrix();

                    state._fsp--;


                            	matrixContext=MC_TRANSITION_ROW;
                            	for(int a : paction11)	
                            		for (int s : (state12!=null?state12.l:null))
                            			for (int i=0;i<dotPomdpSpec.nrSta;i++)
                            				dotPomdpSpec.T[s][a].assign(i,u_matrix13.get(i,0));
                            

                    }
                    break;
                case 3 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:323:7: paction ui_matrix
                    {
                    pushFollow(FOLLOW_paction_in_trans_spec_tail1641);
                    paction14=paction();

                    state._fsp--;

                    pushFollow(FOLLOW_ui_matrix_in_trans_spec_tail1643);
                    ui_matrix15=ui_matrix();

                    state._fsp--;


                            matrixContext=MC_TRANSITION;
                            for(int a : paction14)
                            	for(int s=0;s<dotPomdpSpec.nrSta;s++) 
                            		dotPomdpSpec.T[s][a] = (ui_matrix15).getColumn(s);
                            

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "trans_spec_tail"


    // $ANTLR start "obs_prob_spec"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:333:1: obs_prob_spec : OTOK COLONTOK obs_spec_tail ;
    public final void obs_prob_spec() throws RecognitionException {
        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:334:5: ( OTOK COLONTOK obs_spec_tail )
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:334:7: OTOK COLONTOK obs_spec_tail
            {
            match(input,OTOK,FOLLOW_OTOK_in_obs_prob_spec1681); 
            match(input,COLONTOK,FOLLOW_COLONTOK_in_obs_prob_spec1683); 
            pushFollow(FOLLOW_obs_spec_tail_in_obs_prob_spec1685);
            obs_spec_tail();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "obs_prob_spec"


    // $ANTLR start "obs_spec_tail"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:337:1: obs_spec_tail : ( paction COLONTOK state COLONTOK obs prob | paction COLONTOK state u_matrix | paction u_matrix );
    public final void obs_spec_tail() throws RecognitionException {
        double prob16 = 0.0;

        ArrayList<Integer> paction17 = null;

        DotPomdpParser.state_return state18 = null;

        DotPomdpParser.obs_return obs19 = null;

        ArrayList<Integer> paction20 = null;

        DotPomdpParser.state_return state21 = null;

        SparseMatrix u_matrix22 = null;

        ArrayList<Integer> paction23 = null;

        SparseMatrix u_matrix24 = null;


        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:338:5: ( paction COLONTOK state COLONTOK obs prob | paction COLONTOK state u_matrix | paction u_matrix )
            int alt12=3;
            alt12 = dfa12.predict(input);
            switch (alt12) {
                case 1 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:338:7: paction COLONTOK state COLONTOK obs prob
                    {
                    pushFollow(FOLLOW_paction_in_obs_spec_tail1704);
                    paction17=paction();

                    state._fsp--;

                    match(input,COLONTOK,FOLLOW_COLONTOK_in_obs_spec_tail1706); 
                    pushFollow(FOLLOW_state_in_obs_spec_tail1708);
                    state18=state();

                    state._fsp--;

                    match(input,COLONTOK,FOLLOW_COLONTOK_in_obs_spec_tail1710); 
                    pushFollow(FOLLOW_obs_in_obs_spec_tail1712);
                    obs19=obs();

                    state._fsp--;

                    pushFollow(FOLLOW_prob_in_obs_spec_tail1714);
                    prob16=prob();

                    state._fsp--;


                            //if(prob16 > 0.0) // rewriting... puff 
                                for(int a : paction17)
                                    for(int s2 : (state18!=null?state18.l:null))
                                        for(int o : (obs19!=null?obs19.l:null))
                                            dotPomdpSpec.O[s2][a].assign(o, prob16);
                            

                    }
                    break;
                case 2 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:347:7: paction COLONTOK state u_matrix
                    {
                    pushFollow(FOLLOW_paction_in_obs_spec_tail1741);
                    paction20=paction();

                    state._fsp--;

                    match(input,COLONTOK,FOLLOW_COLONTOK_in_obs_spec_tail1743); 
                    pushFollow(FOLLOW_state_in_obs_spec_tail1745);
                    state21=state();

                    state._fsp--;

                    pushFollow(FOLLOW_u_matrix_in_obs_spec_tail1747);
                    u_matrix22=u_matrix();

                    state._fsp--;


                            	matrixContext=MC_OBSERVATION_ROW;
                            	for(int a : paction20)	
                            		for (int s : (state21!=null?state21.l:null))
                            			for (int i=0;i<dotPomdpSpec.nrObs;i++)
                            				dotPomdpSpec.O[s][a].assign(i,u_matrix22.get(i,0));
                            	

                    }
                    break;
                case 3 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:355:7: paction u_matrix
                    {
                    pushFollow(FOLLOW_paction_in_obs_spec_tail1766);
                    paction23=paction();

                    state._fsp--;

                    pushFollow(FOLLOW_u_matrix_in_obs_spec_tail1768);
                    u_matrix24=u_matrix();

                    state._fsp--;


                            	matrixContext=MC_OBSERVATION;
                            	for(int a : paction23) 
                            		for(int s=0;s<dotPomdpSpec.nrSta;s++)
                            			dotPomdpSpec.O[s][a] = (u_matrix24).getColumn(s);
                            

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "obs_spec_tail"


    // $ANTLR start "reward_spec"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:365:1: reward_spec : RTOK COLONTOK reward_spec_tail ;
    public final void reward_spec() throws RecognitionException {
        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:366:5: ( RTOK COLONTOK reward_spec_tail )
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:366:7: RTOK COLONTOK reward_spec_tail
            {
            match(input,RTOK,FOLLOW_RTOK_in_reward_spec1808); 
            match(input,COLONTOK,FOLLOW_COLONTOK_in_reward_spec1810); 
            pushFollow(FOLLOW_reward_spec_tail_in_reward_spec1812);
            reward_spec_tail();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "reward_spec"


    // $ANTLR start "reward_spec_tail"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:369:1: reward_spec_tail : ( paction COLONTOK s_1= state COLONTOK s_2= state COLONTOK obs number | paction COLONTOK state COLONTOK state num_matrix | paction COLONTOK state num_matrix );
    public final void reward_spec_tail() throws RecognitionException {
        DotPomdpParser.state_return s_1 = null;

        DotPomdpParser.state_return s_2 = null;

        DotPomdpParser.obs_return obs25 = null;

        double number26 = 0.0;

        ArrayList<Integer> paction27 = null;


        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:370:5: ( paction COLONTOK s_1= state COLONTOK s_2= state COLONTOK obs number | paction COLONTOK state COLONTOK state num_matrix | paction COLONTOK state num_matrix )
            int alt13=3;
            alt13 = dfa13.predict(input);
            switch (alt13) {
                case 1 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:370:7: paction COLONTOK s_1= state COLONTOK s_2= state COLONTOK obs number
                    {
                    pushFollow(FOLLOW_paction_in_reward_spec_tail1830);
                    paction27=paction();

                    state._fsp--;

                    match(input,COLONTOK,FOLLOW_COLONTOK_in_reward_spec_tail1832); 
                    pushFollow(FOLLOW_state_in_reward_spec_tail1836);
                    s_1=state();

                    state._fsp--;

                    match(input,COLONTOK,FOLLOW_COLONTOK_in_reward_spec_tail1838); 
                    pushFollow(FOLLOW_state_in_reward_spec_tail1842);
                    s_2=state();

                    state._fsp--;

                    match(input,COLONTOK,FOLLOW_COLONTOK_in_reward_spec_tail1844); 
                    pushFollow(FOLLOW_obs_in_reward_spec_tail1846);
                    obs25=obs();

                    state._fsp--;

                    pushFollow(FOLLOW_number_in_reward_spec_tail1848);
                    number26=number();

                    state._fsp--;

                       
                            	if(!dotPomdpSpec.compReward && (s_2!=null?input.toString(s_2.start,s_2.stop):null).compareTo(Character.toString('*'))!=0 ||
                                            (obs25!=null?input.toString(obs25.start,obs25.stop):null).compareTo(Character.toString('*'))!=0){
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
                            	    if(number26 != 0.0)
                            			for(int a : paction27)
                                    		for(int s1 : (s_1!=null?s_1.l:null))
                                        		dotPomdpSpec.R[a].assign(s1, number26); 
                            	}
                            	else{         
                                	if(number26 != 0.0)
                                		for(int a : paction27)
                                    		for(int s1 : (s_1!=null?s_1.l:null))
                                    			for(int s2 : (s_2!=null?s_2.l:null))
                                    				for(int o : (obs25!=null?obs25.l:null)) 
                                        				dotPomdpSpec.fullR[a][s1].assign(s2,o,number26);                   
                            	}
                            

                    }
                    break;
                case 2 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:406:7: paction COLONTOK state COLONTOK state num_matrix
                    {
                    pushFollow(FOLLOW_paction_in_reward_spec_tail1866);
                    paction();

                    state._fsp--;

                    match(input,COLONTOK,FOLLOW_COLONTOK_in_reward_spec_tail1868); 
                    pushFollow(FOLLOW_state_in_reward_spec_tail1870);
                    state();

                    state._fsp--;

                    match(input,COLONTOK,FOLLOW_COLONTOK_in_reward_spec_tail1872); 
                    pushFollow(FOLLOW_state_in_reward_spec_tail1874);
                    state();

                    state._fsp--;

                    pushFollow(FOLLOW_num_matrix_in_reward_spec_tail1876);
                    num_matrix();

                    state._fsp--;


                            err("unsupported feature COLONTOK state COLONTOK state num_matrix");

                    }
                    break;
                case 3 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:409:7: paction COLONTOK state num_matrix
                    {
                    pushFollow(FOLLOW_paction_in_reward_spec_tail1894);
                    paction();

                    state._fsp--;

                    match(input,COLONTOK,FOLLOW_COLONTOK_in_reward_spec_tail1896); 
                    pushFollow(FOLLOW_state_in_reward_spec_tail1898);
                    state();

                    state._fsp--;

                    pushFollow(FOLLOW_num_matrix_in_reward_spec_tail1900);
                    num_matrix();

                    state._fsp--;

                    err("unsupported feature COLONTOK state num_matrix");

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "reward_spec_tail"


    // $ANTLR start "ui_matrix"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:413:1: ui_matrix returns [SparseMatrix m] : ( UNIFORMTOK | IDENTITYTOK | prob_matrix );
    public final SparseMatrix ui_matrix() throws RecognitionException {
        SparseMatrix m = null;

        SparseMatrix prob_matrix28 = null;


        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:414:5: ( UNIFORMTOK | IDENTITYTOK | prob_matrix )
            int alt14=3;
            switch ( input.LA(1) ) {
            case UNIFORMTOK:
                {
                alt14=1;
                }
                break;
            case IDENTITYTOK:
                {
                alt14=2;
                }
                break;
            case INT:
            case FLOAT:
                {
                alt14=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }

            switch (alt14) {
                case 1 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:414:7: UNIFORMTOK
                    {
                    match(input,UNIFORMTOK,FOLLOW_UNIFORMTOK_in_ui_matrix1936); 
                    m = SparseMatrix.getUniform(dotPomdpSpec.nrSta,dotPomdpSpec.nrSta);

                    }
                    break;
                case 2 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:416:7: IDENTITYTOK
                    {
                    match(input,IDENTITYTOK,FOLLOW_IDENTITYTOK_in_ui_matrix1952); 
                    m = SparseMatrix.getIdentity(dotPomdpSpec.nrSta);

                    }
                    break;
                case 3 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:418:7: prob_matrix
                    {
                    pushFollow(FOLLOW_prob_matrix_in_ui_matrix1971);
                    prob_matrix28=prob_matrix();

                    state._fsp--;

                    m = prob_matrix28;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return m;
    }
    // $ANTLR end "ui_matrix"


    // $ANTLR start "u_matrix"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:422:1: u_matrix returns [SparseMatrix m] : ( UNIFORMTOK | RESETTOK | prob_matrix );
    public final SparseMatrix u_matrix() throws RecognitionException {
        SparseMatrix m = null;

        SparseMatrix prob_matrix29 = null;


        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:423:5: ( UNIFORMTOK | RESETTOK | prob_matrix )
            int alt15=3;
            switch ( input.LA(1) ) {
            case UNIFORMTOK:
                {
                alt15=1;
                }
                break;
            case RESETTOK:
                {
                alt15=2;
                }
                break;
            case INT:
            case FLOAT:
                {
                alt15=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:423:7: UNIFORMTOK
                    {
                    match(input,UNIFORMTOK,FOLLOW_UNIFORMTOK_in_u_matrix1999); 

                        	switch (matrixContext){
                        	case MC_OBSERVATION: 
                        		m = SparseMatrix.getUniform(dotPomdpSpec.nrSta,dotPomdpSpec.nrObs);
                        		break;
                        	case MC_TRANSITION:
                        		m = SparseMatrix.getUniform(dotPomdpSpec.nrSta,dotPomdpSpec.nrSta);
                        		break;
                        	case MC_TRANSITION_ROW:
                        		m = SparseMatrix.getUniform(1,dotPomdpSpec.nrSta);
                        		break;
                     		case MC_OBSERVATION_ROW:
                        		m = SparseMatrix.getUniform(1,dotPomdpSpec.nrObs);
                        		break;
                        	default:
                        		err("PARSER: wrong matrix context... umh? (UNIFORMTOK)");
                        		break;
                        	}
                        	

                    }
                    break;
                case 2 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:443:7: RESETTOK
                    {
                    match(input,RESETTOK,FOLLOW_RESETTOK_in_u_matrix2014); 
                    err("PARSER: the reset feature is not supported yet");

                    }
                    break;
                case 3 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:445:7: prob_matrix
                    {
                    pushFollow(FOLLOW_prob_matrix_in_u_matrix2029);
                    prob_matrix29=prob_matrix();

                    state._fsp--;

                    m = prob_matrix29;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return m;
    }
    // $ANTLR end "u_matrix"


    // $ANTLR start "prob_matrix"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:449:1: prob_matrix returns [SparseMatrix m] : ( prob )+ ;
    public final SparseMatrix prob_matrix() throws RecognitionException {
        SparseMatrix m = null;

        double prob30 = 0.0;


        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:450:5: ( ( prob )+ )
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:451:6: ( prob )+
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
                 m = new SparseMatrix(i_max,j_max);
                 
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:479:9: ( prob )+
            int cnt16=0;
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==INT||LA16_0==FLOAT) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:479:10: prob
            	    {
            	    pushFollow(FOLLOW_prob_in_prob_matrix2075);
            	    prob30=prob();

            	    state._fsp--;


            	            	if (prob30 > 0.0) m.assign(index % i_max,index / i_max,prob30);
            	                index++;
            	            

            	    }
            	    break;

            	default :
            	    if ( cnt16 >= 1 ) break loop16;
                        EarlyExitException eee =
                            new EarlyExitException(16, input);
                        throw eee;
                }
                cnt16++;
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return m;
    }
    // $ANTLR end "prob_matrix"


    // $ANTLR start "prob_vector"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:487:1: prob_vector returns [SparseVector vector] : ( prob )+ ;
    public final SparseVector prob_vector() throws RecognitionException {
        SparseVector vector = null;

        double prob31 = 0.0;


        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:488:5: ( ( prob )+ )
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:490:9: ( prob )+
            {
            int index = 0; vector = new SparseVector(dotPomdpSpec.nrSta);
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:491:9: ( prob )+
            int cnt17=0;
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==INT||LA17_0==FLOAT) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:491:10: prob
            	    {
            	    pushFollow(FOLLOW_prob_in_prob_vector2148);
            	    prob31=prob();

            	    state._fsp--;


            	                // action here - the check for 0 actually doesn't matter
            	                if (prob31 > 0.0) vector.assign(index, prob31);
            	                index++;
            	            

            	    }
            	    break;

            	default :
            	    if ( cnt17 >= 1 ) break loop17;
                        EarlyExitException eee =
                            new EarlyExitException(17, input);
                        throw eee;
                }
                cnt17++;
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return vector;
    }
    // $ANTLR end "prob_vector"


    // $ANTLR start "num_matrix"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:500:1: num_matrix : ( number )+ ;
    public final void num_matrix() throws RecognitionException {
        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:501:5: ( ( number )+ )
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:501:12: ( number )+
            {

                 int index = 0;
                 //int i_max;
                 
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:505:9: ( number )+
            int cnt18=0;
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( ((LA18_0>=PLUSTOK && LA18_0<=MINUSTOK)||LA18_0==INT||LA18_0==FLOAT) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:505:10: number
            	    {
            	    pushFollow(FOLLOW_number_in_num_matrix2209);
            	    number();

            	    state._fsp--;


            	                index++;
            	            

            	    }
            	    break;

            	default :
            	    if ( cnt18 >= 1 ) break loop18;
                        EarlyExitException eee =
                            new EarlyExitException(18, input);
                        throw eee;
                }
                cnt18++;
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "num_matrix"

    public static class state_return extends ParserRuleReturnScope {
        public ArrayList<Integer> l = new ArrayList<Integer>();
    };

    // $ANTLR start "state"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:512:1: state returns [ArrayList<Integer> l = new ArrayList<Integer>()] : ( INT | STRING | ASTERICKTOK );
    public final DotPomdpParser.state_return state() throws RecognitionException {
        DotPomdpParser.state_return retval = new DotPomdpParser.state_return();
        retval.start = input.LT(1);

        Token INT32=null;
        Token STRING33=null;

        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:513:5: ( INT | STRING | ASTERICKTOK )
            int alt19=3;
            switch ( input.LA(1) ) {
            case INT:
                {
                alt19=1;
                }
                break;
            case STRING:
                {
                alt19=2;
                }
                break;
            case ASTERICKTOK:
                {
                alt19=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:514:9: INT
                    {
                    INT32=(Token)match(input,INT,FOLLOW_INT_in_state2260); 
                    retval.l.add(Integer.parseInt((INT32!=null?INT32.getText():null)));

                    }
                    break;
                case 2 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:517:9: STRING
                    {
                    STRING33=(Token)match(input,STRING,FOLLOW_STRING_in_state2288); 
                    retval.l.add(dotPomdpSpec.staList.indexOf((STRING33!=null?STRING33.getText():null)));

                    }
                    break;
                case 3 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:520:9: ASTERICKTOK
                    {
                    match(input,ASTERICKTOK,FOLLOW_ASTERICKTOK_in_state2315); 
                    for(int s=0; s<dotPomdpSpec.nrSta; s++) retval.l.add(s);

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "state"


    // $ANTLR start "paction"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:524:1: paction returns [ArrayList<Integer> l = new ArrayList<Integer>()] : ( INT | STRING | ASTERICKTOK );
    public final ArrayList<Integer> paction() throws RecognitionException {
        ArrayList<Integer> l =  new ArrayList<Integer>();

        Token INT34=null;
        Token STRING35=null;

        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:525:5: ( INT | STRING | ASTERICKTOK )
            int alt20=3;
            switch ( input.LA(1) ) {
            case INT:
                {
                alt20=1;
                }
                break;
            case STRING:
                {
                alt20=2;
                }
                break;
            case ASTERICKTOK:
                {
                alt20=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:526:9: INT
                    {
                    INT34=(Token)match(input,INT,FOLLOW_INT_in_paction2357); 
                    l.add(Integer.parseInt((INT34!=null?INT34.getText():null)));

                    }
                    break;
                case 2 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:529:9: STRING
                    {
                    STRING35=(Token)match(input,STRING,FOLLOW_STRING_in_paction2385); 
                    l.add(dotPomdpSpec.actList.indexOf((STRING35!=null?STRING35.getText():null)));

                    }
                    break;
                case 3 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:532:9: ASTERICKTOK
                    {
                    match(input,ASTERICKTOK,FOLLOW_ASTERICKTOK_in_paction2412); 
                    for(int a=0; a<dotPomdpSpec.nrAct; a++) l.add(a);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return l;
    }
    // $ANTLR end "paction"

    public static class obs_return extends ParserRuleReturnScope {
        public ArrayList<Integer> l = new ArrayList<Integer>();
    };

    // $ANTLR start "obs"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:536:1: obs returns [ArrayList<Integer> l = new ArrayList<Integer>()] : ( INT | STRING | ASTERICKTOK );
    public final DotPomdpParser.obs_return obs() throws RecognitionException {
        DotPomdpParser.obs_return retval = new DotPomdpParser.obs_return();
        retval.start = input.LT(1);

        Token INT36=null;
        Token STRING37=null;

        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:537:5: ( INT | STRING | ASTERICKTOK )
            int alt21=3;
            switch ( input.LA(1) ) {
            case INT:
                {
                alt21=1;
                }
                break;
            case STRING:
                {
                alt21=2;
                }
                break;
            case ASTERICKTOK:
                {
                alt21=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:538:9: INT
                    {
                    INT36=(Token)match(input,INT,FOLLOW_INT_in_obs2455); 
                    retval.l.add(Integer.parseInt((INT36!=null?INT36.getText():null)));

                    }
                    break;
                case 2 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:541:9: STRING
                    {
                    STRING37=(Token)match(input,STRING,FOLLOW_STRING_in_obs2483); 
                    retval.l.add(dotPomdpSpec.obsList.indexOf((STRING37!=null?STRING37.getText():null)));

                    }
                    break;
                case 3 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:544:9: ASTERICKTOK
                    {
                    match(input,ASTERICKTOK,FOLLOW_ASTERICKTOK_in_obs2510); 
                    for(int o=0; o<dotPomdpSpec.nrObs; o++) retval.l.add(o);

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "obs"


    // $ANTLR start "ident_list"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:548:1: ident_list returns [ArrayList<String> list] : ( STRING )+ ;
    public final ArrayList<String> ident_list() throws RecognitionException {
        ArrayList<String> list = null;

        Token STRING38=null;

        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:549:5: ( ( STRING )+ )
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:550:9: ( STRING )+
            {
            list = new ArrayList<String>();
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:551:9: ( STRING )+
            int cnt22=0;
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==STRING) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:551:10: STRING
            	    {
            	    STRING38=(Token)match(input,STRING,FOLLOW_STRING_in_ident_list2566); 
            	    list.add((STRING38!=null?STRING38.getText():null));

            	    }
            	    break;

            	default :
            	    if ( cnt22 >= 1 ) break loop22;
                        EarlyExitException eee =
                            new EarlyExitException(22, input);
                        throw eee;
                }
                cnt22++;
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return list;
    }
    // $ANTLR end "ident_list"


    // $ANTLR start "prob"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:556:1: prob returns [double p] : ( INT | FLOAT );
    public final double prob() throws RecognitionException {
        double p = 0.0;

        Token INT39=null;
        Token FLOAT40=null;

        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:557:5: ( INT | FLOAT )
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==INT) ) {
                alt23=1;
            }
            else if ( (LA23_0==FLOAT) ) {
                alt23=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:557:7: INT
                    {
                    INT39=(Token)match(input,INT,FOLLOW_INT_in_prob2614); 
                    p = Double.parseDouble((INT39!=null?INT39.getText():null));

                    }
                    break;
                case 2 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:559:7: FLOAT
                    {
                    FLOAT40=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_prob2632); 
                    p = Double.parseDouble((FLOAT40!=null?FLOAT40.getText():null));

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return p;
    }
    // $ANTLR end "prob"


    // $ANTLR start "number"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:563:1: number returns [double n] : ( optional_sign INT | optional_sign FLOAT );
    public final double number() throws RecognitionException {
        double n = 0.0;

        Token INT42=null;
        Token FLOAT44=null;
        int optional_sign41 = 0;

        int optional_sign43 = 0;


        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:564:5: ( optional_sign INT | optional_sign FLOAT )
            int alt24=2;
            switch ( input.LA(1) ) {
            case PLUSTOK:
                {
                int LA24_1 = input.LA(2);

                if ( (LA24_1==INT) ) {
                    alt24=1;
                }
                else if ( (LA24_1==FLOAT) ) {
                    alt24=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 24, 1, input);

                    throw nvae;
                }
                }
                break;
            case MINUSTOK:
                {
                int LA24_2 = input.LA(2);

                if ( (LA24_2==INT) ) {
                    alt24=1;
                }
                else if ( (LA24_2==FLOAT) ) {
                    alt24=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 24, 2, input);

                    throw nvae;
                }
                }
                break;
            case INT:
                {
                alt24=1;
                }
                break;
            case FLOAT:
                {
                alt24=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;
            }

            switch (alt24) {
                case 1 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:564:7: optional_sign INT
                    {
                    pushFollow(FOLLOW_optional_sign_in_number2675);
                    optional_sign41=optional_sign();

                    state._fsp--;

                    INT42=(Token)match(input,INT,FOLLOW_INT_in_number2677); 
                    n = optional_sign41 * Double.parseDouble((INT42!=null?INT42.getText():null));

                    }
                    break;
                case 2 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:566:7: optional_sign FLOAT
                    {
                    pushFollow(FOLLOW_optional_sign_in_number2696);
                    optional_sign43=optional_sign();

                    state._fsp--;

                    FLOAT44=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_number2698); 
                    n = optional_sign43 * Double.parseDouble((FLOAT44!=null?FLOAT44.getText():null));

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return n;
    }
    // $ANTLR end "number"


    // $ANTLR start "optional_sign"
    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:570:1: optional_sign returns [int s] : ( PLUSTOK | MINUSTOK | );
    public final int optional_sign() throws RecognitionException {
        int s = 0;

        try {
            // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:571:5: ( PLUSTOK | MINUSTOK | )
            int alt25=3;
            switch ( input.LA(1) ) {
            case PLUSTOK:
                {
                alt25=1;
                }
                break;
            case MINUSTOK:
                {
                alt25=2;
                }
                break;
            case INT:
            case FLOAT:
                {
                alt25=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;
            }

            switch (alt25) {
                case 1 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:571:7: PLUSTOK
                    {
                    match(input,PLUSTOK,FOLLOW_PLUSTOK_in_optional_sign2730); 
                    s = 1;

                    }
                    break;
                case 2 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:573:7: MINUSTOK
                    {
                    match(input,MINUSTOK,FOLLOW_MINUSTOK_in_optional_sign2748); 
                    s = -1;

                    }
                    break;
                case 3 :
                    // /home/mauricio/scm/libsdm/java/libsdm/pomdp/parser/DotPomdp.g:576:9: 
                    {
                    s = 1;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return s;
    }
    // $ANTLR end "optional_sign"

    // Delegated rules


    protected DFA11 dfa11 = new DFA11(this);
    protected DFA12 dfa12 = new DFA12(this);
    protected DFA13 dfa13 = new DFA13(this);
    static final String DFA11_eotS =
        "\13\uffff";
    static final String DFA11_eofS =
        "\13\uffff";
    static final String DFA11_minS =
        "\1\25\3\14\1\uffff\1\25\3\14\2\uffff";
    static final String DFA11_maxS =
        "\1\31\3\33\1\uffff\1\31\3\33\2\uffff";
    static final String DFA11_acceptS =
        "\4\uffff\1\3\4\uffff\1\1\1\2";
    static final String DFA11_specialS =
        "\13\uffff}>";
    static final String[] DFA11_transitionS = {
            "\1\3\2\uffff\1\2\1\1",
            "\2\4\6\uffff\1\5\4\uffff\1\4\1\uffff\1\4",
            "\2\4\6\uffff\1\5\4\uffff\1\4\1\uffff\1\4",
            "\2\4\6\uffff\1\5\4\uffff\1\4\1\uffff\1\4",
            "",
            "\1\10\2\uffff\1\7\1\6",
            "\1\12\6\uffff\1\12\1\11\4\uffff\1\12\1\uffff\1\12",
            "\1\12\6\uffff\1\12\1\11\4\uffff\1\12\1\uffff\1\12",
            "\1\12\6\uffff\1\12\1\11\4\uffff\1\12\1\uffff\1\12",
            "",
            ""
    };

    static final short[] DFA11_eot = DFA.unpackEncodedString(DFA11_eotS);
    static final short[] DFA11_eof = DFA.unpackEncodedString(DFA11_eofS);
    static final char[] DFA11_min = DFA.unpackEncodedStringToUnsignedChars(DFA11_minS);
    static final char[] DFA11_max = DFA.unpackEncodedStringToUnsignedChars(DFA11_maxS);
    static final short[] DFA11_accept = DFA.unpackEncodedString(DFA11_acceptS);
    static final short[] DFA11_special = DFA.unpackEncodedString(DFA11_specialS);
    static final short[][] DFA11_transition;

    static {
        int numStates = DFA11_transitionS.length;
        DFA11_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA11_transition[i] = DFA.unpackEncodedString(DFA11_transitionS[i]);
        }
    }

    class DFA11 extends DFA {

        public DFA11(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 11;
            this.eot = DFA11_eot;
            this.eof = DFA11_eof;
            this.min = DFA11_min;
            this.max = DFA11_max;
            this.accept = DFA11_accept;
            this.special = DFA11_special;
            this.transition = DFA11_transition;
        }
        public String getDescription() {
            return "305:1: trans_spec_tail : ( paction COLONTOK s_1= state COLONTOK s_2= state prob | paction COLONTOK state u_matrix | paction ui_matrix );";
        }
    }
    static final String DFA12_eotS =
        "\13\uffff";
    static final String DFA12_eofS =
        "\13\uffff";
    static final String DFA12_minS =
        "\1\25\3\14\1\25\1\uffff\3\14\2\uffff";
    static final String DFA12_maxS =
        "\1\31\3\33\1\31\1\uffff\3\33\2\uffff";
    static final String DFA12_acceptS =
        "\5\uffff\1\3\3\uffff\1\1\1\2";
    static final String DFA12_specialS =
        "\13\uffff}>";
    static final String[] DFA12_transitionS = {
            "\1\3\2\uffff\1\2\1\1",
            "\1\5\6\uffff\1\5\1\4\4\uffff\1\5\1\uffff\1\5",
            "\1\5\6\uffff\1\5\1\4\4\uffff\1\5\1\uffff\1\5",
            "\1\5\6\uffff\1\5\1\4\4\uffff\1\5\1\uffff\1\5",
            "\1\10\2\uffff\1\7\1\6",
            "",
            "\1\12\6\uffff\1\12\1\11\4\uffff\1\12\1\uffff\1\12",
            "\1\12\6\uffff\1\12\1\11\4\uffff\1\12\1\uffff\1\12",
            "\1\12\6\uffff\1\12\1\11\4\uffff\1\12\1\uffff\1\12",
            "",
            ""
    };

    static final short[] DFA12_eot = DFA.unpackEncodedString(DFA12_eotS);
    static final short[] DFA12_eof = DFA.unpackEncodedString(DFA12_eofS);
    static final char[] DFA12_min = DFA.unpackEncodedStringToUnsignedChars(DFA12_minS);
    static final char[] DFA12_max = DFA.unpackEncodedStringToUnsignedChars(DFA12_maxS);
    static final short[] DFA12_accept = DFA.unpackEncodedString(DFA12_acceptS);
    static final short[] DFA12_special = DFA.unpackEncodedString(DFA12_specialS);
    static final short[][] DFA12_transition;

    static {
        int numStates = DFA12_transitionS.length;
        DFA12_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA12_transition[i] = DFA.unpackEncodedString(DFA12_transitionS[i]);
        }
    }

    class DFA12 extends DFA {

        public DFA12(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 12;
            this.eot = DFA12_eot;
            this.eof = DFA12_eof;
            this.min = DFA12_min;
            this.max = DFA12_max;
            this.accept = DFA12_accept;
            this.special = DFA12_special;
            this.transition = DFA12_transition;
        }
        public String getDescription() {
            return "337:1: obs_spec_tail : ( paction COLONTOK state COLONTOK obs prob | paction COLONTOK state u_matrix | paction u_matrix );";
        }
    }
    static final String DFA13_eotS =
        "\17\uffff";
    static final String DFA13_eofS =
        "\17\uffff";
    static final String DFA13_minS =
        "\1\25\3\24\1\25\3\24\1\25\1\uffff\3\24\2\uffff";
    static final String DFA13_maxS =
        "\1\31\3\24\1\31\3\33\1\31\1\uffff\3\33\2\uffff";
    static final String DFA13_acceptS =
        "\11\uffff\1\3\3\uffff\1\2\1\1";
    static final String DFA13_specialS =
        "\17\uffff}>";
    static final String[] DFA13_transitionS = {
            "\1\3\2\uffff\1\2\1\1",
            "\1\4",
            "\1\4",
            "\1\4",
            "\1\7\2\uffff\1\6\1\5",
            "\1\10\1\uffff\2\11\1\uffff\1\11\1\uffff\1\11",
            "\1\10\1\uffff\2\11\1\uffff\1\11\1\uffff\1\11",
            "\1\10\1\uffff\2\11\1\uffff\1\11\1\uffff\1\11",
            "\1\14\2\uffff\1\13\1\12",
            "",
            "\1\16\1\uffff\2\15\1\uffff\1\15\1\uffff\1\15",
            "\1\16\1\uffff\2\15\1\uffff\1\15\1\uffff\1\15",
            "\1\16\1\uffff\2\15\1\uffff\1\15\1\uffff\1\15",
            "",
            ""
    };

    static final short[] DFA13_eot = DFA.unpackEncodedString(DFA13_eotS);
    static final short[] DFA13_eof = DFA.unpackEncodedString(DFA13_eofS);
    static final char[] DFA13_min = DFA.unpackEncodedStringToUnsignedChars(DFA13_minS);
    static final char[] DFA13_max = DFA.unpackEncodedStringToUnsignedChars(DFA13_maxS);
    static final short[] DFA13_accept = DFA.unpackEncodedString(DFA13_acceptS);
    static final short[] DFA13_special = DFA.unpackEncodedString(DFA13_specialS);
    static final short[][] DFA13_transition;

    static {
        int numStates = DFA13_transitionS.length;
        DFA13_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA13_transition[i] = DFA.unpackEncodedString(DFA13_transitionS[i]);
        }
    }

    class DFA13 extends DFA {

        public DFA13(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 13;
            this.eot = DFA13_eot;
            this.eof = DFA13_eof;
            this.min = DFA13_min;
            this.max = DFA13_max;
            this.accept = DFA13_accept;
            this.special = DFA13_special;
            this.transition = DFA13_transition;
        }
        public String getDescription() {
            return "369:1: reward_spec_tail : ( paction COLONTOK s_1= state COLONTOK s_2= state COLONTOK obs number | paction COLONTOK state COLONTOK state num_matrix | paction COLONTOK state num_matrix );";
        }
    }
 

    public static final BitSet FOLLOW_preamble_in_dotPomdp796 = new BitSet(new long[]{0x0000000000010E00L});
    public static final BitSet FOLLOW_start_state_in_dotPomdp814 = new BitSet(new long[]{0x0000000000000E00L});
    public static final BitSet FOLLOW_param_list_in_dotPomdp833 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_param_type_in_preamble869 = new BitSet(new long[]{0x00000000000001F2L});
    public static final BitSet FOLLOW_discount_param_in_param_type901 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_value_param_in_param_type909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_state_param_in_param_type917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_param_in_param_type925 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_obs_param_in_param_type933 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DISCOUNTTOK_in_discount_param953 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_discount_param955 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_FLOAT_in_discount_param957 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VALUESTOK_in_value_param987 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_value_param989 = new BitSet(new long[]{0x000000000000C000L});
    public static final BitSet FOLLOW_value_tail_in_value_param991 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REWARDTOK_in_value_tail1014 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COSTTOK_in_value_tail1022 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STATESTOK_in_state_param1055 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_state_param1057 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_state_tail_in_state_param1059 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_state_tail1082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ident_list_in_state_tail1109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTIONSTOK_in_action_param1150 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_action_param1152 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_action_tail_in_action_param1154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_action_tail1177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ident_list_in_action_tail1204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OBSERVATIONSTOK_in_obs_param1241 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_obs_param1243 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_obs_param_tail_in_obs_param1245 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_obs_param_tail1263 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ident_list_in_obs_param_tail1290 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STARTTOK_in_start_state1331 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_start_state1333 = new BitSet(new long[]{0x000000000A000000L});
    public static final BitSet FOLLOW_prob_vector_in_start_state1335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STARTTOK_in_start_state1362 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_start_state1364 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_STRING_in_start_state1366 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STARTTOK_in_start_state1384 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_INCLUDETOK_in_start_state1386 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_start_state1388 = new BitSet(new long[]{0x0000000003200000L});
    public static final BitSet FOLLOW_start_state_list_in_start_state1390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STARTTOK_in_start_state1409 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_EXCLUDETOK_in_start_state1411 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_start_state1413 = new BitSet(new long[]{0x0000000003200000L});
    public static final BitSet FOLLOW_start_state_list_in_start_state1415 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_state_in_start_state_list1463 = new BitSet(new long[]{0x0000000003200002L});
    public static final BitSet FOLLOW_param_spec_in_param_list1486 = new BitSet(new long[]{0x0000000000000E02L});
    public static final BitSet FOLLOW_trans_prob_spec_in_param_spec1509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_obs_prob_spec_in_param_spec1517 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_reward_spec_in_param_spec1526 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TTOK_in_trans_prob_spec1548 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_trans_prob_spec1550 = new BitSet(new long[]{0x0000000003200000L});
    public static final BitSet FOLLOW_trans_spec_tail_in_trans_prob_spec1552 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paction_in_trans_spec_tail1574 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_trans_spec_tail1576 = new BitSet(new long[]{0x0000000003200000L});
    public static final BitSet FOLLOW_state_in_trans_spec_tail1580 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_trans_spec_tail1582 = new BitSet(new long[]{0x0000000003200000L});
    public static final BitSet FOLLOW_state_in_trans_spec_tail1586 = new BitSet(new long[]{0x000000000A000000L});
    public static final BitSet FOLLOW_prob_in_trans_spec_tail1588 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paction_in_trans_spec_tail1616 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_trans_spec_tail1618 = new BitSet(new long[]{0x0000000003200000L});
    public static final BitSet FOLLOW_state_in_trans_spec_tail1620 = new BitSet(new long[]{0x000000000A081000L});
    public static final BitSet FOLLOW_u_matrix_in_trans_spec_tail1622 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paction_in_trans_spec_tail1641 = new BitSet(new long[]{0x000000000A083000L});
    public static final BitSet FOLLOW_ui_matrix_in_trans_spec_tail1643 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTOK_in_obs_prob_spec1681 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_obs_prob_spec1683 = new BitSet(new long[]{0x0000000003200000L});
    public static final BitSet FOLLOW_obs_spec_tail_in_obs_prob_spec1685 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paction_in_obs_spec_tail1704 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_obs_spec_tail1706 = new BitSet(new long[]{0x0000000003200000L});
    public static final BitSet FOLLOW_state_in_obs_spec_tail1708 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_obs_spec_tail1710 = new BitSet(new long[]{0x0000000003200000L});
    public static final BitSet FOLLOW_obs_in_obs_spec_tail1712 = new BitSet(new long[]{0x000000000A000000L});
    public static final BitSet FOLLOW_prob_in_obs_spec_tail1714 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paction_in_obs_spec_tail1741 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_obs_spec_tail1743 = new BitSet(new long[]{0x0000000003200000L});
    public static final BitSet FOLLOW_state_in_obs_spec_tail1745 = new BitSet(new long[]{0x000000000A081000L});
    public static final BitSet FOLLOW_u_matrix_in_obs_spec_tail1747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paction_in_obs_spec_tail1766 = new BitSet(new long[]{0x000000000A081000L});
    public static final BitSet FOLLOW_u_matrix_in_obs_spec_tail1768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RTOK_in_reward_spec1808 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_reward_spec1810 = new BitSet(new long[]{0x0000000003200000L});
    public static final BitSet FOLLOW_reward_spec_tail_in_reward_spec1812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paction_in_reward_spec_tail1830 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_reward_spec_tail1832 = new BitSet(new long[]{0x0000000003200000L});
    public static final BitSet FOLLOW_state_in_reward_spec_tail1836 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_reward_spec_tail1838 = new BitSet(new long[]{0x0000000003200000L});
    public static final BitSet FOLLOW_state_in_reward_spec_tail1842 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_reward_spec_tail1844 = new BitSet(new long[]{0x0000000003200000L});
    public static final BitSet FOLLOW_obs_in_reward_spec_tail1846 = new BitSet(new long[]{0x000000000AC00000L});
    public static final BitSet FOLLOW_number_in_reward_spec_tail1848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paction_in_reward_spec_tail1866 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_reward_spec_tail1868 = new BitSet(new long[]{0x0000000003200000L});
    public static final BitSet FOLLOW_state_in_reward_spec_tail1870 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_reward_spec_tail1872 = new BitSet(new long[]{0x0000000003200000L});
    public static final BitSet FOLLOW_state_in_reward_spec_tail1874 = new BitSet(new long[]{0x000000000AC00000L});
    public static final BitSet FOLLOW_num_matrix_in_reward_spec_tail1876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paction_in_reward_spec_tail1894 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_COLONTOK_in_reward_spec_tail1896 = new BitSet(new long[]{0x0000000003200000L});
    public static final BitSet FOLLOW_state_in_reward_spec_tail1898 = new BitSet(new long[]{0x000000000AC00000L});
    public static final BitSet FOLLOW_num_matrix_in_reward_spec_tail1900 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNIFORMTOK_in_ui_matrix1936 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTITYTOK_in_ui_matrix1952 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prob_matrix_in_ui_matrix1971 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNIFORMTOK_in_u_matrix1999 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RESETTOK_in_u_matrix2014 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prob_matrix_in_u_matrix2029 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prob_in_prob_matrix2075 = new BitSet(new long[]{0x000000000A000002L});
    public static final BitSet FOLLOW_prob_in_prob_vector2148 = new BitSet(new long[]{0x000000000A000002L});
    public static final BitSet FOLLOW_number_in_num_matrix2209 = new BitSet(new long[]{0x000000000AC00002L});
    public static final BitSet FOLLOW_INT_in_state2260 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_state2288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASTERICKTOK_in_state2315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_paction2357 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_paction2385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASTERICKTOK_in_paction2412 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_obs2455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_obs2483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASTERICKTOK_in_obs2510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_ident_list2566 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_INT_in_prob2614 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_prob2632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_optional_sign_in_number2675 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_INT_in_number2677 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_optional_sign_in_number2696 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_FLOAT_in_number2698 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUSTOK_in_optional_sign2730 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUSTOK_in_optional_sign2748 = new BitSet(new long[]{0x0000000000000002L});

}