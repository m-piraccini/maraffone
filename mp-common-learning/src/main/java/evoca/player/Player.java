package evoca.player;

import evoca.learning.common.Action;
import evoca.learning.common.Encodable;
import evoca.learning.common.Environment;


/**

 */
public interface Player<A extends Action, E extends Environment> extends Encodable{


   String getName();


   void setName(String name);

   void setNextPlayer(Player p);

   Player getNextPlayer();


   void setCumulatedPointsInMatches(double cumulatedPointsInMatches);


   double getCumulatedPointsInMatches();


   A act(E environment);

   int getId();
}
