package maraffone.player.ann;

import maraffone.card.Card;
import maraffone.card.Card.Suit;
import maraffone.card.CardOnTable;
import maraffone.constants.MaraffoneConstants;
import maraffone.knowledge.*;
import maraffone.knowledge.Replay;
import maraffone.player.AbstractPlayer;
import maraffone.player.PlayerCard;
import maraffone.table.Match;
import maraffone.table.Turn;
import maraffone.utils.ANNEncoder;
import org.apache.log4j.Logger;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.rng.DefaultRandom;
import org.nd4j.linalg.api.rng.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class DqnPlayer extends AbstractPlayer {

   private final static Logger LOG = Logger.getLogger(DqnPlayer.class);
   private Random random = new DefaultRandom(System.currentTimeMillis());

   private MultiLayerNetwork deepQ;

   PlayerCard action;

   public DqnPlayer() {
      this.deepQ = retrieveCommonAnn();
   }

   private MultiLayerNetwork retrieveCommonAnn() {
      return DqnForPlayers.getInstance().getAnn();
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
      //      LOG.info(getName() + ": Briscola Is " + selectedSuit);
      return selectedSuit;
   }


   @Override
   public Card playCard(Match match, Map<String, CardOnTable> table, int t, int zrow) {
      oldStateObject = getOrderedHand(getCurrentCards());
      oldState = getHash(oldStateObject);
      action = selectCardToPlay(table, playerHand);

      action.setAlreadyPlayed(true);
      //      LOG.debug("" + name + " played " + action.getCard().toShortString());
      currentStateObject = getOrderedHand(getCurrentCards());
      currentState = getHash(currentStateObject);
      //      LOG.debug(this.getName() + " currentstate: " + currentState);
      return action.getCard();

   }


   /**
    * retrieve card to play using an actual startegy based on Q table
    * @param table
    * @param hand
    * @return
    */
   private PlayerCard selectCardToPlay(Map<String, CardOnTable> table, List<PlayerCard> hand) {
      ArrayList<PlayerCard> actionsFromState = actionsFromState(table, hand);

      double maxResult = Integer.MIN_VALUE;
      PlayerCard actionWithMaxResult = null;

      // return action from Ann
      INDArray input = ANNEncoder.getBinaryEncodingForPlayerCardsList(actionsFromState);
      INDArray output = deepQ.output(input);


      // return the card which gives the max output and is contained in the allowed actions
      for (int i = 0; i < output.length(); i++) {
         final Card cardFromId = Card.getCardFromUniqueNumericId(i);
         if (actionsFromState.stream().filter(pc -> pc.getCard().equals(cardFromId)).findAny().isPresent()
               && output.getDouble(i) > maxResult) {
            // LOG.info("Q USED");
            maxResult = output.getDouble(i);
            actionWithMaxResult = actionsFromState.stream().filter(playerCard -> playerCard.getCard().equals(cardFromId))
                  .findFirst().get();
         }
      }

      currentAction = actionWithMaxResult;
      return actionWithMaxResult;
   }


   private ArrayList<PlayerCard> actionsFromState(Map<String, CardOnTable> table, List<PlayerCard> hand) {
      if (table.isEmpty()) {
         return hand.stream().filter(c -> !c.isAlreadyPlayed()).collect(Collectors.toCollection(ArrayList::new));
      }

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


   public void updateTurnReward(double calculatedTurnReward, Turn turn) {
      // store the information on replay Memory
      Replay replay = new Replay(ANNEncoder.getBinaryEncodingForCardsList(currentStateObject), currentAction.getCard(),
            getNormalizedReward(calculatedTurnReward), ANNEncoder.getBinaryEncodingForCardsList(oldStateObject),
            ANNEncoder.getBinaryMaskEncodingForCardsList(oldStateObject));
      ReplayMemory replayMemory = DqnForPlayers.getInstance().getReplayMemory();
      replayMemory.addReplay(replay);
   }

   public double getNormalizedReward(double reward){
      return reward / MaraffoneConstants.MARAFFA_TOTAL_POINTS_PER_MATCH;
   }

}
