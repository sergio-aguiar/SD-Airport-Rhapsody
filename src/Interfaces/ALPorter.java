package Interfaces;
/**
 * Porter Arrival Lounge Interface.
 * 
 * @author sergioaguiar
 * @author marcomacedo
 */
public interface ALPorter {
    /**
     * Porter takes a rest.
     * @param pid Porter id.
     * @return true if Porter take a rest or false otherwise.
     */
    public boolean takeARest(int pid);
    /**
     * Porter tries to collect a Bag.
     * @param pid Porter id.
     * @return 
     */
    public String tryToCollectABag(int pid);

}
