package maraffone;

import java.util.List;

public class PlayingState {

	private List<Card> currentHand;

	public List<Card> getCurrentHand() {
		return currentHand;
	}

	public void setCurrentHand(List<Card> currentHand) {
		this.currentHand = currentHand;
	}
	
	
	public String getHash()
	{
		return null;
	}
	
	
}
