package evoca.learning.startegies.dqn;

import evoca.learning.configuration.DQNParams;
import evoca.learning.configuration.ReplyMemoryParams;
import org.apache.log4j.Logger;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.Arrays;
import java.util.Random;


/**
 * Implements a ANN agent state representation for players
 */
public class DqnForPlayers {

   private final static Logger LOG = Logger.getLogger(DqnForPlayers.class);

   private ReplayMemory replayMemory;
   private DQNParams dqnParams;
   private MultiLayerNetwork targetDeepQ; // = new MultiLayerNetwork( getConfiguration());
   private MultiLayerNetwork tempDeepQ; // = new MultiLayerNetwork( getConfiguration() );
   private static Random randomGenerator = new Random(123);
   UIServer uiServer;


   public DqnForPlayers(ReplyMemoryParams memoryParams, DQNParams dqnParams, boolean enableVisualization){
      replayMemory = new ReplayMemory(memoryParams.getMaxMemorySize(), memoryParams.getBatchSize());
      this.dqnParams = dqnParams;

      targetDeepQ = new MultiLayerNetwork( getConfiguration());
      tempDeepQ = new MultiLayerNetwork( getConfiguration() );

      tempDeepQ.init();
      targetDeepQ.init();
      targetDeepQ.setParams(tempDeepQ.params());

      try {
         initVisualization(targetDeepQ ,enableVisualization);
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

   public ReplayMemory getMemory(){
      return replayMemory;
   }

   private void initVisualization(MultiLayerNetwork targetDeepQ, boolean trainingEnabled) throws Exception {
      if  (trainingEnabled) {
         //Initialize the user interface backend
         uiServer = UIServer.getInstance();

         //Configure where the network information (gradients, activations, score vs. time etc) is to be stored
         //Then add the StatsListener to collect this information from the network, as it trains
         StatsStorage statsStorage = new InMemoryStatsStorage();             //Alternative: new FileStatsStorage(File) - see UIStorageExample
         int listenerFrequency = 10;
         tempDeepQ.setListeners(new StatsListener(statsStorage, listenerFrequency));

         //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
         uiServer.attach(statsStorage);
      }
   }


   public void stopSimulation(){
      uiServer.stop();
   }



   public MultiLayerNetwork getAnn() {
      return targetDeepQ;
   }


   public MultiLayerConfiguration getConfiguration() {


      final MultiLayerConfiguration conf1 = new NeuralNetConfiguration.Builder().seed(123).iterations(1)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).learningRate(dqnParams.learningRate).updater(Updater.NESTEROVS)
            .momentum(0.95).list()
            .layer(0,
                  new DenseLayer.Builder().nIn(dqnParams.inputLength).nOut(dqnParams.hiddenLayers).weightInit(WeightInit.XAVIER)
                        .activation(Activation.IDENTITY).build())
            .layer(1,
                  new DenseLayer.Builder().nIn(dqnParams.hiddenLayers).nOut(dqnParams.hiddenLayers).weightInit(WeightInit.XAVIER)
                        .activation(Activation.IDENTITY).build())
            .layer(2,
                  new OutputLayer.Builder(LossFunctions.LossFunction.MSE).weightInit(WeightInit.XAVIER).activation(Activation.IDENTITY)
                        .weightInit(WeightInit.XAVIER).nIn(dqnParams.hiddenLayers).nOut(dqnParams.outputLength).build()).pretrain(false)
            .backprop(true).build();
      return conf1;
   }



   public void trainDqn(){
      if (true) //dqnParams. < replayMemory.getSize())
      {
         trainNetwork();
      }
   }



   private void trainNetwork()
   {
      Replay[] replays = replayMemory.getBatchArray();
      final INDArray currInputs = combineInputs(replays);
      final INDArray targetInputs = combineNextInputs(replays);

      float TotalError = 0;

      final INDArray CurrOutputs = tempDeepQ.output(currInputs);
      final INDArray targetOutputs = targetDeepQ.output(targetInputs);
      final int y = replays.length;
      for (int i = 0; i < y; i++)
      {
         final int ind[] = { i, replays[i].action.getId() };
         double futureReward = 0;
         if (replays[i].nextState != null)
         {
            futureReward = findMax(targetOutputs.getRow(i), replays[i].nextActionMask);
         }
         final double targetReward = replays[i].reward + dqnParams.discount * futureReward;
         TotalError += (targetReward - CurrOutputs.getDouble(ind)) * (targetReward - CurrOutputs.getDouble(ind));
         CurrOutputs.putScalar(ind, targetReward);
      }
//      LOG.info("Avgerage Error: " + (TotalError / y));

      tempDeepQ.fit(currInputs, CurrOutputs);
      reconcileNetworks();
   }


   public static double findMax(final INDArray netOutputs, final int actionMask[])
   {
      int i = 0;
      while (actionMask[i] == 0)
      {
         i++;
      }

      double maxVal = netOutputs.getFloat(i);
      for (; i < netOutputs.size(1); i++)
      {
         if (netOutputs.getDouble(i) > maxVal && actionMask[i] == 1)
         {
            maxVal = netOutputs.getDouble(i);
         }
      }
      return maxVal;
   }


   public static int findMaxArg(INDArray netOutputs, int[] actionMask) {

      double maxVal = Integer.MIN_VALUE;
      int argMax = 0;
      for (int i = 0; i < netOutputs.size(1); i++)
      {
         if (netOutputs.getDouble(i) > maxVal && actionMask[i] == 1)
         {
            maxVal = netOutputs.getDouble(i);
            argMax = i;
         }
      }
      return argMax;
   }


   private INDArray combineInputs(final Replay[] replays)
   {
      final INDArray retVal = Nd4j.create(replays.length, dqnParams.inputLength);
      for (int i = 0; i < replays.length; i++)
      {
         retVal.putRow(i, replays[i].state);
      }
      return retVal;
   }


   INDArray combineNextInputs(final Replay replays[])
   {
      final INDArray retVal = Nd4j.create(replays.length, dqnParams.inputLength);
      for (int i = 0; i < replays.length; i++)
      {
         if (replays[i].nextState != null)
         {
            retVal.putRow(i, replays[i].nextState);
         }
      }
      return retVal;
   }


   public void storeReplay(Replay transition){
      replayMemory.store(transition);
   }


   void reconcileNetworks()
   {
      targetDeepQ.setParams(tempDeepQ.params());
   }


   public INDArray output(int[] encode) {
      return targetDeepQ.output(Nd4j. create(Arrays.stream(encode).asDoubleStream().toArray()));
   }


}
