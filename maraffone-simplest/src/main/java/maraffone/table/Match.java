package maraffone.table;

import java.util.ArrayList;
import java.util.List;

import maraffone.card.Card;
import maraffone.card.Card.Suit;
import maraffone.player.Player;
import maraffone.player.QPlayer;


public class Match {
	
	private Player startPlayer;
	private List<Turn> turns;
	private Suit briscola;
	private List<Card> winnerCardStack1;
	private List<Card> winnerCardStack2;
	
	public Match()
	{
		turns = new ArrayList<Turn>(); 
	}
	
	
	public Player getStartPlayer() {
		return startPlayer;
	}
	public void setStartPlayer(Player startPlayer) {
		this.startPlayer = startPlayer;
	}
	public List<Turn> getTurns() {
		return turns;
	}
	public void setTurns(List<Turn> turns) {
		this.turns = turns;
	}


	public Suit getBriscola() {
		return briscola;
	}


	public void setBriscola(Suit briscola) {
		this.briscola = briscola;
	}


	public List<Card> getWinnerCardStack1() {
		return winnerCardStack1;
	}


	public void setWinnerCardStack1(List<Card> winnerCardStack1) {
		this.winnerCardStack1 = winnerCardStack1;
	}


	public List<Card> getWinnerCardStack2() {
		return winnerCardStack2;
	}


	public void setWinnerCardStack2(List<Card> winnerCardStack2) {
		this.winnerCardStack2 = winnerCardStack2;
	}


	public void clearMatch() {
		startPlayer = null;
		turns.clear();
		briscola = null;
		winnerCardStack1.clear();
		winnerCardStack2.clear();
		
	}
	
}
