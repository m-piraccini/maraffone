package maraffone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class Card
{
	public enum Rank
	{
		ACE("1"), TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"), SEVEN("7"), JACK("F"), KNIGHT("C"), KING("R");

		private final String id;

		Rank(final String id)
		{
			this.id = id;
		}

		public String getValue()
		{
			return id;
		}
	}

	public enum Suit
	{
		CLUBS("B"), DIAMONDS("C"), HEARTS("D"), SPADES("S");
		private final String id;

		Suit(final String id)
		{
			this.id = id;
		}

		public String getValue()
		{
			return id;
		}
	}

	private static HashMap<Rank, Integer> priorityRankMap = new HashMap();
	static
	{
		priorityRankMap.put(Rank.FOUR, 1);
		priorityRankMap.put(Rank.FIVE, 2);
		priorityRankMap.put(Rank.SIX, 3);
		priorityRankMap.put(Rank.SEVEN, 4);
		priorityRankMap.put(Rank.JACK, 5);
		priorityRankMap.put(Rank.KNIGHT, 6);
		priorityRankMap.put(Rank.KING, 7);
		priorityRankMap.put(Rank.ACE, 8);
		priorityRankMap.put(Rank.TWO, 9);
		priorityRankMap.put(Rank.THREE, 10);
	}

	private static HashMap<Rank, Double> pointMap = new HashMap();
	static
	{
		pointMap.put(Rank.FOUR, 0d);
		pointMap.put(Rank.FIVE, 0d);
		pointMap.put(Rank.SIX, 0d);
		pointMap.put(Rank.SEVEN, 0d);
		pointMap.put(Rank.JACK, 1 / 3d);
		pointMap.put(Rank.KNIGHT, 1 / 3d);
		pointMap.put(Rank.KING, 1 / 3d);
		pointMap.put(Rank.ACE, 1d);
		pointMap.put(Rank.TWO, 1 / 3d);
		pointMap.put(Rank.THREE, 1 / 3d);
	}



	private static HashMap<Suit, Integer> suitOrderingMap = new HashMap();
	static
	{
		suitOrderingMap.put(Suit.HEARTS, 4);
		suitOrderingMap.put(Suit.SPADES, 3);
		suitOrderingMap.put(Suit.DIAMONDS, 2);
		suitOrderingMap.put(Suit.CLUBS, 1);
	}



	private final Rank rank;
	private final Suit suit;


	private Card(final Rank rank, final Suit suit)
	{
		this.rank = rank;
		this.suit = suit;
	}

	public Rank rank()
	{
		return rank;
	}

	public Suit suit()
	{
		return suit;
	}

	@Override
	public String toString()
	{
		return rank + " of " + suit;
	}

	public String toShortString()
	{
		return rank.getValue() + suit.getValue();
	}

	public int rankPriority()
	{
		return priorityRankMap.get(rank);
	}

	public int suitPriority()
	{
		return suitOrderingMap.get(suit);
	}

	public double pointsValue()
	{
		return pointMap.get(rank);
	}


	private static final List<Card> protoDeck = new ArrayList<Card>();

	// Initialize prototype deck
	static
	{
		for (final Suit suit : Suit.values())
		{
			for (final Rank rank : Rank.values())
			{
				protoDeck.add(new Card(rank, suit));
			}
		}
	}

	public static ArrayList<Card> newDeck()
	{

		Collections.shuffle(protoDeck);
		return new ArrayList<Card>(protoDeck); // Return copy of prototype deck

	}

	@Override
	public boolean equals(final Object c2)
	{
		return c2 != null && (c2 instanceof Card) && this.suit.equals(((Card) c2).suit) && this.rank.equals(((Card) c2).rank);
	}
}
