package Interfaces;
/**
 * Passenger's Arrival Terminal Transfer Quay Interface.
 * 
 * @author sergioaguiar
 * @author marcomacedo
 */
public interface ATTQPassenger {
    /**
     * The passenger decides to take the bus and enters the waiting queue.
     * @param pid the passenger's ID.
     */
    public void takeABus(int pid);
    /**
     * The Passenger leaves the waiting queue and enters the bus.
     * @param pid The passenger's ID.
     */
    public int enterTheBus(int pid);
}
