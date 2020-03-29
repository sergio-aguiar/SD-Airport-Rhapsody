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
    }
	/**
     * Executes the Bus Driver's life-cycle.
     */
    @Override
    public void run() {
        System.out.println("BUS DRIVER STARTING!");
        while(!this.attqBusDriver.hasDaysWorkEnded()) {
            System.out.println("BUS DRIVER BEFORE ANNOUNCING BUS BOARDING!");
            if(!this.attqBusDriver.announcingBusBoarding()) break;
            System.out.println("BUS DRIVER ANNOUNCED BUS BOARDING!");
            this.passengersBeingTaken = this.attqBusDriver.goToDepartureTerminal(this.bid);
            System.out.println("BUS DRIVER GOING TO THE DEPARTURE TERMINAL!");
            this.dttqBusDriver.parkTheBusAndLetPassOff(this.bid, this.passengersBeingTaken);
            System.out.println("BUS DRIVER PARKED AND LET THE PASSENGERS OFF!");
            this.dttqBusDriver.goToArrivalTerminal(this.bid);
            System.out.println("BUS DRIVER GOING TO THE ARRIVAL TERMINAL!");
            this.attqBusDriver.parkTheBus(this.bid);
            System.out.println("BUS DRIVER FINISHED A CYCLE!");
        }
        System.out.println("BUS DRIVER DIED!");
    }
}
