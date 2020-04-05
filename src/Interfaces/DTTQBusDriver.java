package Interfaces;
/**
 * Bus Driver's Departure Terminal Transfer Exit Interface.
 * 
 * @author sergioaguiar
 * @author marcomacedo
 */
public interface DTTQBusDriver {
	/**
     * The bus driver parks the Bus and let's teh passengers off.
     * @param bid The bus driver's ID.
     * @param passengersThatArrived The number of passengers that arrived aboard the bus.
     * @param flightNumber The bus driver's estimated flight number.
     * @return The current flight number.
     */
    public int parkTheBusAndLetPassOff(int bid, int passengersThatArrived, int flightNumber);
	 /**
     * The bus driver drives towards the Arrival Terminal Transfer Quay.
     * @param bid The bus driver's ID.
     */
    public void goToArrivalTerminal(int bid);
}
