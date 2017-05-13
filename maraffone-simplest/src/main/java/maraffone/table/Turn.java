package maraffone.table;

import maraffone.card.CardOnTable;
import maraffone.player.Player;

import java.util.List;
import java.util.Map;


public class Turn {

	private Player firstPlayer;
	private Map<String, CardOnTable> playedCards;
	private Player winnerPlayer;
	private int turnNumber;
	
	private double getPointsInTurn()
	{
		return playedCards.values().stream().mapToDouble(pc -> pc.getCard().pointsValue()).sum();
	}
	
	public Player getFirstPlayer() {
		return firstPlayer;
	}
	public void setFirstPlayer(Player firstPlayer) {
		this.firstPlayer = firstPlayer;
	}


	public Player getWinnerPlayer() {
		return winnerPlayer;
	}

	public void setWinnerPlayer(Player winnerPlayer) {
		this.winnerPlayer = winnerPlayer;
	}

	public int getTurnNumber() {
		return turnNumber;
	}

	public void setTurnNumber(int turnNumber) {
		this.turnNumber = turnNumber;
	}

	public void setPlayedCards(Map<String, CardOnTable> playedCards) {
		this.playedCards = playedCards;
	}

	public Map<String, CardOnTable> getPlayedCards() {
		return this.playedCards;
	}
}
