package tictactoe;


import evoca.learning.common.ActionEffect;
import evoca.learning.configuration.DQNParams;
import evoca.learning.configuration.ReplyMemoryParams;
import evoca.learning.startegies.dqn.DqnForPlayers;
import evoca.learning.startegies.dqn.ReplayMemory;
import evoca.player.Player;
import org.apache.log4j.Logger;
import tictactoe.action.PlayerSymbol;
import tictactoe.action.TictactoeAction;
import tictactoe.env.TictactoeTable;

import java.util.ArrayList;
import java.util.List;


public class TictactoeApplication {

   private final static Logger LOG = Logger.getLogger(TictactoeApplication.class);
   private static final int MATCHES_LEARNING_TRIALS = 100000;
   private static final int PLAYER_NUMBER = 2;

   private List<Player> players;
   private TictactoeTable table;
   private Player firstMatchPlayer = null;
   private DqnForPlayers dqn;

   private void go() {

      initializeLearning();
      initializePlayers();
      table = new TictactoeTable(players.toArray(new Player[players.size()]));

      // matches (epochs) to play?
      for (int match_number = 0; match_number < MATCHES_LEARNING_TRIALS; match_number++) {


         prepareForNewMatch();

         firstMatchPlayer = defineFirstPlayerForMatch(firstMatchPlayer, players);
         Player firstPlayer = firstMatchPlayer;
         Player<TictactoeAction, TictactoeTable> currentPlayer = firstPlayer;

         // there are 10 turns in a match
         while (!table.done()) {

            TictactoeAction act = currentPlayer.act(table);

            currentPlayer = currentPlayer.getNextPlayer();
         }

         if (match_number % 100 == 0 ){
            LOG.info(table.humanEncode());
            LOG.info("Matches played: " + match_number);

            double p0points = players.get(0).getCumulatedPointsInMatches();
            double p1points = players.get(1).getCumulatedPointsInMatches();
            LOG.info("Cumulated points p0: "+p0points);
            LOG.info("Cumulated points p1: "+p1points);
            LOG.info("points difference: "+Math.abs(p1points -p0points));
            LOG.info("points ratio: "+(p1points<p0points ?p1points /p0points :p0points /p1points));
            ReplayMemory memory = dqn.getMemory();
            memory.getSize();
         }


         players.get(0).setCumulatedPointsInMatches(players.get(0).getCumulatedPointsInMatches() + table.getReward(players.get(0)));
         players.get(1).setCumulatedPointsInMatches(players.get(1).getCumulatedPointsInMatches() + table.getReward(players.get(1)));
         dqn.trainDqn();
      }


      LOG.info("match ended!");

      double p0points = players.get(0).getCumulatedPointsInMatches();
      double p1points = players.get(1).getCumulatedPointsInMatches();
      LOG.info("Cumulated points p0: "+p0points);
      LOG.info("Cumulated points p1: "+p1points);
      LOG.info("points difference: "+Math.abs(p1points -p0points));
      LOG.info("points ratio: "+(p1points<p0points ?p1points /p0points :p0points /p1points));
   }

   private void initializeLearning() {
      ReplyMemoryParams memoryParams = new ReplyMemoryParams(128, 32);
      DQNParams dqnParams = new DQNParams(18, 54, 18, 0.00025, 0.90 );
      dqn = new DqnForPlayers(memoryParams,dqnParams, true);
   }


   public TictactoeApplication() {}



   private void initializePlayers() {

      players = createAndConfigurePlayers();
      establishPlayerTurnAndMates();
   }


   private void establishPlayerTurnAndMates() {
         players.get(0).setNextPlayer(players.get(1));
         players.get(1).setNextPlayer(players.get(0));
   }



   /**
    * returns always the subsequent player for fairness
    * @param firstPlayer
    * @param players
    * @return
    */
   private Player defineFirstPlayerForMatch(Player firstPlayer, List<Player> players) {
      return firstPlayer != null ? firstPlayer.getNextPlayer() : players.get(0);
   }


   private void prepareForNewMatch() {
      table.reset();
   }


   public List<Player> createAndConfigurePlayers() {
      ArrayList<Player> players = new ArrayList<>();
      Player player;

      for (int p = 0; p < PLAYER_NUMBER; p++) {
         if (p == 0){
            player = new TttDqnPlayer(dqn);
            ((TttDqnPlayer)player).setSymbol(PlayerSymbol.CIRCLE);
         }
         else{
            player = new TttDqnPlayer(dqn);
            ((TttDqnPlayer)player).setSymbol(PlayerSymbol.CROSS);
         }
         ((TttDqnPlayer) player).setId(p);
         player.setName("P" + p);
         players.add(player);
      }
      return players;
   }


   public static void main(String[] args) {
      new TictactoeApplication().go();
   }

}
