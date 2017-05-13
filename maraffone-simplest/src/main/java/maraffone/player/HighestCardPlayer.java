package maraffone.player;

import com.google.common.collect.Table;
import maraffone.card.Card;
import maraffone.card.Card.Suit;
import maraffone.card.CardComparator;
import maraffone.card.CardOnTable;
import maraffone.knowledge.QForPlayers;
import maraffone.table.Match;
import maraffone.table.Turn;
import org.apache.log4j.Logger;
import org.nd4j.linalg.api.rng.DefaultRandom;
import org.nd4j.linalg.api.rng.Random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class HighestCardPlayer extends AbstractPlayer {

   private final static Logger LOG = Logger.getLogger(HighestCardPlayer.class);

   private Random random = new DefaultRandom(System.currentTimeMillis());

   public HighestCardPlayer() {
   }

   private Table<String, Card, Double> retrieveCommonQ() {
      return QForPlayers.getInstance().getQ();
   }


   /**
    * chooses a briscola where I have more cards
    * @return
    */
   @Override
   public Suit chooseBriscola() {

      Map<Suit, Long> counting = new HashMap<>();
      getHand().stream().forEach(pcard -> {
         if (counting.get(pcard.getCard().suit()) == null)
            counting.put(pcard.getCard().suit(), 0L);
         else
            counting.put(pcard.getCard().suit(), counting.get(pcard.getCard().suit()) + 1);
      });


      Suit selectedSuit = null;
      long max = 0;
      for(Suit suit :counting.keySet()){
         if (counting.get(suit) > max)
         {
            selectedSuit = suit;
            max = counting.get(suit);
         }
      }


      logBriscola(selectedSuit);
      return selectedSuit;
   }


   public Card playCard(Match match, Map<String, CardOnTable> table, int t, int zrow) {
      oldState = getHash(getOrderedHand(getCurrentCards()));
      PlayerCard action = selectCardToPlay(table, playerHand);
      action.setAlreadyPlayed(true);
//      LOG.debug(this.getName() + " currentstate: " + oldState + ". Playing: " + action.getCard().toShortString());
      //      LOG.debug("" + name + " played " + action.getCard().toShortString());
      currentState = getHash(getOrderedHand(getCurrentCards()));
      //      LOG.debug(this.getName() + " currentstate: " + currentState);
      return action.getCard();

   }

   /**
    * selects a random card
    * @param table
    * @param hand
    * @return
    */
   private PlayerCard selectCardToPlay(Map<String, CardOnTable> table, List<PlayerCard> hand) {
      ArrayList<PlayerCard> actionsFromState = actionsFromState(table, hand);

      PlayerCard highestCard = actionsFromState.stream().max((c1, c2) -> CardComparator.comparePlayedCards(c2, c1)).get();
      PlayerCard action = highestCard != null ? highestCard : actionsFromState.get(random.nextInt(actionsFromState.size()));

      currentAction = action;

      return action;

   }


   /**
    * retrieve a list of cards that the player could throw in the specified hand
    * @param table
    * @param hand
    * @return
    */
   private ArrayList<PlayerCard> actionsFromState(Map<String, CardOnTable> table, List<PlayerCard> hand) {
      // TODO: refactor this method
      // in case of first player return all possible cards
      if (table.isEmpty()) {
         return hand.stream().filter(c -> !c.isAlreadyPlayed()).collect(Collectors.toCollection(ArrayList::new));
      }
      else // filter cards by suit
      {
         final ArrayList<PlayerCard> findSuitMatchingCard = hand.stream()
               .filter(c -> !c.isAlreadyPlayed() && c.getCard().suit().equals(table.values().iterator().next().getCard().suit()))
               .collect(Collectors.toCollection(ArrayList::new));

         if (findSuitMatchingCard.size() > 0) {
            return findSuitMatchingCard;
         }
         else {
            ArrayList<PlayerCard> findAnyCard = hand.stream().filter(c -> !c.isAlreadyPlayed())
                  .collect(Collectors.toCollection(ArrayList::new));
            return findAnyCard;
         }
      }
   }


   /**
    *
    * @param calculatedTurnReward
    * @param turn
    */
   public void updateTurnReward(double calculatedTurnReward, Turn turn) {
      // no reward mechanism for random player
   }


}
