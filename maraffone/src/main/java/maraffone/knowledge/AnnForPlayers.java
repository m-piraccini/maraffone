package maraffone.knowledge;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.conf.preprocessor.CnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.conf.preprocessor.FeedForwardToCnnPreProcessor;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.lossfunctions.LossFunctions;


/**
 * Implements a ANN agent state representation for players
 */
public class AnnForPlayers implements Knowledge{
   private static AnnForPlayers ourInstance = new AnnForPlayers();

   public static AnnForPlayers getInstance() {
      return ourInstance;
   }



   private AnnForPlayers() {
   }



   @Override
   public void printLearning() {

   }


   public MultiLayerConfiguration getAnn(){

      return getConfiguration();
   }


       public static MultiLayerConfiguration getConfiguration() {
//           MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
//                   .seed(SEED)
//   //                .batchSize(BATCH_SIZE)
//                   .iterations(ITERATIONS)
//                   .momentum(0.9)
//                   .regularization(true)
//   //                .constrainGradientToUnitNorm(true)
//                   .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
//                   .list(6)
//                   .layer(0, new ConvolutionLayer.Builder(new int[]{5, 5})
//                           .nIn(1)
//                           .nOut(20)
//                           .stride(new int[]{1, 1})
//                           .activation("relu")
//                           .weightInit(WeightInit.XAVIER)
//                           .build())
//                   .layer(1, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX, new int[]{2, 2})
//                           .build())
//                   .layer(2, new ConvolutionLayer.Builder(new int[]{5, 5})
//                           .nIn(20)
//                           .nOut(40)
//                           .stride(new int[]{1, 1})
//                           .activation("relu")
//                           .weightInit(WeightInit.XAVIER)
//                           .build())
//                   .layer(3, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX, new int[]{2, 2})
//                           .build())
//                   .layer(4, new DenseLayer.Builder()
//                           .nIn(40 * 5 * 5)
//                           .nOut(1000)
//                           .activation("relu")
//                           .weightInit(WeightInit.XAVIER)
//                           .dropOut(0.5)
//                           .build())
//                   .layer(5, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
//                           .nIn(1000)
//                           .nOut(LABELS.size())
//                           .dropOut(0.5)
//                           .weightInit(WeightInit.XAVIER)
//                           .build())
//                   .inputPreProcessor(0, new FeedForwardToCnnPreProcessor(WIDTH, HEIGHT, 1))
//                   .inputPreProcessor(4, new CnnToFeedForwardPreProcessor())
//                   .backprop(true).pretrain(false)
//                   .build();

           return null;
       }
}
