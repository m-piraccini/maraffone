package maraffone.player;

import maraffone.card.Card;
import maraffone.card.Card.Suit;
import maraffone.card.CardOnTable;
import maraffone.knowledge.AnnForPlayers;
import maraffone.table.Match;
import maraffone.table.Turn;
import org.apache.log4j.Logger;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.nd4j.linalg.api.rng.DefaultRandom;
import org.nd4j.linalg.api.rng.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class AnnPlayer extends AbstractPlayer {

   private final static Logger LOG = Logger.getLogger(AnnPlayer.class);
   private Random random = new DefaultRandom(System.currentTimeMillis());

   private MultiLayerConfiguration ann;


   public AnnPlayer() {
      this.ann = retrieveCommonAnn();
   }

   private MultiLayerConfiguration retrieveCommonAnn() {
      return AnnForPlayers.getInstance().getAnn();
   }


   //
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


   @Override
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
    * retrieve card to play using an actual startegy based on Q table
    * @param table
    * @param hand
    * @return
    */
   private PlayerCard selectCardToPlay(ArrayList<CardOnTable> table, List<PlayerCard> hand) {
      ArrayList<PlayerCard> actionsFromState = actionsFromState(table, hand);

      double maxResult = 0d;
      PlayerCard actionWithMaxResult = null;

      // find action returning max reward
      for (PlayerCard card: actionsFromState)
      {
//         Double reward = Q.get(currentState,card);
//         if (reward != null && reward > maxResult){
//            LOG.error("Q USED");
//            maxResult=reward.doubleValue();
//            actionWithMaxResult = card;
//         }
      }

      // return random card if cannot find action in table
      if (actionWithMaxResult == null)
         actionWithMaxResult = actionsFromState.stream().findAny().get();

      currentAction = actionWithMaxResult;

      return actionWithMaxResult;
   }


   private ArrayList<PlayerCard> actionsFromState(ArrayList<CardOnTable> table, List<PlayerCard> hand) {
      if (table.isEmpty()) {
         return hand.stream().filter(c -> !c.isAlreadyPlayed()).collect(Collectors.toCollection(ArrayList::new));
      }

      ArrayList<PlayerCard> findSuitMatchingCard = hand.stream()
            .filter(c -> !c.isAlreadyPlayed() && c.getCard().suit().equals(table.get(0).getCard().suit()))
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

   public void updateTurnReward(double calculatedTurnReward, Turn turn) {


   }



}
