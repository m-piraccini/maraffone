package evoca.learning.configuration;

/**
 * Created by mattia on 07/05/17.
 */
public class ReplyMemoryParams {

   private int maxMemorySize;
   private int batchSize;

   public ReplyMemoryParams(int maxMemorySize, int batchSize) {
      this.maxMemorySize = maxMemorySize;
      this.batchSize = batchSize;
   }

   public int getMaxMemorySize() {
      return maxMemorySize;
   }

   public void setMaxMemorySize(int maxMemorySize) {
      this.maxMemorySize = maxMemorySize;
   }

   public int getBatchSize() {
      return batchSize;
   }

   public void setBatchSize(int batchSize) {
      this.batchSize = batchSize;
   }
}
