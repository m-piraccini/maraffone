package maraffone.player;

import maraffone.card.Card;
import maraffone.card.CardOnTable;
import maraffone.table.Match;
import maraffone.table.Turn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**

 */
public interface Player {


   List<Card> getCurrentCards();


   String getHash(List<Card> cardList);


   Collection<Card> getWinnedCardsStack();


   Player getMate();

   void updateTurnReward(double calculateTurnReward, Turn turn);

   Player getNextPlayer();

   Card playCard(Match match, Map<String,CardOnTable> table, int t, int trow);

   Card.Suit chooseBriscola();

   void setWinnedCardsStack(List<Card> winnedCardsStack);


   List<PlayerCard> getHand();


   void setHand(List<PlayerCard> hand);


   String getName();


   void setName(String name);


   void setNextPlayer(Player nextPlayer);


   void setMate(Player mate);


   void clearPlayer();

   void initializeHand(List<Card> subList);


   double getCumulatedPointsInMatches();


   void setCumulatedPointsInMatches(double cumulatedPointsInMatches);

}
