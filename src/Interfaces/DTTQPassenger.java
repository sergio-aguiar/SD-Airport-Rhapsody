package Interfaces;
/**
 * Passanger Departure Terminal Transfer Exit Interface.
 * 
 * @author sergioaguiar
 * @author marcomacedo
 */
public interface DTTQPassenger {
    /**
     * The Passenger leaves the Bus.
     * @param pid Passanger id.
     */
    public void leaveTheBus(int pid);
}
