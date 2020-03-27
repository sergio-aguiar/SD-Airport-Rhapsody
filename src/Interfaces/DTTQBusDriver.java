package Interfaces;
/**
 * Bus Driver Departure Terminal Transfer Exit Interface.
 * 
 * @author sergioaguiar
 * @author marcomacedo
 */
public interface DTTQBusDriver {
	/**
     * The Bus Driver parks the Bus and let pass off.
     * @param bid Bus driver id.
     * @param PassengersThatArrived number of passengers that arrived.
     */
    public void parkTheBusAndLetPassOff(int bid, int PassengersThatArrived);
	 /**
     * The Bus Diver goes to the Arrival Terminal.
     * @param bid Bus driver id.
     */
    public void goToArrivalTerminal(int bid);
}
