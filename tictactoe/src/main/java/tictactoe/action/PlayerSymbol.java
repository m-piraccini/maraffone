package tictactoe.action;

/**
 * Created by mattia on 05/05/17.
 */
public enum PlayerSymbol {


   CROSS("X"), CIRCLE("O");

   private final String id;

   PlayerSymbol(final String id) {
      this.id = id;
   }

   public String getValue() {
      return id;
   }
}
