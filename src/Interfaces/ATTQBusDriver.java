package Interfaces;
/**
 * Bus Driver Arrival Terminal Transfer Quay Interface.
 * 
 * @author sergioaguiar
 * @author marcomacedo
 */
public interface ATTQBusDriver {
	/**
     * Has the day work ended for the Bus Driver.
     * @return true if the days work ended or false otherwise. 
     */
    public boolean hasDaysWorkEnded();
	/**
     * Bus Driver announcing the bs boarding.
     */
    public boolean announcingBusBoarding();
	/**
     * The bus driver goes to the Departure Terminal.
     * @param bid Bus Driver id.
     * @return number of passenger being taken.
     */
    public int goToDepartureTerminal(int bid);
	/**
     * The Bus driver parks the Bus.
     * @param bid Bus Driver id.
     */
    public void parkTheBus(int bid);
}
