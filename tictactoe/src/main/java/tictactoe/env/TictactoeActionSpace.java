package tictactoe.env;

import evoca.learning.common.ActionSpace;
import tictactoe.action.TictactoeAction;

import java.util.List;
import java.util.Random;


/**
 * Created by mattia on 06/05/17.
 */
public class TictactoeActionSpace implements ActionSpace<TictactoeAction> {

   Random r = new Random(12345);
   private List<TictactoeAction> availableActions = null;


   @Override
   public TictactoeAction random() {
      return availableActions.get(r.nextInt(availableActions.size()));
   }


   @Override
   public List<TictactoeAction> getAvailableActions() {
      return availableActions;
   }

   public void setAvailableActions(List<TictactoeAction> availableActions) {
      this.availableActions = availableActions;
   }


   @Override
   public int[] encode() {
      int[] doubles = new int[9*2];
      for (TictactoeAction action: availableActions) {
         doubles[9*action.getPlayer().getId() + action.getRow()*3+action.getCol()] = 1;
      }
      return doubles;
   }


   @Override
   public String humanEncode() {
      return null;
   }


   public void reset() {
      if ( availableActions != null) availableActions.clear();
   }
}
