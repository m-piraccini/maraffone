package maraffone.player;

import maraffone.card.Card;
import maraffone.card.CardComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 *
 * This player is the actual intelligent agent and can be customized using Qtable, ann, dnq, others
 *
 */
public abstract class AbstractPlayer implements Player {


   protected String name;
   protected List<PlayerCard> playerHand = new ArrayList<PlayerCard>();
   protected Player nextPlayer;
   protected List<Card> wonCardsStack;
   protected Player mate;
   protected double cumulatedPointsInMatches;



   protected PlayerCard currentAction;
   protected String currentState;
   protected String oldState;

   @Override
   public void initializeHand(List<Card> subList) {
      playerHand = new ArrayList<PlayerCard>();
      for (Card card : subList) {
         playerHand.add(new PlayerCard(card));
      }

      // TODO: init Learning
      for (Card card : subList) {
         String hash = getHash(getOrderedHand(getCurrentCards()));
         //			Q.put(hash, card , 0d);
         //			System.out.println( this.getName() + " - hashing: " + hash + " : " + card.toShortString() );
      }

   }


   public List<Card> getOrderedHand(List<Card> currentCards) {
      Collections.sort(currentCards, new CardComparator());
      return currentCards;
   }




   /**
    *
    * @return current available cards for a player
    */
   @Override
   public List<Card> getCurrentCards() {
      return playerHand.stream().filter(c -> !c.isAlreadyPlayed()).map(c -> c.getCard())
            .collect(Collectors.toCollection(ArrayList::new));
   }


   @Override
   public String getHash(List<Card> cardList) {
      String hash = "";
      for (Card card : cardList) {
         hash = hash.concat(card.toShortString()).concat(":");
      }
      hash = hash.substring(0, hash.length() > 0 ? hash.length() - 1 : 0);
      return hash;
   }

   @Override
   public Collection<Card> getWinnedCardsStack() {
      return wonCardsStack;
   }


   @Override
   public void setWinnedCardsStack(List<Card> winnedCardsStack) {
      this.wonCardsStack = winnedCardsStack;
   }


   @Override
   public List<PlayerCard> getHand() {
      return playerHand;
   }

   @Override
   public void setHand(List<PlayerCard> hand) {
      this.playerHand = hand;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public void setName(String name) {
      this.name = name;
   }

   @Override
   public Player getNextPlayer() {
      return nextPlayer;
   }

   @Override
   public void setNextPlayer(Player nextPlayer) {
      this.nextPlayer = nextPlayer;
   }

   @Override
   public Player getMate() {
      return mate;
   }

   @Override
   public void setMate(Player mate) {
      this.mate = mate;
   }

   @Override
   public void clearPlayer() {
      playerHand.clear();
      wonCardsStack.clear();

   }

   @Override
   public double getCumulatedPointsInMatches() {
      return cumulatedPointsInMatches;
   }

   @Override
   public void setCumulatedPointsInMatches(double cumulatedPointsInMatches) {
      this.cumulatedPointsInMatches = cumulatedPointsInMatches;
   }
}
