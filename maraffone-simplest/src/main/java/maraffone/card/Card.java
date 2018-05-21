package maraffone.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class Card {
   public static final int CARDS_IN_DECK = 20;
   public static final int RANKS = 5;


   public enum Rank {
      ACE("1"), TWO("2"), THREE("3"), KNIGHT("C"), KING("R");

      private final String id;

      Rank(final String id) {
         this.id = id;
      }

      public String getValue() {
         return id;
      }

      protected static HashMap<Rank, Integer> priorityRankMap = new HashMap();

      static {
         priorityRankMap.put(Rank.KNIGHT, 0);
         priorityRankMap.put(Rank.KING, 1);
         priorityRankMap.put(Rank.ACE, 2);
         priorityRankMap.put(Rank.TWO, 3);
         priorityRankMap.put(Rank.THREE, 4);
      }
   }


   public enum Suit {
      CLUBS("B"), DIAMONDS("C"), HEARTS("D"), SPADES("S");
      private final String id;

      Suit(final String id) {
         this.id = id;
      }

      public String getValue() {
         return id;
      }

      public static int getStartingUniqueId(Suit suit) {
         return uniqueNumericIdMap.get(suit);
      }
   }


   private static HashMap<Suit, Integer> uniqueNumericIdMap = new HashMap();

   static {
      uniqueNumericIdMap.put(Suit.CLUBS, 0);
      uniqueNumericIdMap.put(Suit.DIAMONDS, 1);
      uniqueNumericIdMap.put(Suit.HEARTS, 2);
      uniqueNumericIdMap.put(Suit.SPADES, 3);
   }

   public static HashMap<Suit, Integer> getUniqueNumericIdMap() {
      return uniqueNumericIdMap;
   }

   private static HashMap<Rank, Double> pointMap = new HashMap();

   static {
      pointMap.put(Rank.KNIGHT, 1 / 3d);
      pointMap.put(Rank.KING, 1 / 3d);
      pointMap.put(Rank.ACE, 1d);
      pointMap.put(Rank.TWO, 1 / 3d);
      pointMap.put(Rank.THREE, 1 / 3d);
   }


   private static final HashMap<Suit, Integer> suitOrderingMap = new HashMap();

   static {
      suitOrderingMap.put(Suit.HEARTS, 4);
      suitOrderingMap.put(Suit.SPADES, 3);
      suitOrderingMap.put(Suit.DIAMONDS, 2);
      suitOrderingMap.put(Suit.CLUBS, 1);
   }


   private final Rank rank;
   private final Suit suit;
   private final int uniqueNumericId;


   private Card(final Rank rank, final Suit suit) {
      this.rank = rank;
      this.suit = suit;
      this.uniqueNumericId = Card.uniqueNumericIdMap.get(suit)*RANKS + Rank.priorityRankMap.get(rank);
   }

   public Rank rank() {
      return rank;
   }

   public Suit suit() {
      return suit;
   }

   @Override
   public String toString() {
      return rank + " of " + suit;
   }

   public String toShortString() {
      return rank.getValue() + suit.getValue();
   }

   public int rankPriority() {
      return Rank.priorityRankMap.get(rank);
   }

   public int suitPriority() {
      return suitOrderingMap.get(suit);
   }

   public double pointsValue() {
      return pointMap.get(rank);
   }


   private static final List<Card> protoDeck = new ArrayList<Card>();

   // Initialize prototype deck
   static {
      for (final Suit suit : Suit.values()) {
         for (final Rank rank : Rank.values()) {
            protoDeck.add(new Card(rank, suit));
         }
      }
   }

   public static ArrayList<Card> newDeck() {

      Collections.shuffle(protoDeck);
      return new ArrayList<Card>(protoDeck); // Return copy of prototype deck

   }


   public static Card getCardFromUniqueNumericId(int numericId) {
//      System.out.println("numericId " + numericId);
      Rank rank = Rank.priorityRankMap.entrySet().stream().filter(map -> map.getValue() == numericId % RANKS).findFirst().get()

            .getKey();
//      System.out.println("RANK" + rank.toString());
      Suit suit = uniqueNumericIdMap.entrySet().stream().filter(map -> map.getValue() == numericId / RANKS).findFirst().get()
            .getKey();
//      System.out.println("SUIT" + suit.toString());
      return new Card(rank, suit);
   }

   public static HashMap<Suit, Integer> getSuitOrderingMap() {
      return suitOrderingMap;
   }


   @Override
   public boolean equals(final Object c2) {
      return c2 != null && (c2 instanceof Card) && this.suit.equals(((Card) c2).suit) && this.rank.equals(((Card) c2).rank);
   }

   public int getUniqueNumericIdentifier() {
      return uniqueNumericId;
   }
}
