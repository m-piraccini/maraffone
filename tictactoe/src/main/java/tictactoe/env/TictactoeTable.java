package tictactoe.env;

import evoca.learning.common.ActionEffect;
import evoca.learning.common.Environment;
import evoca.player.Player;
import tictactoe.TttPlayer;
import tictactoe.action.TictactoeAction;

import java.util.ArrayList;
import java.util.List;


/**
 */
public class TictactoeTable implements Environment<TictactoeAction, TictactoeActionSpace> {


   static int TABLE_LENGTH = 3;

   Player[] players;
   double[] status = new double[TABLE_LENGTH*TABLE_LENGTH*2];

   Player[][] actionTable = new Player[TABLE_LENGTH][TABLE_LENGTH];
   private int turn = 0;
   TictactoeActionSpace tictactoeActionSpace;

   public TictactoeTable(Player[] players){
      this.players = players;
      tictactoeActionSpace = new TictactoeActionSpace();
   }


   @Override
   public ActionEffect step(Player player, TictactoeAction action) {
      actionTable[action.getRow()][action.getCol()] = player;
      turn++;
      return new ActionEffect(getReward(player));
   }


   boolean hasWon(Player player) {

      int count = 0;
      for (int r = 0; r < TABLE_LENGTH; r++) {
         count = 0;
         for (int c = 0; c < TABLE_LENGTH; c++) {
            if (player.equals(actionTable[r][c]))
               count++;
         }
         if (count == TABLE_LENGTH)
            return true;
      }


      for (int c = 0; c < TABLE_LENGTH; c++) {
         count = 0;
         for (int r = 0; r < TABLE_LENGTH; r++) {
            if (player.equals(actionTable[r][c]))
               count++;
         }
         if (count == TABLE_LENGTH)
            return true;
      }

      for (int c = 0; c < TABLE_LENGTH; c++) {
         count = 0;
         if (player.equals(actionTable[c][c])) {
            count++;
         }
         if (count == TABLE_LENGTH)
            return true;
      }

      for (int c = 0; c < TABLE_LENGTH; c++) {
         count = 0;
         if (player.equals(actionTable[c][TABLE_LENGTH - c - 1])) {
            count++;
         }
         if (count == TABLE_LENGTH)
            return true;
      }

      return false;
   }


   @Override
   public double[] encode() {

      status = new double[TABLE_LENGTH*TABLE_LENGTH*2];
      for (int i = 0; i < players.length; i++) {

         for (int j = 0; j < TABLE_LENGTH*TABLE_LENGTH; j++) {
            if(actionTable[j/TABLE_LENGTH][j%TABLE_LENGTH] == players[i])
               status[j+ TABLE_LENGTH*TABLE_LENGTH*i] = 1;
         }
      }
      return status;

   }


   @Override
   public String humanEncode() {
      String encode = new String();
      for (int i = 0; i < TABLE_LENGTH; i++) {
         System.out.println();
         System.out.print("|");
         for (int j = 0; j < TABLE_LENGTH; j++) {
            System.out.print(actionTable[i][j] == null ? " ": ((TttPlayer)actionTable[i][j]).getSymbol().getValue());
         }
         System.out.print("|");
      }
      System.out.println();
      return "";
   }


   @Override
   public double getReward(Player player) {
      return hasWon(player) ? 1 : 0 ;
   }



   @Override
   public TictactoeActionSpace getActionSpace(Player player) {
      tictactoeActionSpace.reset();
      List<TictactoeAction> list = new ArrayList<>();
      for (int i = 0; i < TABLE_LENGTH*TABLE_LENGTH; i++) {
         if (actionTable[i/TABLE_LENGTH][i%TABLE_LENGTH] == null){
            TictactoeAction action = new TictactoeAction(i/TABLE_LENGTH, i%TABLE_LENGTH, player);
            list.add(action);
         }
      }
      tictactoeActionSpace.setAvailableActions(list);
      return tictactoeActionSpace;
   }


   @Override
   public boolean done() {
      return hasWon(players[0]) || hasWon(players[1]) || !areCellsAvailable();
   }


   private boolean areCellsAvailable() {
      for (int i = 0; i < TABLE_LENGTH*TABLE_LENGTH; i++) {
         if (actionTable[i/TABLE_LENGTH][i%TABLE_LENGTH] == null) return true;
      }
      return false;
   }


   @Override
   public void reset() {
      for (int i = 0; i < TABLE_LENGTH*TABLE_LENGTH; i++) {
         actionTable[i/TABLE_LENGTH][i%TABLE_LENGTH] = null;
      }
      turn = 0;
   }

}
