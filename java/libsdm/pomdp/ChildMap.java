package libsdm.pomdp;

public class ChildMap {
	boolean map[][];
	int actions;
	int observations;
	
	public ChildMap(int actions,int observations){
		this.actions=actions;
		this.observations=observations;
		map=new boolean[actions][observations];
	}

	public boolean get(int a, int o){
		return(map[a][o]);
	}

	public void set(int a, int o) {
		map[a][o]=true;
	}
	
}
