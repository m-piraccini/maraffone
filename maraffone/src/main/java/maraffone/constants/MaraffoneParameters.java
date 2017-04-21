package maraffone.constants;

import maraffone.player.HighestCardPlayer;
import maraffone.player.Player;
import maraffone.player.QPlayer;
import maraffone.player.RandomPlayer;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class MaraffoneParameters {

   public static final boolean enableLogLevel = true;


   public static final int MATCHES_LEARNING_TRIALS = 50000;

   public static final int MATCHING_SAMPLING = 1000;




   public static List<Player> createAndConfigurePlayers() {
      ArrayList<Player> players = new ArrayList<>();
      Player player;

      for (int p = 0; p < MaraffoneConstants.PLAYER_NUMBER; p++) {
         if (p % 2 == 0)
            player = new RandomPlayer();
         else
            player = new RandomPlayer();

         player.setName("P" + p);
         players.add(player);
      }
      return players;
   }

}

