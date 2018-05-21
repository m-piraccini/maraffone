package maraffone.knowledge;

import maraffone.card.Card;
import org.nd4j.linalg.api.ndarray.INDArray;

public class Replay {

	INDArray state;
	Card action;
	double reward;
	INDArray nextState;
	int[] nextActionMask ;
	
	public Replay(INDArray state , Card action , double reward , INDArray nextState, int[] nextActionMask ){
		this.state = state;
		this.action = action;
		this.reward = reward;
		this.nextState = nextState;
		this.nextActionMask = nextActionMask ;
	}
	
}
