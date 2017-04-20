package maraffone;

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

}
