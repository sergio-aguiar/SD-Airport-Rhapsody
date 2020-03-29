package Interfaces;
/**
 * Passenger's Departure Terminal Exit Interface.
 * 
 * @author sergioaguiar
 * @author marcomacedo
 */
public interface DTEPassenger {
    /**
     * The passenger checks if they is the last to make it to their destination inside the airport. If so, they signal all others to leave together. Otherwise, they wait for the last one to signal them.
     * @param pid The passenger's ID.
     */
    public void prepareNextLeg(int pid);
}
