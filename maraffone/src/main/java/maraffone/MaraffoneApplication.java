package maraffone;


import maraffone.card.Card;
import maraffone.card.CardOnTable;
import maraffone.constants.MaraffoneConstants;
import maraffone.constants.MaraffoneParameters;
import maraffone.player.Player;
import maraffone.player.QPlayer;
import maraffone.player.RandomPlayer;
import maraffone.knowledge.QForPlayers;
import maraffone.table.Match;
import maraffone.table.Turn;
import maraffone.utils.MaraffoneUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class MaraffoneApplication {

   private final static Logger LOG = Logger.getLogger(MaraffoneApplication.class);

   private ArrayList<Card> newDeck;
   private List<Player> players;
   private ArrayList<CardOnTable> table;
   private Match match;
   private Player firstMatchPlayer = null;

   long start;
   long end;
   long time;


   private void go() {

      start = System.currentTimeMillis();
      time = 0;
      end = System.currentTimeMillis();


      // matches to play?
      for (int match_number = 0; match_number < MaraffoneParameters.MATCHES_LEARNING_TRIALS; match_number++) {
         sampling(match_number);

         prepareForNewMatch();

         firstMatchPlayer = defineFirstPlayerForMatch(firstMatchPlayer, players);
         Player firstPlayer = firstMatchPlayer;

         match.setStartPlayer(firstPlayer);
         match.setBriscola(firstPlayer.chooseBriscola());

         // there are 10 turns in a match
         for (int t = 0; t < MaraffoneConstants.TURNS_NUMBER; t++) {
            table = new ArrayList<>(MaraffoneConstants.PLAYER_NUMBER);
            Turn turn = new Turn();
            turn.setTurnNumber(t);
            turn.setFirstPlayer(firstPlayer);
            Player currentPlayer = firstPlayer;

            // there are 4 throws in a turn
            for (int trow = 0; trow < MaraffoneConstants.THROWS_NUMBER; trow++) {
               CardOnTable trowedCard = new CardOnTable();
               trowedCard.setCard(currentPlayer.trowCard(match, table, t, trow));
               trowedCard.setPlayer(currentPlayer);
               table.add(trowedCard);
               currentPlayer = currentPlayer.getNextPlayer();
            }
            turn.setPlayedCards(table);
            match.getTurns().add(turn);
            firstPlayer = MaraffoneUtils.defineTurnWinner(turn, match.getBriscola());
            firstPlayer.getWinnedCardsStack().addAll(
                  turn.getPlayedCards().stream().map(pc -> pc.getCard()).collect(Collectors.toCollection(ArrayList::new)));


            // calc reward: updates Q
            double calculateTurnReward = MaraffoneUtils.calculateTurnReward(turn);
            firstPlayer.updateTurnReward(calculateTurnReward, turn);
            firstPlayer.getMate().updateTurnReward(calculateTurnReward, turn);

            firstPlayer.getNextPlayer().updateTurnReward(-calculateTurnReward, turn);
            firstPlayer.getNextPlayer().getMate().updateTurnReward(-calculateTurnReward, turn);

         }

         players.get(0).setCumulatedPointsInMatches(players.get(0).getCumulatedPointsInMatches() + MaraffoneUtils.countPoints(players.get(0), match));
         players.get(1).setCumulatedPointsInMatches(players.get(1).getCumulatedPointsInMatches() + MaraffoneUtils.countPoints(players.get(1), match));

//         MaraffoneUtils.printMatch(match);
      }

      LOG.info("simulation running: " + (end - start) / 1000);
      LOG.info("match ended!");
      LOG.info("Cumulated points p0: " + players.get(0).getCumulatedPointsInMatches());
      LOG.info("Cumulated points p1: " + players.get(1).getCumulatedPointsInMatches());

      QForPlayers.getInstance().printLearning();

   }


   private void sampling(int matchNumber) {
      if (matchNumber % MaraffoneParameters.MATCHING_SAMPLING == 0) {
         end = System.currentTimeMillis();
         time = (end - start) / 1000;
         start = end;
         LOG.info("trial " + matchNumber + ": " + time + " secs");
      }
   }


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

      players = MaraffoneParameters.createAndConfigurePlayers();
      establishPlayerTurnAndMates();
      setPlayersWinnerStack();
   }


   private void setPlayersWinnerStack() {
      players.get(0).setWinnedCardsStack(match.getWinnerCardStack1());
      players.get(2).setWinnedCardsStack(match.getWinnerCardStack1());
      players.get(1).setWinnedCardsStack(match.getWinnerCardStack2());
      players.get(3).setWinnedCardsStack(match.getWinnerCardStack2());
   }


   private void establishPlayerTurnAndMates() {
      for (int p = 0; p < MaraffoneConstants.PLAYER_NUMBER; p++) {
         players.get(p).setNextPlayer(players.get((p + 1) % MaraffoneConstants.PLAYER_NUMBER));
         players.get(p).setMate(players.get((p + 2) % MaraffoneConstants.PLAYER_NUMBER));
      }
   }



   private void prepareForNewMatch() {
      match.clearMatch();
      players.forEach(p -> p.clearPlayer());
      newDeck = Card.newDeck();
      MaraffoneUtils.distributeCards(players, newDeck);
   }


   /**
    * returns always the subsequent player for fairness
    * @param firstPlayer
    * @param players
    * @return
    */
   private Player defineFirstPlayerForMatch(Player firstPlayer, List<Player> players) {
      return firstPlayer != null ? firstPlayer.getNextPlayer() : players.get(0);
   }


   public static void main(String[] args) {


      new MaraffoneApplication().go();

   }

}
