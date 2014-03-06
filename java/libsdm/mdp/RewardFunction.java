package libsdm.mdp;

public interface RewardFunction {
	public abstract double min(int a);
	public abstract double max(int a);
	public abstract double max();
	public abstract double min();
	public boolean stationary();
	public abstract double get(int state, int action, int nstate, int i);
}
