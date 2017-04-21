package maraffone.table;

import maraffone.card.CardOnTable;
import maraffone.player.Player;

import java.util.List;

public class Turn {

	private Player firstPlayer;
	private List<CardOnTable> playedCards;
	private Player winnerPlayer;
	private int turnNumber;
	
	private double getPointsInTurn()
	{
		return playedCards.stream().mapToDouble(pc -> pc.getCard().pointsValue()).sum();
	}
	
	public Player getFirstPlayer() {
		return firstPlayer;
	}
	public void setFirstPlayer(Player firstPlayer) {
		this.firstPlayer = firstPlayer;
	}
	public List<CardOnTable> getPlayedCards() {
		return playedCards;
	}
	public void setPlayedCards(List<CardOnTable> playedCards) {
		this.playedCards = playedCards;
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
	
}
