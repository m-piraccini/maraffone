//package evoca.policy;
//
//import lombok.AllArgsConstructor;
//import org.deeplearning4j.rl4j.learning.Learning;
//import org.deeplearning4j.rl4j.network.dqn.DQN;
//import org.deeplearning4j.rl4j.network.dqn.IDQN;
//import org.deeplearning4j.rl4j.space.Encodable;
//import org.nd4j.linalg.api.ndarray.INDArray;
//
//
///**
// * @author rubenfiszel (ruben.fiszel@epfl.ch) 7/18/16.
// *
// * DQN policy returns the action with the maximum Q-value as evaluated
// * by the dqn model
// */
//@AllArgsConstructor
//public class DQNPolicy<O extends Encodable> extends Policy<O, Integer> {
//
//    final private IDQN dqn;
//
//    public static <O extends Encodable> DQNPolicy<O> load(String path) {
//        return new DQNPolicy<O>(DQN.load(path));
//    }
//
//    public Integer nextAction(INDArray input) {
//        INDArray output = dqn.output(input);
//        return Learning.getMaxAction(output);
//    }
//
//    public void save(String filename) {
//        dqn.save(filename);
//    }
//
//}
