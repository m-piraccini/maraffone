package evoca.learning.startegies.dqn;

import evoca.learning.common.Action;
import org.nd4j.linalg.api.ndarray.INDArray;


public class Replay<A extends Action> {

	INDArray state;
	A action;
	double reward;
	INDArray nextState;
	int[] nextActionMask ;
	
	public Replay(INDArray state , A action , double reward , INDArray nextState, int[] nextActionMask ){
		this.state = state;
		this.action = action;
		this.reward = reward;
		this.nextState = nextState;
		this.nextActionMask = nextActionMask ;
	}
	
}
