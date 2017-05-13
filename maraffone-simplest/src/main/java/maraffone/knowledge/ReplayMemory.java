package maraffone.knowledge;

import com.google.common.collect.EvictingQueue;


/**
 */
public class ReplayMemory {


   private static ReplayMemory replayMemoryInstance = new ReplayMemory();

   private EvictingQueue<Replay> replayList; // = EvictingQueue.create(1024*16);


   private ReplayMemory() {
      replayList = EvictingQueue.create(1024 * 16);
   }


   public static ReplayMemory getInstance() {
      return replayMemoryInstance;
   }


   public void addReplay(Replay replay) {
      replayList.add(replay);
   }


   public int getSize() {
      return this.replayList.size();
   }

   public Replay getElement(int i) {
      return (Replay) this.replayList.toArray()[i];
   }
}
