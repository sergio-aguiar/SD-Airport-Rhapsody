package Interfaces;
/**
 * Passenger Arrival Terminal Transfer Quay Interface.
 * 
 * @author sergioaguiar
 * @author marcomacedo
 */
public interface ATTQPassenger {
    /**
     * The Passenger takes the Bus.
     * @param pid Passenger id.
     */
    public void takeABus(int pid);
    /**
     * The Passenger enters the Bus.
     * @param pid Passenger id.
     */
    public int enterTheBus(int pid);
}
