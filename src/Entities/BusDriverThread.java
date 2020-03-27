package Entities;

import Interfaces.ATTQBusDriver;
import Interfaces.DTTQBusDriver;

/**
* BusDriver Thread: implements the life-cycle of the Bus Driver.
* 
* @author marcomacedo
* @author sergioaguiar
*/
public class BusDriverThread extends Thread {
	  /**
     * Enumerate with the Bus Driver states.
     */
    public enum BusDriverStates {
        PARKING_AT_THE_ARRIVAL_TERMINAL("paat"),
        DRIVING_FORWARD("dfwd"),
        PARKING_AT_THE_DEPARTURE_TERMINAL("padt"),
        DRIVING_BACKWARD("dbwd");

        String description;

        BusDriverStates(String description) {
            this.description = description;
        }
    }
	
    /**
     * Bus Driver id.
     */
    private final int bid;
    
    /**
     * Instance of the Bus Driver Arrival Terminal Transfer Quay interface.
     */
    private final ATTQBusDriver attqBusDriver;
    
     /**
     * Instance of the Bus Driver Departure Terminal Transfer Quay interface.
     */
    private final DTTQBusDriver dttqBusDriver;

	/**
     * Number of passengers being taken.
     */
    private int passengersBeingTaken;
	
	 
    /**
     * Constructor: Bus Driver
     * @param bid Busdriver id.
     * @param attq Bus Diver Arrival Terminal Tranfer Quay Interface.
     * @param dttq Bus Diver Departure Terminal Tranfer Quay Interface.
     */
    public BusDriverThread(int bid, ATTQBusDriver attq, DTTQBusDriver dttq) {
        this.bid = bid;
        this.attqBusDriver = attq;
        this.dttqBusDriver = dttq;
        this.passengersBeingTaken = 0;
    }
	/**
     * Implements the life cycle of the Bus Driver.
     */
    @Override
    public void run() {
        while(!this.attqBusDriver.hasDaysWorkEnded()) {
            this.attqBusDriver.announcingBusBoarding();
            this.passengersBeingTaken = this.attqBusDriver.goToDepartureTerminal(this.bid);
            this.dttqBusDriver.parkTheBusAndLetPassOff(this.bid, this.passengersBeingTaken);
            this.dttqBusDriver.goToArrivalTerminal(this.bid);
            this.attqBusDriver.parkTheBus(this.bid);
        }
    }
}
