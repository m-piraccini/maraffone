package evoca.learning.common;

/**
 * Created by mattia on 06/05/17.
 */
public class ActionEffect {
   private double reward;


   public ActionEffect(double reward){
      this.reward = reward;
   }

   public double getReward() {
      return reward;
   }

   public void setReward(double reward) {
      this.reward = reward;
   }
}
