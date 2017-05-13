package maraffone.utils;

import maraffone.card.Card;
import maraffone.card.CardOnTable;
import maraffone.constants.MaraffoneConstants;
import maraffone.player.Player;
import maraffone.player.PlayerCard;
import maraffone.table.Turn;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;


public class ANNEncoder {

   private static final int PLAYER_CARDS = 20;
   private static final int BRISCOLA_CARDS = 20;
   private static final int ACTION_CARD = 20;
   private static final int REWARD_FOR_ACTION_PLAYED = 1;
   private static final int PLAYER_NUMBER_IN_TURN = 4;

   private static final double ACTUAL_TURN_ACTIVATED = 1.0d;
   private static final double THROW_IN_TURN_ACTIVATED = 1.0d;
   private static final double ACTION_ACTIVATED = 1.0d;
   private static final int UNIDIMENSIONAL_ARRAY = 0;

   public ANNEncoder() {

   }


   public static final HashMap<String, INDArray> encodeTurn(Turn turn) {
      HashMap<String, INDArray> mapPlayers = new HashMap<String, INDArray>();

      for (CardOnTable cardOnTable : turn.getPlayedCards().values()) {
         INDArray playerVector = Nd4j.hstack(getDummyEncodingForPlayedCard(cardOnTable.getCard()),
               getBinaryEncodingForPlaceInTurn(cardOnTable.getPositionInTurn()));
         mapPlayers.put(cardOnTable.getPlayer().getName(), playerVector);
      }

      return mapPlayers;
   }


   private static INDArray getBinaryEncodingForPlaceInTurn(int positionInTurn) {
      INDArray inputs = Nd4j.zeros(MaraffoneConstants.THROWS_NUMBER);
      inputs.put(0, positionInTurn, THROW_IN_TURN_ACTIVATED);
      return inputs;
   }


   private static INDArray getBinaryEncodingForTurnNumber(int turnNumber) {
      INDArray inputs = Nd4j.zeros(MaraffoneConstants.TURNS_NUMBER);
      inputs.put(UNIDIMENSIONAL_ARRAY, turnNumber, ACTUAL_TURN_ACTIVATED);
      return inputs;
   }


   public DataSet trainDataFromVectors(INDArray in, INDArray out) {
      return new DataSet(in, out);
   }


   public static INDArray getBinaryEncodingForChosenBriscolaSuit(Card.Suit suit) {
      INDArray inputs = Nd4j.zeros(BRISCOLA_CARDS);
      for (int i = 0; i < MaraffoneConstants.NUMBER_OF_CARDS_PER_SUIT; i++) {
         inputs.put(UNIDIMENSIONAL_ARRAY, Card.Suit.getStartingUniqueId(suit) + i, ACTION_ACTIVATED);
      }
      return inputs;
   }


   public static INDArray getBinaryEncodingForPlayerCardsList(List<PlayerCard> playerHand) {
      INDArray inputs = Nd4j.zeros(PLAYER_CARDS);
      playerHand.stream().filter(palyerCard -> !palyerCard.isAlreadyPlayed())
            .forEach(playerCard -> inputs.put(UNIDIMENSIONAL_ARRAY, playerCard.getCard().getUniqueNumericIdentifier(), ACTION_ACTIVATED));
      return inputs;
   }

   /**
    * Retunrs a binary
    * @param playerHand
    * @return
    */
   public static INDArray getBinaryEncodingForCardsList(List<Card> playerHand) {
      INDArray inputs = Nd4j.zeros(PLAYER_CARDS);
      playerHand.stream().forEach(card -> inputs.put(UNIDIMENSIONAL_ARRAY, card.getUniqueNumericIdentifier(), ACTION_ACTIVATED));
      return inputs;
   }



   /**
    * Retunrs a binary
    * @param playerHand
    * @return
    */
   public static int[] getBinaryMaskEncodingForCardsList(List<Card> playerHand) {
      int[] inputs = new int[PLAYER_CARDS];
      playerHand.stream().forEach(card -> inputs[card.getUniqueNumericIdentifier()] = 1);
      return inputs;
   }




   public static INDArray getDummyEncodingForPlayedCard(Card card) {
      INDArray inputs = Nd4j.zeros(ACTION_CARD);
      inputs.put(UNIDIMENSIONAL_ARRAY, card.getUniqueNumericIdentifier(), ACTION_ACTIVATED);
      return inputs;
   }


   public static INDArray getNormalizedTurnPoints(double points) {
      INDArray outputs = Nd4j.create(REWARD_FOR_ACTION_PLAYED);
      outputs.put(0, 0, points / MaraffoneConstants.MARAFFA_TOTAL_POINTS_PER_MATCH);
      return outputs;
   }


   public static List<DataSet> createANNInputSamples(Turn turn, HashMap<String, INDArray> playersHands,
         double calculateTurnReward, Card.Suit briscola, Player winnerPlayer) {

      List<DataSet> datasetList = new ArrayList<>();

      int ONE_PLAYER_SLOT = ACTION_CARD + PLAYER_NUMBER_IN_TURN;
      INDArray playersActions = Nd4j.create(1, 4 * (ONE_PLAYER_SLOT)); // 4*(ACTION_CARD+PLAYER_NUMBER_IN_TURN));

      IntStream.range(0, 4).forEach(i -> {

         CardOnTable cardOnTable = turn.getPlayedCards().get("P" + i);
         playersActions
               .put(new INDArrayIndex[] { NDArrayIndex.interval(i * (ONE_PLAYER_SLOT), i * (ONE_PLAYER_SLOT) + ACTION_CARD) },
                     getDummyEncodingForPlayedCard(cardOnTable.getCard()));
         playersActions.put(new INDArrayIndex[] {
                     NDArrayIndex.interval(i * (ONE_PLAYER_SLOT) + ACTION_CARD, i * (ONE_PLAYER_SLOT) + ONE_PLAYER_SLOT) },
               getBinaryEncodingForPlaceInTurn(cardOnTable.getPositionInTurn()));
         //					playersActions = Nd4j.hstack(playersActions, getDummyEncodingForPlayedCard(card.getCard()));
         //					playersActions = Nd4j.hstack(playersActions, getDummyEncodingForPlaceInTurn(card.getPositionInTurn()));

      });

      List<INDArray> encodedTurnList = new ArrayList<INDArray>();
      List<INDArray> outputList = new ArrayList<INDArray>();

      INDArray encodedTurn = Nd4j.create(266);
      IntStream.range(0, 4).forEach(i -> {

         // encode choosen briscola
         encodedTurn.put(new INDArrayIndex[] { NDArrayIndex.interval(0, BRISCOLA_CARDS) },
               getBinaryEncodingForChosenBriscolaSuit(
                     briscola)); // BRISCOLA_CARDS + PLAYER_CARDS + 4*(ACTION_CARD+PLAYER_NUMBER_IN_TURN) + MarafoneConstants.TURNS_NUMBER );

         // encode player hand
         encodedTurn.put(new INDArrayIndex[] { NDArrayIndex.interval(BRISCOLA_CARDS, BRISCOLA_CARDS + PLAYER_CARDS) },
               playersHands.get("P" + i));

         // add players actions
         encodedTurn.put(new INDArrayIndex[] {
                     NDArrayIndex.interval(BRISCOLA_CARDS + PLAYER_CARDS, BRISCOLA_CARDS + PLAYER_CARDS + 4 * ONE_PLAYER_SLOT) },
               playersActions);

         // add turn number
         encodedTurn.put(new INDArrayIndex[] { NDArrayIndex.interval(BRISCOLA_CARDS + PLAYER_CARDS + 4 * ONE_PLAYER_SLOT,
               BRISCOLA_CARDS + PLAYER_CARDS + 4 * ONE_PLAYER_SLOT + MaraffoneConstants.TURNS_NUMBER) },
               getBinaryEncodingForTurnNumber(turn.getTurnNumber()));


         // add points to output
         INDArray normalizedPoints;
         if (winnerPlayer.getName().equals("P" + i) || winnerPlayer.getMate().getName().equals("P" + i)) {
            normalizedPoints = getNormalizedTurnPoints(calculateTurnReward);
         }
         else {
            normalizedPoints = getNormalizedTurnPoints(-calculateTurnReward);
         }
         encodedTurnList.add(encodedTurn);
         outputList.add(normalizedPoints);

         DataSet set = new DataSet(encodedTurn, normalizedPoints);
         datasetList.add(set);

         //			System.out.println(encodedTurn.size(1) + ": " +encodedTurn);
      });

      return datasetList;
   }


}
