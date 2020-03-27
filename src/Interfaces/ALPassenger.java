package Interfaces;

import Entities.PassengerThread;
/**
 * Passenger Arrival Lounge Interface.
 * 
 * @author sergioaguiar
 * @author marcomacedo
 */
public interface ALPassenger {
    /**
     * Passenger is in transit or final destination.
     * @param pid Passanger id.
     * 
     */
    public void whatShouldIDo(int pid);
    /**
     * The Passenger goes collect a bag.
     * @param pid Passanger id.
     */
    public void goCollectABag(int pid);
}
