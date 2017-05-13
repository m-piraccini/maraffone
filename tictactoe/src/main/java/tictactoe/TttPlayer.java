package tictactoe;

import evoca.learning.common.Action;
import evoca.learning.common.Environment;
import evoca.player.AbstractPlayer;
import tictactoe.action.PlayerSymbol;


/**
 * Created by mattia on 07/05/17.
 */
public abstract class TttPlayer extends AbstractPlayer<Action, Environment> {

   private PlayerSymbol symbol;

   public PlayerSymbol getSymbol() {
      return symbol;
   }

   public void setSymbol(PlayerSymbol symbol) {
      this.symbol = symbol;
   }
}
