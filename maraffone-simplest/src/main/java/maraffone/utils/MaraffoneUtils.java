package maraffone.utils;

import maraffone.card.Card;
import maraffone.card.CardOnTable;
import maraffone.constants.MaraffoneConstants;
import maraffone.player.Player;
import maraffone.table.Match;
import maraffone.table.Turn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 */
public class MaraffoneUtils {

   private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MaraffoneUtils.class);

   /**
    * find the card that is the table winner at a certain moment
    *
    * @param nextCard
    * @param currentWinnerPlayedCard
    * @param briscola
    * @return
    */
   public static CardOnTable findWinnerCard(CardOnTable nextCard, CardOnTable currentWinnerPlayedCard, Card.Suit briscola) {
      if ((nextCard.getCard().suit().equals(currentWinnerPlayedCard.getCard().suit())
            && nextCard.getCard().rankPriority() > currentWinnerPlayedCard.getCard().rankPriority()) || (
            nextCard.getCard().suit().equals(briscola) && !currentWinnerPlayedCard.getCard().suit().equals(briscola))) {
         return nextCard;
      }
      else {
         return currentWinnerPlayedCard;
      }
   }


   /**
    *
    * @param players
    * @param newDeck2
    */
   public static void distributeCards(List<Player> players, List<Card> newDeck2) {
      int i = 0;
      for (Player player : players) {
         player.initializeHand(newDeck2
               .subList(i * MaraffoneConstants.PLAYER_HAND, i * MaraffoneConstants.PLAYER_HAND + MaraffoneConstants.PLAYER_HAND));
         i++;
      }

   }



   public static double calculateTurnReward(Turn turn) {
      return (turn.getTurnNumber() == MaraffoneConstants.LAST_TURN ?
            MaraffoneConstants.LAST_POINT_GAINED :
            MaraffoneConstants.LAST_POINT_LOST) + turn.getPlayedCards().values().stream().mapToDouble(c -> c.getCard().pointsValue())
            .sum();
   }


   public static void printMatch(Match match) {
      for (Turn turn : match.getTurns()) {
         for (CardOnTable card : turn.getPlayedCards().values()) {
            LOG.info(card.getPlayer().getName() + ":" + card.getCard().toShortString() + " ");
         }
         LOG.info("\n");
      }
   }


   public static double countPoints(Player p, Match m) {
      double points = Math.floor(p.getWinnedCardsStack().stream().mapToDouble(c -> c.pointsValue()).sum());
      Player lastTurnWinner = m.getTurns().get(MaraffoneConstants.LAST_TURN).getWinnerPlayer();
      if (lastTurnWinner.equals(p) || lastTurnWinner.equals(p.getMate())) {
         points = points + 1d;
      }
//      LOG.debug(p.getName() + " points: " + points);
      return points;
   }




   public static Player defineTurnWinner(Turn turn, Card.Suit briscola) {
      Collection<CardOnTable> playedCards = turn.getPlayedCards().values();
      final Iterator<CardOnTable> iterator = playedCards.iterator();
      CardOnTable currentWinnerPlayedCard = iterator.next();
      for (; iterator.hasNext();)
      {
         currentWinnerPlayedCard = findWinnerCard(iterator.next(), currentWinnerPlayedCard, briscola);
      }
      final Player winnerPlayer = currentWinnerPlayedCard.getPlayer();
      turn.setWinnerPlayer(winnerPlayer);
      return winnerPlayer;
   }

}
