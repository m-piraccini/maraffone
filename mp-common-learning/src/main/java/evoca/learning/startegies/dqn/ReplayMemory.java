package evoca.learning.startegies.dqn;

import evoca.learning.common.Action;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.apache.commons.lang3.ArrayUtils.toArray;


/**
 */
public class ReplayMemory<A extends Action> {


   final private Logger log = LoggerFactory.getLogger("Exp Replay");
   final private int batchSize;
   Random random = new Random(1234567);

   //Implementing this as a circular buffer queue
   private CircularFifoQueue<Replay<A>> storage;

   public ReplayMemory(int maxSize, int batchSize) {
      this.batchSize = batchSize;
      storage = new CircularFifoQueue<Replay<A>>(maxSize);
   }


   public int getSize(){
      return storage.size();
   }


   public List<Replay<A>> getBatch(int size) {

      int minSize = Math.min(size, storage.size());
      Set<Integer> intSet = new HashSet<>();
      while (intSet.size() < minSize) {
         int rd = random.nextInt(minSize);
         intSet.add(rd);
      }

      ArrayList<Replay<A>> batch = new ArrayList<>(minSize);
      Iterator<Integer> iter = intSet.iterator();
      while (iter.hasNext()) {
         Replay<A> trans = storage.get(iter.next());
         batch.add(trans);
      }

      return batch;
   }

   public List<Replay<A>> getBatch() {
      return getBatch(batchSize);
   }

   public Replay<A>[] getBatchArray() {
      List<Replay<A>> batch = getBatch(batchSize);
      return batch.toArray(new Replay[batch.size()]);
   }


   public void store(Replay<A> transition) {
      storage.add(transition);
//      log.info("size: "+storage.size());
   }

}
