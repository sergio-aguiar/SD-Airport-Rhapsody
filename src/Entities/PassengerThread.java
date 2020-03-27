package Entities;

import Interfaces.*;


/**
 * Passenger Thread: implements the life-cycle of the Passenger.
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
    }
	/**
     * Enumerate with the Passenger and Bag Situations states.
	 * TRT: if passenger is in Transit.
	 * FDT: if final destination.
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
     * Passanger id.
     */
    private final int pid;
    
    /**
     * Passenger luggage at start.
     */
    private final int luggageAtStart;
    
    /**
     * Passanger current luggage.
     */
    private int currentLuggage;
	
	/**
     * Passenger bag and situation state.
     */
    private final PassengerAndBagSituations passengerSituation;

    /**
     * Passenger's current seat on the bus.
     */
    private int busSeat;

    /**
     * Instance of the Passenger Arrival Lounge interface.
     */
    private final ALPassenger alPassenger;
    /**
     * Instance of the Passenger Arrival Terminal Exit interface.
     */
    private final ATEPassenger atePassenger;
    /**
     * Instance of the Passenger Arrival Terminal Transfer Quay interface.
     */
    private final ATTQPassenger attqPassenger;
    /**
     * Instance of the Passenger Baggage Collection Point interface.
     */
    private final BCPPassenger bcpPassenger;
    /**
     * Instance of the Passenger Departure Terminal Entrance interface.
     */
    private final DTEPassenger dtePassenger;
    /**
     * Instance of the Passenger Departure Terminal Transfer Quay interface.
     */
    private final DTTQPassenger dttqPassenger;
    /**
     * Instance of the Passenger Baggage Reclaim Office interface.
     */
    private final BROPassenger broPassenger;
	
	 /**
     * Constructor: Passanger
     * @param id Passenger id.
     * @param luggageAtStart Passanger luggage at start
     * @param al Passenger Arrival Lounge interface.
     * @param ate Passenger Arrival Terminal Exit interface.
     * @param attq Passenger Arrival Terminal Transfer Quay interface.
     * @param bcp Passenger Baggage Collection Point interface
     * @param dte Passenger Departure Terminal Entrance interface.
     * @param dttq Passenger Departure Terminal Transfer Quay interface.
     * @param bro Passenger Baggage Reclaim Office interface.
     * @param situation Passenger situation.
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
     * Implements the life cycle of the Passanger.
     */
    @Override
    public void run() {
        this.alPassenger.whatShouldIDo(this.pid);
        if(this.passengerSituation.toString().equals(PassengerAndBagSituations.FDT.toString())) {
            if(this.luggageAtStart != 0) {
                this.alPassenger.goCollectABag(this.pid);
                while(this.bcpPassenger.goCollectABag(this.pid)) {
                    this.currentLuggage++;
                }
                if(this.currentLuggage != this.luggageAtStart)
                    this.broPassenger.reportMissingBags(this.pid, this.luggageAtStart - this.currentLuggage);
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
