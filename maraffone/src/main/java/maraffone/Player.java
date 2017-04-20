package maraffone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import maraffone.Card.Suit;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class Player {

	private String name;
	private List<PlayerCard> playerHand = new ArrayList<PlayerCard>();
	private Player nextPlayer;
	private List<Card> wonCardsStack;
	private Player mate;
	
	private Double alpha = 0.5;
	private Double gamma = 0.5;
	
	
	Table<String, Card, Double> Q = HashBasedTable.create();
	private PlayerCard currentAction;
	private String currentState;
	private String oldState;
	
	
	public Player()
	{
		
	}
	
	
	public Player(Table<String, Card, Double> Q)
	{
		this.Q = Q;
	}
	
	
	
	public void initializeHand(List<Card> subList) {
		playerHand = new ArrayList<PlayerCard>();
		for (Card card : subList) {
			playerHand.add(new PlayerCard(card));
		}
		
		// TODO: init Learning
		for (Card card : subList) {
			String hash = getHash(getOrderedHand(getCurrentCards()));
//			Q.put(hash, card , 0d);
//			System.out.println( this.getName() + " - hashing: " + hash + " : " + card.toShortString() );
		}
		
	}
	
	
	/**
	 * 
	 * @return current available cards for a player
	 */
	public List<Card> getCurrentCards()
	{
		return playerHand.stream().filter(c -> !c.isAlreadyPlayed()).map(c -> c.getCard()).collect(Collectors.toCollection(ArrayList::new));
	}
	
	
	public String getHash(List<Card> cardList)
	{
		String hash = "";
		for (Card card : cardList) {
			hash = hash.concat(card.toShortString()).concat(":");
		}
		hash = hash.substring(0, hash.length() > 0 ? hash.length()-1 : 0);
		return hash;
	}
	
	
	public List<Card> getOrderedHand(List<Card> currentCards)
	{
		Collections.sort(currentCards, new CardComparator());
		return currentCards;
	}
	

	
	public Suit chooseBriscola()
	{	
//		System.out.println("Briscola Is " + Suit.HEARTS);
		// TODO: return correct briscola; 
		return Suit.HEARTS;
	}
	
	public Card trowCard(Match match, ArrayList<CardOnTable> table, int t, int zrow) 
	{
		oldState = getHash(getOrderedHand(getCurrentCards()));
		PlayerCard action = selectCardToPlay(table, playerHand);
		action.setAlreadyPlayed(true);
//		System.out.println("" + name + " played " + action.getCard().toShortString());
		currentState = getHash(getOrderedHand(getCurrentCards()));
//		System.out.println(this.getName() + " currentstate: " + currentState);
		return action.getCard();
		
	}


	
	private PlayerCard selectCardToPlay(ArrayList<CardOnTable> table, List<PlayerCard> hand) 
	{
		ArrayList<PlayerCard> actionsFromState = actionsFromState(table, hand);
		
		PlayerCard action = actionsFromState.stream().findAny().get();
		
		currentAction = action;
		
		return action;
		
	}


	public void updateTurnReward(double calculatedTurnReward, Turn turn) {
		//get the max Q value of the resulting state if it's not terminal, 0 otherwise
		double maxQ = turn.getTurnNumber() == MarafoneConstants.TURNS_NUMBER ? 0. : this.maxQ(currentState);

		//update the old Q-value
		Double oldQ;
		if (Q.contains(oldState, currentAction.getCard()))
		{
			oldQ = Q.get(oldState, currentAction.getCard());
		}
		else
		{
			oldQ = 0d;
			Q.put(oldState, currentAction.getCard(),oldQ);
		}
		oldQ = oldQ + this.alpha * (calculatedTurnReward + this.gamma * maxQ - oldQ);
		
		Q.put(oldState, currentAction.getCard(), oldQ);

	}
	

	public double maxQ(String s) {
//		System.out.println(this.getName() + " - retrieving actions for state: " + s );
		Map<Card, Double> row = Q.row(s);
		if (row.isEmpty())
		{
			return 0;
		}
		return row.values().stream().max(Double::compare).get();
	}
	

	private ArrayList<PlayerCard> actionsFromState(ArrayList<CardOnTable> table, List<PlayerCard> hand) 
	{
		if (table.isEmpty())
		{
			return hand.stream().filter(c -> !c.isAlreadyPlayed() ).collect(Collectors.toCollection(ArrayList::new));
		}
		
		ArrayList<PlayerCard> findSuitMatchingCard = hand.stream().filter(c -> !c.isAlreadyPlayed() && c.getCard().suit().equals(table.get(0).getCard().suit()) ).collect(Collectors.toCollection(ArrayList::new));
		if (findSuitMatchingCard.size() > 0)
		{
			return findSuitMatchingCard;
		}
		else
		{
			ArrayList<PlayerCard> findAnyCard = hand.stream().filter(c -> !c.isAlreadyPlayed() ).collect(Collectors.toCollection(ArrayList::new));
			return findAnyCard;
		}
	}


	public List<PlayerCard> getHand() {
		return playerHand;
	}

	public void setHand(List<PlayerCard> hand) {
		this.playerHand = hand;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public Player getNextPlayer() {
		return nextPlayer;
	}


	public void setNextPlayer(Player nextPlayer) {
		this.nextPlayer = nextPlayer;
	}



	public List<Card> getWinnedCardsStack() {
		return wonCardsStack;
	}



	public void setWinnedCardsStack(List<Card> winnedCardsStack) {
		this.wonCardsStack = winnedCardsStack;
	}


	public Player getMate() {
		return mate;
	}


	public void setMate(Player mate) {
		this.mate = mate;
	}



	public void printQLearning() {
		try {
			File file = new File("q.txt");
			FileWriter bw = new FileWriter(file);
			
		
			System.out.println("");
			System.out.println("Printing Q for Player: " + getName());
			Set<String> rowKeySet = Q.rowKeySet();
			for (String hash : rowKeySet) {
				Map<Card, Double> row = Q.row(hash);
				bw.write(hash + "|");
				for (Card action : row.keySet()) {
					bw.write( "\t" + BigDecimal.valueOf(row.get(action)).setScale(2,RoundingMode.HALF_DOWN).toString() + ";"); 
				}
				bw.write("\n");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public void clearPlayer() {
		playerHand.clear();
		wonCardsStack.clear();
		
	}

	
}
