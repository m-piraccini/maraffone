package maraffone.player;

import maraffone.card.Card;


public class PlayerCard {

	private boolean alreadyPlayed = false;
	private Card card;
	
	
	public PlayerCard(Card card) {
		this.card = card;
	}

	public boolean isAlreadyPlayed() {
		return alreadyPlayed;
	}

	public void setAlreadyPlayed(boolean alreadyPlayed) {
		this.alreadyPlayed = alreadyPlayed;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}
	
}
