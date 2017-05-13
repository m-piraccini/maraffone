package evoca.learning.configuration;

/**
 * Created by mattia on 07/05/17.
 */
public class DQNParams {


   public DQNParams(int inputLength, int hiddenLayers, int outputLength, double learningRate, double discount){
      this.discount = discount;
      this.hiddenLayers = hiddenLayers;
      this.inputLength = inputLength;
      this.learningRate = learningRate;
      this.outputLength = outputLength;
   }


   public int inputLength;
   public int hiddenLayers;
   public int outputLength;
   public double learningRate;
   public double discount;


}
