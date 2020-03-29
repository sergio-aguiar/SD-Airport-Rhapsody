package Interfaces;

/**
 * Passenger's Arrival Lounge Interface.
 * 
 * @author sergioaguiar
 * @author marcomacedo
 */
public interface ALPassenger {
	 /**
     * Passenger's first action when arriving from the plane. Signal's the porter if the final passenger to arrive.
     * @param pid The passenger's ID.
     * @param situation A string representation of the passenger's travel situation.
     */
    public void whatShouldIDo(int pid, String situation);
	 /**
     * The Passenger decides to move towards the Baggage Collection Point to collect a bag.
     * @param pid The passenger's ID.
     */
    public void goCollectABag(int pid);
}
