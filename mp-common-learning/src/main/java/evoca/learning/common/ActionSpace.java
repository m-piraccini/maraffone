package evoca.learning.common;

import java.util.List;


/**
 * Created by mattia on 05/05/17.
 */
public interface ActionSpace<A> {

   A random();

   List<A> getAvailableActions();

   int[] encode();

   String humanEncode();

}
