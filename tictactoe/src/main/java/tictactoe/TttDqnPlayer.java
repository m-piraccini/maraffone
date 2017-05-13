package tictactoe;

import evoca.learning.common.Action;
import evoca.learning.common.ActionEffect;
import evoca.learning.common.Environment;
import evoca.learning.startegies.dqn.DqnForPlayers;
import evoca.learning.startegies.dqn.Replay;
import evoca.player.AbstractPlayer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import tictactoe.action.TictactoeAction;
import tictactoe.env.TictactoeActionSpace;
import tictactoe.env.TictactoeTable;

import java.util.Random;


/**
 * Created by mattia on 07/05/17.
 */
public class TttDqnPlayer extends TttPlayer {

   private DqnForPlayers dqn;
   Random r = new Random(123);
   int plays = 0;
   //   private Policy playingPolicy;

   public TttDqnPlayer(DqnForPlayers dqn) {
      this.dqn = dqn;
   }

   @Override
   public Action act(Environment environment) {
      plays++;
      double[] currentState = environment.encode();

      // retrieve maximum action
      TictactoeActionSpace actionSpace = ((TictactoeTable) environment).getActionSpace(this);

      INDArray output = dqn.output(actionSpace.encode());

      int anInt = 0;
      if (r.nextDouble() > (1d / Math.pow(plays, 1 / 3))) {
         anInt = DqnForPlayers.findMaxArg(output, actionSpace.encode());
      }
      else {
         TictactoeAction randomAction = actionSpace.random();
         anInt = randomAction.getPlayer().getId() * 9 + randomAction.getRow() * 3 + randomAction.getCol();
      }


      TictactoeAction action = new TictactoeAction((anInt % 9) / 3, (anInt % 9) % 3, this);
      ActionEffect effect = environment.step(this, action);

      double[] nextState = environment.encode();

      //      dqn.storeReplay(new Replay(Nd4j.create(currentState), action, effect.getReward(), environment.done() ? null : Nd4j.create(nextState),
      //            environment.done() ? null : actionSpace.encode()));

      dqn.storeReplay(
            new Replay(Nd4j.create(currentState), action, effect.getReward(), environment.done() ? null : Nd4j.create(nextState),
                  environment.done() ? null : actionSpace.encode()));


      return null;
   }


   @Override
   public double[] encode() {
      return new double[0];
   }

   @Override
   public String humanEncode() {
      return null;
   }
}
