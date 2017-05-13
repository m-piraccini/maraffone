//package evoca.player;
//
//import com.google.common.collect.Table;
//import evoca.learning.common.Action;
//import evoca.learning.common.Environment;
//import maraffone.card.Card;
//import maraffone.card.Card.Suit;
//import maraffone.card.CardOnTable;
//import maraffone.constants.MaraffoneConstants;
//import maraffone.constants.MaraffoneParameters;
//import maraffone.knowledge.QForPlayers;
//import maraffone.table.Match;
//import maraffone.table.Turn;
//import org.apache.log4j.Logger;
//import org.nd4j.linalg.api.rng.DefaultRandom;
//import org.nd4j.linalg.api.rng.Random;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//
//public class QPlayer extends AbstractPlayer {
//
//   private final static Logger LOG = Logger.getLogger(QPlayer.class);
//   private Random random = new DefaultRandom(System.currentTimeMillis());
//
//
//   private Table<String, Card, Double> Q;
//
//
//   public QPlayer() {
//      this.Q = retrieveCommonQ();
//   }
//
//   private Table<String, Card, Double> retrieveCommonQ() {
//      return QForPlayers.getInstance().getQ();
//   }
//
//
//
//
//   @Override
//   public Action act(Environment environment) {
//      return ((TictactoeTable)environment).getActionSpace().random();
//   }
//
//
//   //
//   @Override
//   public Suit chooseBriscola() {
//      int randomSuitNumber = random.nextInt(3) + 1;
//      Suit selectedSuit = null;
//      for (Suit suit : Card.getSuitOrderingMap().keySet()) {
//         if (randomSuitNumber == Card.getSuitOrderingMap().get(suit))
//            selectedSuit = suit;
//      }
//
//      logBriscola(selectedSuit);
//      return selectedSuit;
//   }
//
//
//   @Override
//   public Card playCard(Match match, Map<String, CardOnTable> table, int t, int zrow) {
//      oldState = getHash(getOrderedHand(getCurrentCards()));
//      PlayerCard action = selectCardToPlay(table, playerHand);
//      action.setAlreadyPlayed(true);
//      //      LOG.debug("" + name + " played " + action.getCard().toShortString());
//      currentState = getHash(getOrderedHand(getCurrentCards()));
//      //      LOG.debug(this.getName() + " currentstate: " + currentState);
//      return action.getCard();
//
//   }
//
//
//   /**
//    * retrieve card to play using an actual startegy based on Q table
//    * @param table
//    * @param hand
//    * @return
//    */
//   private PlayerCard selectCardToPlay(Map<String, CardOnTable> table, List<PlayerCard> hand) {
//      List<PlayerCard> actionsFromState = actionsFromState(table, hand);
//
//      PlayerCard actionWithMaxResult = egreedyPolicy(actionsFromState);
//
//      currentAction = actionWithMaxResult;
//
//      return actionWithMaxResult;
//   }
//
//
//   private PlayerCard egreedyPolicy(List<PlayerCard> actionsFromState) {
//
//      if (random.nextDouble() < MaraffoneParameters.EGREEDY_POLICY_EPS) {
//         return returnCardWithMaxExpectedValue(actionsFromState);
//      }
//      else // select random policy
//      {
//         return returnRandomCard(actionsFromState);
//      }
//   }
//
//
//   private PlayerCard returnCardWithMaxExpectedValue(List<PlayerCard> actionsFromState) {
//      PlayerCard actionWithMaxResult = null;
//      double maxResult = -(Double.MAX_VALUE - 10);
//      // find action returning max reward
//      for (PlayerCard card : actionsFromState) {
//         Double reward = Q.get(currentState, card.getCard());
//         if (reward != null && reward > maxResult) {
//            // LOG.info("Q USED");
//            maxResult = reward.doubleValue();
//            actionWithMaxResult = card;
//         }
//      }
//      // return random card if cannot find action in table
//      if (actionWithMaxResult == null)
//         actionWithMaxResult = returnRandomCard(actionsFromState);
//
//      return actionWithMaxResult;
//   }
//
//
//   private PlayerCard returnRandomCard(List<PlayerCard> actionsFromState) {
//      return actionsFromState.get(random.nextInt(actionsFromState.size()));
//      //            actionsFromState.stream().findAny().get();
//   }
//
//
//   private List<PlayerCard> actionsFromState(Map<String, CardOnTable> table, List<PlayerCard> hand) {
//      if (table.isEmpty()) {
//         return hand.stream().filter(c -> !c.isAlreadyPlayed()).collect(Collectors.toCollection(ArrayList::new));
//      }
//
//      ArrayList<PlayerCard> findSuitMatchingCard = hand.stream()
//            .filter(c -> !c.isAlreadyPlayed() && c.getCard().suit().equals(table.values().iterator().next().getCard().suit()))
//            .collect(Collectors.toCollection(ArrayList::new));
//      if (findSuitMatchingCard.size() > 0) {
//         return findSuitMatchingCard;
//      }
//      else {
//         ArrayList<PlayerCard> findAnyCard = hand.stream().filter(c -> !c.isAlreadyPlayed())
//               .collect(Collectors.toCollection(ArrayList::new));
//         return findAnyCard;
//      }
//   }
//
//   public void updateTurnReward(double calculatedTurnReward, Turn turn) {
//      //get the max Q value of the resulting state if it's not terminal, 0 otherwise
//      double maxQ = turn.getTurnNumber() == MaraffoneConstants.TURNS_NUMBER ? 0. : this.maxQ(currentState);
//
//      //update the old Q-value
//      Double oldQ;
//      if (Q.contains(oldState, currentAction.getCard())) {
//         oldQ = Q.get(oldState, currentAction.getCard());
//      }
//      else {
//         oldQ = 0d;
//         Q.put(oldState, currentAction.getCard(), oldQ);
//      }
//      oldQ =
//            oldQ + MaraffoneParameters.Q_PLAYER.ALPHA * (calculatedTurnReward + MaraffoneParameters.Q_PLAYER.GAMMA * maxQ - oldQ);
//
//      Q.put(oldState, currentAction.getCard(), oldQ);
//
//   }
//
//
//   private double maxQ(String s) {
//      //      LOG.info(this.getName() + " - retrieving actions for state: " + s);
//      Map<Card, Double> row = Q.row(s);
//      if (row.isEmpty()) {
//         return 0;
//      }
//      return row.values().stream().max(Double::compare).get();
//   }
//
//
//}
