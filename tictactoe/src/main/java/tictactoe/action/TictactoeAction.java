package tictactoe.action;

import evoca.learning.common.Action;
import evoca.player.Player;


/**
 * Created by mattia on 05/05/17.
 */
public class TictactoeAction implements Action {

   private int row;
   private int col;
   private Player player;


   public TictactoeAction(int row, int col, Player player) {
      this.row = row;
      this.col = col;
      this.player = player;
   }

   public int getRow() {
      return row;
   }

   public void setRow(int row) {
      this.row = row;
   }

   public int getCol() {
      return col;
   }

   public void setCol(int col) {
      this.col = col;
   }

   public Player getPlayer() {
      return player;
   }

   public void setPlayer(Player symbol) {
      this.player = symbol;
   }


   @Override
   public int getId() {
      return player.getId()*9+row*3+col;
   }
}
