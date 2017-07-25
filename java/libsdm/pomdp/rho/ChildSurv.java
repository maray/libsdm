package libsdm.pomdp.rho;

import libsdm.common.SparseVector;
import libsdm.mdp.TransitionMatrix;
import libsdm.pomdp.BeliefReward;
import libsdm.pomdp.ObservationMatrix;
import libsdm.pomdp.Pomdp;
import libsdm.pomdp.plain.DensePomdp;

public class ChildSurv {


	static public final int RTYPE_STATE = 1;
	static public final int RTYPE_ENTROPY = 2;
	static public final int RTYPE_LINEAR = 3;
	static public final int RTYPE_VARIANCE = 4;

	static public final int STAY=0; 
	static public final int LEFT=1;
	static public final int RIGHT=2;
	
	static public final double oProb=0.9;
	static public final double oProbDark=0.3;
	static public final double mProb=0.9;

	static public Pomdp getPomdp(int type, int rooms, int childs){
		int nCodes=(int) Math.pow(rooms, childs);
		SparseVector tMat[][] = new SparseVector[nCodes*rooms][3];
		SparseVector oMat[][] = new SparseVector[nCodes*rooms][3];
		for (int cam=0;cam<rooms;cam++){
			int left=(cam>0)?cam-1:cam;
			int right=(cam<rooms-1)?cam+1:cam;
			SparseVector cv;
			SparseVector ov;
			for (int chCode=0;chCode < nCodes ;chCode++){
				//int nCh;
				int state=cam*nCodes + chCode;
				
				cv=childMove(chCode,cam,rooms,childs);
				tMat[state][STAY]=cv;
				cv=childMove(chCode,left,rooms,childs);
				tMat[state][LEFT]=cv;
				cv=childMove(chCode,right,rooms,childs);
				tMat[state][RIGHT]=cv;
				
				//nCh=childCount(chCode,cam,rooms,childs);
				//ov=binopdf(nCh,childs);
				ov=new SparseVector((int)Math.pow(2, childs));
				observVector(0,1.0,0,chCode,cam,rooms,childs,ov);
				oMat[state][0]=ov.copy();
				oMat[state][1]=ov.copy();
				oMat[state][2]=ov.copy();
			}
		}
		SparseVector initialBelief=new SparseVector(nCodes*rooms);
		initialBelief.assign(0, 1.0);
		return new DensePomdp(new ObservationMatrix(oMat), new TransitionMatrix(tMat), (BeliefReward) new ChildSurvReward(type,rooms,childs),
				nCodes*rooms, 3, (int)Math.pow(2, childs), 0.95, null, null,
				null, initialBelief);
	}
	
	private static void observVector(int ch,double prob,int obs,int chCode, int cam, int rooms,
			int childs, SparseVector vect) {
		if (ch == childs ){
			vect.assign(obs, vect.get(obs) + prob);
			return;
		}
		double myProb=oProbDark;
		if (cam== 0 || cam==rooms-1)
			myProb=oProb;
		int nPos = chCode%rooms;
		chCode=chCode/rooms;
		if (nPos==cam){
			observVector(ch+1,prob*myProb,obs + (int)Math.pow(2, ch),chCode,cam,rooms,childs,vect);
			observVector(ch+1,prob*(1-myProb),obs,chCode,cam,rooms,childs,vect);
		}
		else
		{
			observVector(ch+1,prob,obs,chCode,cam,rooms,childs,vect);
		}
		
	}

	/*private static SparseVector binopdf(int nCh,int childs) {
		SparseVector ret=new SparseVector(childs+1);
		for (int i=0;i<=nCh;i++){
			double val=binomialCoefficient(nCh,i)*Math.pow(oProb,i)*Math.pow(1 - oProb, nCh-i);
			ret.assign(i,val);
		}
		return ret	;
	}
	*/
	
	
    public static long binomialCoefficient(int n, int r) {
        long t = 1;
        
        int m = n - r; // r = Math.max(r, n - r);
        if (r < m) {
            r = m;
        }
        
        for (int i = n, j = 1; i > r; i--, j++) {
            t = t * i / j;
        }
        
        return t;
    }
    
	/*private static int childCount(int chCode, int cam, int rooms, int childs) {
		int count=0;
		for (int ch=1;ch<=childs;ch++){
			int nPos = chCode%rooms;
			chCode=chCode/rooms;
			if (nPos==cam)
				count++;
		}
		return count;
	}
	*/

	private static SparseVector childMove(int chCode, int s, int rooms,
			int childs) {
		int nCodes = (int) Math.pow(rooms, childs);
		SparseVector sv = new SparseVector(nCodes*rooms);
		int ini=s*nCodes;
		for (int np=0;np<nCodes;np++){
			double prob=1.0;
			int before = chCode;
			int now = np;
			for (int ch=1;ch<=childs;ch++){
				int bPos = before%rooms;
				before=before/rooms;
				int nPos = now%rooms;
				now=now/rooms;
				if (Math.abs(bPos - nPos) > 1){
					prob=0;
					break;
				}
				if (bPos==nPos){
					if (bPos==0 || bPos==rooms-1)
						prob*= (mProb + (1-mProb)/2);
					else
						prob*=mProb;
				}
				else
					prob*=(1-mProb)/2;
			}
			if (prob!=0)
				sv.assign(ini + np, prob);
		}
		return sv;
	}
		
		
	
}
