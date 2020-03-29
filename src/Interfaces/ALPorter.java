package Interfaces;
/**
 * Porter's Arrival Lounge Interface.
 * 
 * @author sergioaguiar
 * @author marcomacedo
 */
public interface ALPorter {
    /**
     * The porter checks whether he is still needed in the future. If so, he awaits the next flight. He stops otherwise.
     * @param pid The porter's ID.
     * @return true if the porter isn't needed anymore and false otherwise.
     */
    public boolean takeARest(int pid);
    /**
     * The porter tries to collect a bag from the plane's hold.
     * @param pid The Porter's ID.
     * @return the bag's owner ID as a String, or an empty String if the plane is empty of bags.
     */
    public String tryToCollectABag(int pid);

}
