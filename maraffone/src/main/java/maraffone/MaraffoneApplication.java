package maraffone;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import maraffone.Card.Suit;

public class MaraffoneApplication {

	
	private ArrayList<Card> newDeck;
	private ArrayList<Player> players;
	private ArrayList<CardOnTable> table;
	private Match match;
	Table<String, Card, Double> Q = HashBasedTable.create();

	public MaraffoneApplication() {
		initializeMatch();
		initializePlayers();

	}

	private void initializeMatch() {
		match = new Match();
		List<Card> wonStacks1 = new ArrayList<Card>();
		List<Card> wonStacks2 = new ArrayList<Card>();
		
		match.setWinnerCardStack1(wonStacks1);
		match.setWinnerCardStack2(wonStacks2);
	}

	private void initializePlayers() {
		players = new ArrayList<>();
		Player player;
		for (int p = 0; p < MarafoneConstants.PLAYER_NUMBER; p++) {
			player = new Player(Q);
			player.setName("P" + p);
			players.add(player);
		}

		for (int p = 0; p < MarafoneConstants.PLAYER_NUMBER; p++) {
			players.get(p).setNextPlayer(
					players.get((p + 1) % MarafoneConstants.PLAYER_NUMBER));
			players.get(p).setMate(
					players.get((p + 2) % MarafoneConstants.PLAYER_NUMBER));
		}

		players.get(0).setWinnedCardsStack(match.getWinnerCardStack1());
		players.get(2).setWinnedCardsStack(match.getWinnerCardStack1());
		players.get(1).setWinnedCardsStack(match.getWinnerCardStack2());
		players.get(3).setWinnedCardsStack(match.getWinnerCardStack2());
		
	}

	private void go() {
		
		long start = System.currentTimeMillis();
		long time = 0;
		long end = System.currentTimeMillis();
		
		for (int i = 0; i < MarafoneConstants.MATCHES_LEARNING_TRIALS; i++) {
			if (i % 20000 == 0){
				end = System.currentTimeMillis();
				time = (end-start)/1000;
				start = end;
				System.out.println("trial " + i + ": " + time + " secs");
			}
			match.clearMatch();
			players.forEach(p -> p.clearPlayer());
			
			newDeck = Card.newDeck();
			
			distributeCards(players, newDeck);
	
			Player firstPlayer = defineFirstPlayerForMatch(players);
			match.setStartPlayer(firstPlayer);
	
			for (int t = 0; t < MarafoneConstants.TURNS_NUMBER; t++) 
			{
				table = new ArrayList<CardOnTable>(MarafoneConstants.PLAYER_NUMBER);
				Turn turn = new Turn();
				turn.setTurnNumber(t);
				turn.setFirstPlayer(firstPlayer);
	
				Player currentPlayer = firstPlayer;
				match.setBriscola( firstPlayer.chooseBriscola() );
				for (int trow = 0; trow < MarafoneConstants.THROWS_NUMBER; trow++) {
					CardOnTable trowedCard = new CardOnTable();
					trowedCard.setCard(currentPlayer.trowCard(match, table, t, trow));
					trowedCard.setPlayer(currentPlayer);
					table.add( trowedCard);
					currentPlayer = currentPlayer.getNextPlayer();
				}
				turn.setPlayedCards(table);
				match.getTurns().add(turn);
				firstPlayer = defineTurnWinner(turn, match.getBriscola());
				firstPlayer.getWinnedCardsStack().addAll(
						turn.getPlayedCards().stream().map(pc -> pc.getCard()).collect(Collectors.toCollection(ArrayList::new)     )) ;
				
				
				// calc reward: updates Q
				double calculateTurnReward = calculateTurnReward(turn);
				firstPlayer.updateTurnReward( calculateTurnReward, turn );
				firstPlayer.getMate().updateTurnReward( calculateTurnReward, turn );
				
				firstPlayer.getNextPlayer().updateTurnReward( -calculateTurnReward, turn );
				firstPlayer.getNextPlayer().getMate().updateTurnReward( -calculateTurnReward, turn );
				
			}
	
			countPoints(players.get(0),match);
			countPoints(players.get(1),match);
			
		}
		
		System.out.println("simulation running: " + (end-start)/1000);
		
		System.out.println("match ended!");
		printMatch(match);
		players.stream().findFirst().get().printQLearning();
		
	}

	
	private double calculateTurnReward(Turn turn) {
		return (turn.getTurnNumber()==MarafoneConstants.LAST_TURN? 1d : 0d ) + turn.getPlayedCards().stream().mapToDouble(c -> c.getCard().pointsValue()).sum();
	}

	
	private void printMatch(Match match) {
		for (Turn turn : match.getTurns()) {
			for (CardOnTable card : turn.getPlayedCards()) {
				System.out.print(card.getPlayer().getName() + ":"+ card.getCard().toShortString()+ " ");
			}
			System.out.println();
		}
	}

	
	private void countPoints(Player p, Match m) 
	{
		double points = Math.floor(p.getWinnedCardsStack().stream().mapToDouble(c -> c.pointsValue()).sum());
		if( m.getTurns().get( MarafoneConstants.LAST_TURN ).getWinnerPlayer().equals(p)
				|| m.getTurns().get( MarafoneConstants.LAST_TURN ).getWinnerPlayer().equals(p.getMate()))
		{
			points = points+1d ; 
		}
//		System.out.println( p.getName() + " points: " + points );
	}

	private Player defineTurnWinner(Turn turn, Suit briscola) 
	{
		List<CardOnTable> playedCards = turn.getPlayedCards();
		CardOnTable currentWinnerPlayedCard = playedCards.get(0);
		for (int i = 1; i < playedCards.size(); i++) 
		{
			currentWinnerPlayedCard = findWinnerCard(playedCards.get(i), currentWinnerPlayedCard, briscola);
		}
		Player winnerPlayer = currentWinnerPlayedCard.getPlayer();
		turn.setWinnerPlayer(winnerPlayer);
		return winnerPlayer;
	}

	
	private CardOnTable findWinnerCard(CardOnTable nextCard, CardOnTable currentWinnerPlayedCard, Suit briscola) 
	{
		if ( 
			( nextCard.getCard().suit().equals(currentWinnerPlayedCard.getCard().suit()) 
			&& nextCard.getCard().rankPriority() > currentWinnerPlayedCard.getCard().rankPriority() )
			||
			( nextCard.getCard().suit().equals(briscola) && !currentWinnerPlayedCard.getCard().suit().equals(briscola) )) 
		{
			return nextCard;
		} 
		else 
		{
			return currentWinnerPlayedCard;
		}
	}

	
	
	private Player defineFirstPlayerForMatch(ArrayList<Player> players) {
		return players.get(0);
	}

	private void distributeCards(ArrayList<Player> players,
			ArrayList<Card> newDeck2) {
		int i = 0;
		for (Player player : players) {
			player.initializeHand(newDeck
					.subList(i*MarafoneConstants.PLAYER_HAND, i*MarafoneConstants.PLAYER_HAND + MarafoneConstants.PLAYER_HAND));
			i++;
		}

	}

	public static void main(String[] args) {

		new MaraffoneApplication().go();

	}

}
