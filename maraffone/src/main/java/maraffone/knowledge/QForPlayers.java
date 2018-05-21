package maraffone.knowledge;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import maraffone.card.Card;
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
public class QForPlayers implements Knowledge {

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
   private Table<String,Card,Double> Q;


   private QForPlayers() {
      Q = HashBasedTable.create();
   }

   public Table<String,Card,Double> getQ()
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
         Set<String> rowKeySet = Q.rowKeySet();
         for (String hash : rowKeySet) {
            Map<Card, Double> row = Q.row(hash);
            bw.write(hash + "|");
            for (Card action : row.keySet()) {
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
