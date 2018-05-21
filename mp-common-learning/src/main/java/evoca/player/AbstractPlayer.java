package evoca.player;

import evoca.learning.common.Action;
import evoca.learning.common.Environment;
import org.apache.log4j.Logger;


/**
 *
 * This player is the actual intelligent agent and can be customized using Qtable, ann, dnq, others
 *
 */
public abstract class AbstractPlayer<A extends Action, E extends Environment> implements Player {

   private final static Logger LOG = Logger.getLogger(AbstractPlayer.class);

   private int id;
   private String name;
   private double cumulatedPointsInMatches;
   private Player nextPlayer;

   @Override
   public String getName() {
      return name;
   }


   @Override
   public void setName(String name) {
      this.name = name;
   }


   @Override
   public double getCumulatedPointsInMatches() {
      return cumulatedPointsInMatches;
   }

   @Override
   public void setCumulatedPointsInMatches(double cumulatedPointsInMatches) {
      this.cumulatedPointsInMatches = cumulatedPointsInMatches;
   }

   @Override
   public Player getNextPlayer() {
      return nextPlayer;
   }

   @Override
   public void setNextPlayer(Player nextPlayer) {
      this.nextPlayer = nextPlayer;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }
}
