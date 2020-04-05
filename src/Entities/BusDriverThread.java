package Entities;

import Interfaces.ATTQBusDriver;
import Interfaces.DTTQBusDriver;

/**
* BusDriver Thread: executes the Bus Driver's life-cycle.
* 
* @author marcomacedo
* @author sergioaguiar
*/
public class BusDriverThread extends Thread {
    /**
     * Enumerate with all possible Bus Driver states.
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
        
        @Override
        public String toString(){
            return this.description;
        }
    }
	
    /**
     * Bus Driver's ID.
     */
    private final int bid;
    
    /**
     * Instance of the Bus Driver's Arrival Terminal Transfer Quay interface.
     */
    private final ATTQBusDriver attqBusDriver;
    
     /**
     * Instance of the Bus Driver's Departure Terminal Transfer Quay interface.
     */
    private final DTTQBusDriver dttqBusDriver;

	/**
     * Number of passengers being taken in the bus.
     */
    private int passengersBeingTaken;

    private int flightNumber;

    /**
     * Constructor: Bus Driver
     * @param bid Bus Driver's ID.
     * @param attq Bus Driver's Arrival Terminal Transfer Quay Interface.
     * @param dttq Bus Driver's Departure Terminal Transfer Quay Interface.
     */
    public BusDriverThread(int bid, ATTQBusDriver attq, DTTQBusDriver dttq) {
        this.bid = bid;
        this.attqBusDriver = attq;
        this.dttqBusDriver = dttq;
        this.passengersBeingTaken = 0;
        this.flightNumber = 0;
    }
	/**
     * Executes the Bus Driver's life-cycle.
     */
    @Override
    public void run() {
        while(!this.attqBusDriver.hasDaysWorkEnded()) {
            if(!this.attqBusDriver.announcingBusBoarding()) break;
            this.passengersBeingTaken = this.attqBusDriver.goToDepartureTerminal(this.bid);
            this.flightNumber = this.dttqBusDriver.parkTheBusAndLetPassOff(this.bid, this.passengersBeingTaken, this.flightNumber);
            this.dttqBusDriver.goToArrivalTerminal(this.bid);
            this.attqBusDriver.parkTheBus(this.bid);
        }
    }
}
