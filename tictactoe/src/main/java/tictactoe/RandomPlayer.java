package tictactoe;

import evoca.learning.common.Action;
import evoca.learning.common.ActionEffect;
import evoca.learning.common.Environment;
import evoca.player.AbstractPlayer;
import tictactoe.action.TictactoeAction;
import tictactoe.env.TictactoeTable;


/**
 * Created by mattia on 06/05/17.
 */
public class RandomPlayer extends TttPlayer {


   @Override
   public Action act(Environment environment) {

      TictactoeAction action = ((TictactoeTable) environment).getActionSpace(this).random();
      ActionEffect effect = environment.step(this, action);
      return action;
   }


   @Override
   public double[] encode() {
      // No need to encode
      return new double[0];
   }

   @Override
   public String humanEncode() {
      return this.getName();
   }
}
