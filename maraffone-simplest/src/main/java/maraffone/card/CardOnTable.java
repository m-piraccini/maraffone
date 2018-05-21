package maraffone.card;

import maraffone.player.Player;


public class CardOnTable {

   private Card card;
   private Player player;
   private int positionInTurn;


   public Card getCard() {
      return card;
   }

   public void setCard(Card card) {
      this.card = card;
   }

   public Player getPlayer() {
      return player;
   }

   public void setPlayer(Player player) {
      this.player = player;
   }

   public int getPositionInTurn() {
      return positionInTurn;
   }

   public void setPositionInTurn(int positionInTurn) {
      this.positionInTurn = positionInTurn;
   }
}
