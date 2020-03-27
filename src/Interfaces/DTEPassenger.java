package Interfaces;
/**
 * Passenger Departure Terminal Exit Interface.
 * 
 * @author sergioaguiar
 * @author marcomacedo
 */
public interface DTEPassenger {
    /**
     * Passanger prepares Next Leg.
     * @param pid Passanger id.
     */
    public void prepareNextLeg(int pid);
}
