package maraffone.card;

import maraffone.player.PlayerCard;

import java.util.Comparator;


/**
 *
 */
public class CardComparator implements Comparator<Card>
{

	@Override
	public int compare(final Card o1, final Card o2)
	{
		final int suitOrder = o2.suitPriority() - o1.suitPriority();
		return (suitOrder == 0 ? o2.rankPriority() - o1.rankPriority() : suitOrder);

	}


//	public int compareCards(final Card o1, final Card o2)
//	{
//		final int suitOrder = o2.suitPriority() - o1.suitPriority();
//		return (suitOrder == 0 ? o2.rankPriority() - o1.rankPriority() : suitOrder);
//	}


	public static int comparePlayedCards(final PlayerCard o1, final PlayerCard o2)
	{
		final int suitOrder = o2.getCard().suitPriority() - o1.getCard().suitPriority();
		return (suitOrder == 0 ? o2.getCard().rankPriority() - o1.getCard().rankPriority() : suitOrder);

	}

}
