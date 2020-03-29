package Interfaces;
/**
 * Bus Driver's Arrival Terminal Transfer Quay Interface.
 * 
 * @author sergioaguiar
 * @author marcomacedo
 */
public interface ATTQBusDriver {
	/**
     * The bus driver checks if his services are needed in the future.
     * @return true if the bus driver's services are not needed in the future and false otherwise.
     */
    public boolean hasDaysWorkEnded();
	/**
     * The bus driver announces that the bus is boarding after seeing that at least one passenger is in queue.
     */
    public boolean announcingBusBoarding();
	/**
     * The bus driver drives towards the Departure Terminal Transfer Quay.
     * @param bid The bus driver's ID.
     * @return the number of passenger being taken inside the bus.
     */
    public int goToDepartureTerminal(int bid);
	/**
     * The bus driver parks the bus and gets ready for possibly a new trip.
     * @param bid The bus driver's ID.
     */
    public void parkTheBus(int bid);
}
