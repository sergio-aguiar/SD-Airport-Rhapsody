package Entities;

import Interfaces.*;


/**
 * Passenger Thread: executes the Passenger's life-cycle.
 * @author sergioaguiar
 * @author marcomacedo
 */
public class PassengerThread extends Thread {
	/**
     * Enumerate with the Passenger states.
     */
    public enum PassengerStates {
        AT_THE_DISEMBARKING_ZONE("adz"),
        AT_THE_LUGGAGE_COLLECTION_POINT("alc"),
        AT_THE_LUGGAGE_RECLAIM_OFFICE("alr"),
        EXITING_THE_ARRIVAL_TERMINAL("eat"),
        AT_THE_ARRIVAL_TRANSFER_TERMINAL("aat"),
        TERMINAL_TRANSFER("ttf"),
        AT_THE_DEPARTURE_TRANSFER_TERMINAL("adt"),
        ENTERING_THE_DEPARTURE_TERMINAL("edt");

        String description;

        PassengerStates(String description) {
            this.description = description;
        }
        
        @Override
        public String toString(){
            return this.description;
        }
    }
	/**
     * Enumerate with the Passenger and Bag Situations.
	 * TRT: if the Passenger is in Transit (has additional flight legs).
	 * FDT: if the Passenger has reached their final destination.
     */
    public enum PassengerAndBagSituations {
        TRT("TRT"),
        FDT("FDT");

        String description;

        PassengerAndBagSituations(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return this.description;
        }
    }

    /**
     * Passenger's ID.
     */
    private final int pid;
    
    /**
     * Passenger's number of luggage at the start.
     */
    private final int luggageAtStart;
    
    /**
     * Passenger's currently collected luggage.
     */
    private int currentLuggage;
	
	/**
     * Passenger's flight situation.
     */
    private final PassengerAndBagSituations passengerSituation;

    /**
     * Passenger's current seat on the bus.
     */
    private int busSeat;

    /**
     * Instance of the Passenger's Arrival Lounge interface.
     */
    private final ALPassenger alPassenger;
    /**
     * Instance of the Passenger's Arrival Terminal Exit interface.
     */
    private final ATEPassenger atePassenger;
    /**
     * Instance of the Passenger's Arrival Terminal Transfer Quay interface.
     */
    private final ATTQPassenger attqPassenger;
    /**
     * Instance of the Passenger's Baggage Collection Point interface.
     */
    private final BCPPassenger bcpPassenger;
    /**
     * Instance of the Passenger's Departure Terminal Entrance interface.
     */
    private final DTEPassenger dtePassenger;
    /**
     * Instance of the Passenger's Departure Terminal Transfer Quay interface.
     */
    private final DTTQPassenger dttqPassenger;
    /**
     * Instance of the Passenger's Baggage Reclaim Office interface.
     */
    private final BROPassenger broPassenger;
	
	 /**
      * Constructor: Passenger
      * @param id Passenger's ID.
      * @param luggageAtStart Passenger's number of luggage at the start.
      * @param situation Passenger's flight situation.
      * @param al Passenger's Arrival Lounge interface.
      * @param ate Passenger's Arrival Terminal Exit interface.
      * @param attq Passenger's Arrival Terminal Transfer Quay interface.
      * @param bcp Passenger's Baggage Collection Point interface
      * @param dte Passenger's Departure Terminal Entrance interface.
      * @param dttq Passenger's Departure Terminal Transfer Quay interface.
      * @param bro Passenger's Baggage Reclaim Office interface.
     */
    public PassengerThread(int id, int luggageAtStart, PassengerAndBagSituations situation, ALPassenger al,
                           ATEPassenger ate, ATTQPassenger attq, BCPPassenger bcp, DTEPassenger dte, DTTQPassenger dttq,
                           BROPassenger bro) {

        this.pid = id;
        this.luggageAtStart = luggageAtStart;
        this.currentLuggage = 0;
        this.passengerSituation = situation;
        this.busSeat = -1;
        this.alPassenger = al;
        this.atePassenger = ate;
        this.attqPassenger = attq;
        this.bcpPassenger = bcp;
        this.dtePassenger = dte;
        this.dttqPassenger = dttq;
        this.broPassenger = bro;
    }
	
	/**
     * Execute's the passenger's life-cycle.
     */
    @Override
    public void run() {
        System.out.println("PASSENGER " + this.pid + " STARTED!");
        this.alPassenger.whatShouldIDo(this.pid, this.passengerSituation.toString());
        System.out.println("PASSENGER " + this.pid + " DECIDED WHAT TO DO!");
        if(this.passengerSituation.toString().equals(PassengerAndBagSituations.FDT.toString())) {
            System.out.println("PASSENGER " + this.pid + " CHECKING IF THEY HAVE LUGGAGE TO GET1");
            if(this.luggageAtStart != 0) {
                System.out.println("PASSENGER " + this.pid + " GOING TO COLLECT A BAG!");
                this.alPassenger.goCollectABag(this.pid);
                System.out.println("PASSENGER " + this.pid + " TRYING TO COLLECT A BAG!");
                while(this.bcpPassenger.goCollectABag(this.pid)) {
                    this.currentLuggage++;
                    if(this.currentLuggage == this.luggageAtStart) break;
                }
                System.out.println("PASSENGER " + this.pid + " SEEING IF THEY HAVE ANY LUGGAGE MISSING!");
                if(this.currentLuggage != this.luggageAtStart)
                    System.out.println("PASSENGER " + this.pid + " REPORTING MISSING BAGS!");
                    this.broPassenger.reportMissingBags(this.pid, this.luggageAtStart - this.currentLuggage);
            }
            System.out.println("PASSENGER " + this.pid + " GOING HOME!");
            this.atePassenger.goHome(this.pid);
            System.out.println("PASSENGER " + this.pid + " WENT HOME!");
        } else {
            System.out.println("PASSENGER " + this.pid + " DECIDED TO TAKE A BUS!");
            this.attqPassenger.takeABus(this.pid);
            System.out.println("PASSENGER " + this.pid + " IS ABOUT TO ENTER THE BUS!");
            this.busSeat = this.attqPassenger.enterTheBus(this.pid);
            System.out.println("PASSENGER " + this.pid + " IS ABOUT TO LEAVE THE BUS!");
            this.dttqPassenger.leaveTheBus(this.pid, this.busSeat);
            System.out.println("PASSENGER " + this.pid + " LEFT THE BUS AND IS PREPARING FOR THE NEXT LEG!");
            this.dtePassenger.prepareNextLeg(this.pid);
            System.out.println("PASSENGER " + this.pid + " WENT INTO THE NEXT LEG!");
        }
    }
}
