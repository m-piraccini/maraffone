package evoca.learning;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import evoca.learning.common.Action;
import evoca.learning.common.Encodable;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;


/**
 */
public class QForPlayers<A extends Action, E extends Encodable>{

   private final static Logger LOG = Logger.getLogger(QForPlayers.class);

   private static QForPlayers ourInstance = new QForPlayers();

   public static QForPlayers getInstance() {
      return ourInstance;
   }


   /**
    * returns Q table
    * s = String = state
    * a = Card = action
    * Q(s,a) = double = value
    */
   private Table<E,A,Double> Q;


   private QForPlayers() {
      Q = HashBasedTable.create();
   }


   public Table<E,A,Double> getQ()
   {
      return Q;
   }


   public void printLearning() {

      FileWriter bw = null;
      try {
         File file = getOutputFileForKnowledge();
         bw = new FileWriter(file);

         LOG.info("");
         LOG.info("Printing Q");
         Set<E> rowKeySet = Q.rowKeySet();
         for (E hash : rowKeySet) {
            Map<A, Double> row = Q.row(hash);
            bw.write(hash.toString() + "|");
            for (A action : row.keySet()) {
               bw.write("\t" + BigDecimal.valueOf(row.get(action)).setScale(2, RoundingMode.HALF_DOWN).toString() + ";");
            }
            bw.write("\n");
         }
         bw.close();
      }
      catch (FileNotFoundException e) {
         e.printStackTrace();
      }
      catch (IOException e) {
         e.printStackTrace();
      }
   }


   protected File getOutputFileForKnowledge() {
      return new File("q.txt");
   }
}
