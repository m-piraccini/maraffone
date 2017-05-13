package maraffone.constants;

import maraffone.player.Player;
import maraffone.player.RandomPlayer;
import maraffone.player.ann.DqnPlayer;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class MaraffoneParameters {

   public static final boolean enableLogLevel = true;


   public static final int MATCHES_LEARNING_TRIALS = 5000;

   public static final int MATCHING_SAMPLING = 100;
   public static final double EGREEDY_POLICY_EPS = 0.2d;
   public static final boolean TRAINING_ENABLED = Boolean.FALSE;


   public class AnnParameters{

      public static final int STATE_SIZE = 20;
      public static final int HIDDEN_LAYER_1_COUNT = 500;
      public static final int BATCH_SIZE = 100;
      // represents the gamma parameter
      public static final double DISCOUNT = 0.95d;
   }


   public interface Q_PLAYER{
      public Double ALPHA = 0.1;
      public Double GAMMA = 1.;
   }



   public static List<Player> createAndConfigurePlayers() {
      ArrayList<Player> players = new ArrayList<>();
      Player player;

      for (int p = 0; p < MaraffoneConstants.PLAYER_NUMBER; p++) {
         if (p % 2 == 0)
            player = new DqnPlayer();
         else
            player = new RandomPlayer();

         player.setName("P" + p);
         players.add(player);
      }
      return players;
   }

}

