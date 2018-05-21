package evoca.learning.common;

import evoca.player.Player;


/**
 */
public interface Environment<A, AS extends ActionSpace<A>> extends Encodable , Observable{


   ActionEffect step(Player player, A action);


   double getReward(Player player);

   /**
    * An ActionSpace is related to a particular player (other players might have different spaces
    * @param player
    * @return
    */
   AS getActionSpace(Player player);

   boolean done();

   void reset();


}
