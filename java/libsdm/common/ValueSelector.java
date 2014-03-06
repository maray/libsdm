package libsdm.common;

import java.util.ArrayList;

public class ValueSelector {
	
	public static final boolean MAX=true;
	public static final boolean MIN=false;
	
	protected boolean type;
	double best;
	ArrayList<Object> list;
	
	public ValueSelector(boolean type){
		this.type=type;
		if (type)
			best=Double.NEGATIVE_INFINITY;
		else
			best=Double.POSITIVE_INFINITY;
		list=new ArrayList<Object>();
	}
	
	public void put(Object rep,double val){
		if (type){
			if (val>best){
				list.clear();
				best=val;
			}
		}
		else
		{
			if (val<best){
				list.clear();
				best=val;
			}
		}
		if (val==best){
			list.add(rep);
		}
		
	}
	
	public int size(){
		return list.size();
	}
	
	public Object get(int num){
		return list.get(num);
	}
	
	public Object getRandom(){
		//System.out.println(list.size());
		int idx=Utils.gen.nextInt(list.size());
		return (list.get(idx));
	}

	public double getBest(){
		return best;
	}
	
	
}
