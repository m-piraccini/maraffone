package maraffone.player;

import com.google.common.collect.Table;
import maraffone.card.Card;
import maraffone.card.Card.Suit;
import maraffone.card.CardOnTable;
import maraffone.knowledge.QForPlayers;
import maraffone.table.Match;
import maraffone.table.Turn;
import org.apache.log4j.Logger;
import org.nd4j.linalg.api.rng.DefaultRandom;
import org.nd4j.linalg.api.rng.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class RandomPlayer extends AbstractPlayer {

   private final static Logger LOG = Logger.getLogger(RandomPlayer.class);

   private Random random = new DefaultRandom(System.currentTimeMillis());

   public RandomPlayer() {
   }

   private Table<String, Card, Double> retrieveCommonQ() {
      return QForPlayers.getInstance().getQ();
   }


   /**
    * chooses a random briscola
    * @return
    */
   @Override
   public Suit chooseBriscola() {
      int randomSuitNumber = random.nextInt(3) + 1;
      Suit selectedSuit = null;
      for (Suit suit : Card.getSuitOrderingMap().keySet()) {
         if (randomSuitNumber == Card.getSuitOrderingMap().get(suit))
            selectedSuit = suit;
      }
      LOG.info(getName() + ": Briscola Is " + selectedSuit);
      return selectedSuit;
   }


   public Card trowCard(Match match, ArrayList<CardOnTable> table, int t, int zrow) {
      oldState = getHash(getOrderedHand(getCurrentCards()));
      PlayerCard action = selectCardToPlay(table, playerHand);
      action.setAlreadyPlayed(true);
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
   private PlayerCard selectCardToPlay(ArrayList<CardOnTable> table, List<PlayerCard> hand) {
      ArrayList<PlayerCard> actionsFromState = actionsFromState(table, hand);

      PlayerCard action = actionsFromState.get(random.nextInt(actionsFromState.size()));

      currentAction = action;

      return action;

   }


   /**
    * retrieve a list of cards that the player could throw in the specified hand
    * @param table
    * @param hand
    * @return
    */
   private ArrayList<PlayerCard> actionsFromState(ArrayList<CardOnTable> table, List<PlayerCard> hand) {
      // TODO: refactor this method
      // in case of first player return all possible cards
      if (table.isEmpty()) {
         return hand.stream().filter(c -> !c.isAlreadyPlayed()).collect(Collectors.toCollection(ArrayList::new));
      }
      else // filter cards by suit
      {
         ArrayList<PlayerCard> findSuitMatchingCard = hand.stream()
               .filter(c -> !c.isAlreadyPlayed() && c.getCard().suit().equals(table.get(0).getCard().suit())).collect(Collectors.toCollection(ArrayList::new));

         if (findSuitMatchingCard.size() > 0) {
            return findSuitMatchingCard;
         }
         else {
            ArrayList<PlayerCard> findAnyCard = hand.stream().filter(c -> !c.isAlreadyPlayed()).collect(Collectors.toCollection(ArrayList::new));
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
