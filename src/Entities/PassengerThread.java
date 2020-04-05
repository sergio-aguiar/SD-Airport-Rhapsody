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
        AT_THE_DISEMBARKING_ZONE("WSD"),
        AT_THE_LUGGAGE_COLLECTION_POINT("LCP"),
        AT_THE_LUGGAGE_RECLAIM_OFFICE("BRO"),
        EXITING_THE_ARRIVAL_TERMINAL("EAT"),
        AT_THE_ARRIVAL_TRANSFER_TERMINAL("ATT"),
        TERMINAL_TRANSFER("TRT"),
        AT_THE_DEPARTURE_TRANSFER_TERMINAL("DTT"),
        ENTERING_THE_DEPARTURE_TERMINAL("EDT");
        /**
         * Enum's descriptive String.
         */
        String description;
        /**
         * PassengerStates constructor.
         * @param description Enum's descriptive String.
         */
        PassengerStates(String description) {
            this.description = description;
        }
        /**
         * Enum's toString override
         */
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
        /**
         * Enum's descriptive String.
         */
        String description;
        /**
         * PassengerAndBagSituations constructor.
         * @param description Enum's descriptive String.
         */
        PassengerAndBagSituations(String description) {
            this.description = description;
        }
        /**
         * Enum's toString override
         */
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
        this.alPassenger.whatShouldIDo(this.pid, this.passengerSituation.toString());
        if(this.passengerSituation.toString().equals(PassengerAndBagSituations.FDT.toString())) {
            if(this.luggageAtStart != 0) {
                this.alPassenger.goCollectABag(this.pid);
                while(this.bcpPassenger.goCollectABag(this.pid)) {
                    this.currentLuggage++;
                    if(this.currentLuggage == this.luggageAtStart) break;
                }
                if(this.currentLuggage != this.luggageAtStart) {
                    this.broPassenger.reportMissingBags(this.pid, this.luggageAtStart - this.currentLuggage);
                }
            }
            this.atePassenger.goHome(this.pid);
        } else {
            this.attqPassenger.takeABus(this.pid);
            this.busSeat = this.attqPassenger.enterTheBus(this.pid);
            this.dttqPassenger.leaveTheBus(this.pid, this.busSeat);
            this.dtePassenger.prepareNextLeg(this.pid);
        }
    }
}
