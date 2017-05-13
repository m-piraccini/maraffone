package maraffone.knowledge;

import maraffone.card.Card;
import maraffone.constants.MaraffoneParameters;
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
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.IOException;
import java.util.Random;


/**
 * Implements a ANN agent state representation for players
 */
public class DqnForPlayers implements Knowledge {

   private final static Logger LOG = Logger.getLogger(DqnForPlayers.class);

   private static DqnForPlayers ourInstance = new DqnForPlayers();
   private static ReplayMemory replayMemory; // = new ReplayMemory();
   private static MultiLayerNetwork targetDeepQ; // = new MultiLayerNetwork( getConfiguration());
   private static MultiLayerNetwork tempDeepQ; // = new MultiLayerNetwork( getConfiguration() );

   private static Random randomGenerator = new Random(123);

   public static DqnForPlayers getInstance() {
      return ourInstance;
   }


   UIServer uiServer;


   final static int inputLength = MaraffoneParameters.AnnParameters.STATE_SIZE;
   final static int outputLength = Card.CARDS_IN_DECK;
   final static int replayStartSize = 1024;

   private DqnForPlayers(){
      replayMemory = ReplayMemory.getInstance();
      targetDeepQ = new MultiLayerNetwork( getConfiguration());
      tempDeepQ = new MultiLayerNetwork( getConfiguration() );

      tempDeepQ.init();
      targetDeepQ.init();
      targetDeepQ.setParams(tempDeepQ.params());

      try {
         initVisualization(targetDeepQ ,MaraffoneParameters.TRAINING_ENABLED);
      }
      catch (Exception e) {
         e.printStackTrace();
      }
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



   @Override
   public void printLearning() {
         try {
            ModelSerializer.writeModel(targetDeepQ, System.out, true);
         } catch (IOException e) {
            e.printStackTrace();
         }
   }


   public MultiLayerNetwork getAnn() {
      return targetDeepQ;
   }


   public static MultiLayerConfiguration getConfiguration() {


      final int hiddenLayerCount = MaraffoneParameters.AnnParameters.HIDDEN_LAYER_1_COUNT;

      final MultiLayerConfiguration conf1 = new NeuralNetConfiguration.Builder().seed(123).iterations(1)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).learningRate(0.00025).updater(Updater.NESTEROVS)
            .momentum(0.95).list()
            .layer(0,
                  new DenseLayer.Builder().nIn(inputLength).nOut(hiddenLayerCount).weightInit(WeightInit.XAVIER)
                        .activation(Activation.RELU).build())
            .layer(1,
                  new DenseLayer.Builder().nIn(hiddenLayerCount).nOut(hiddenLayerCount).weightInit(WeightInit.XAVIER)
                        .activation(Activation.RELU).build())
            .layer(2,
                  new DenseLayer.Builder().nIn(hiddenLayerCount).nOut(hiddenLayerCount).weightInit(WeightInit.XAVIER)
                        .activation(Activation.RELU).build())
            .layer(3,
                  new DenseLayer.Builder().nIn(hiddenLayerCount).nOut(hiddenLayerCount).weightInit(WeightInit.XAVIER)
                        .activation(Activation.SOFTMAX).build())

            .layer(4,
                  new OutputLayer.Builder(LossFunctions.LossFunction.MSE).weightInit(WeightInit.XAVIER).activation(Activation.IDENTITY)
                        .weightInit(WeightInit.XAVIER).nIn(hiddenLayerCount).nOut(outputLength).build()).pretrain(false)
            .backprop(true).build();

      return conf1;
   }


   public ReplayMemory getReplayMemory() {
      return replayMemory;
   }



   public void trainDqn(){
      if (replayStartSize < replayMemory.getSize())
      {
         trainNetwork();
      }
   }



   private void trainNetwork()
   {
      final Replay replays[] = getMiniBatch(MaraffoneParameters.AnnParameters.BATCH_SIZE);
      final INDArray currInputs = combineInputs(replays);
      final INDArray targetInputs = combineNextInputs(replays);

      float TotalError = 0;

      final INDArray CurrOutputs = tempDeepQ.output(currInputs);
      final INDArray targetOutputs = targetDeepQ.output(targetInputs);
      final int y = replays.length;
      for (int i = 0; i < y; i++)
      {
         final int ind[] = { i, replays[i].action.getUniqueNumericIdentifier() };
         double futureReward = 0;
         if (replays[i].nextState != null)
         {
            futureReward = findMax(targetOutputs.getRow(i), replays[i].nextActionMask);
         }
         final double targetReward = replays[i].reward + MaraffoneParameters.AnnParameters.DISCOUNT * futureReward;
         TotalError += (targetReward - CurrOutputs.getDouble(ind)) * (targetReward - CurrOutputs.getDouble(ind));
         CurrOutputs.putScalar(ind, targetReward);
      }
      LOG.info("Avgerage Error: " + (TotalError / y));

      tempDeepQ.fit(currInputs, CurrOutputs);
      reconcileNetworks();
   }


   double findMax(final INDArray netOutputs, final int actionMask[])
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


   private INDArray combineInputs(final Replay replays[])
   {
      final INDArray retVal = Nd4j.create(replays.length, inputLength);
      for (int i = 0; i < replays.length; i++)
      {
         retVal.putRow(i, replays[i].state);
      }
      return retVal;
   }


   INDArray combineNextInputs(final Replay replays[])
   {
      final INDArray retVal = Nd4j.create(replays.length, inputLength);
      for (int i = 0; i < replays.length; i++)
      {
         if (replays[i].nextState != null)
         {
            retVal.putRow(i, replays[i].nextState);
         }
      }
      return retVal;
   }


   private Replay[] getMiniBatch(final int batchSize)
   {
      final int size = replayMemory.getSize() < batchSize ? replayMemory.getSize() : batchSize;
      final Replay[] retVal = new Replay[size];

      for (int i = 0; i < size; i++)
      {
         retVal[i] = replayMemory.getElement(randomGenerator.nextInt(replayMemory.getSize()));
      }
      return retVal;
   }



   void reconcileNetworks()
   {
      targetDeepQ.setParams(tempDeepQ.params());
   }

}
