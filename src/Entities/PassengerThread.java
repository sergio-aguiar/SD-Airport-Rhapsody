package Entities;

import Extras.Bag;
import Interfaces.*;
import SharedRegions.*;

import java.util.Arrays;
/**
 * Passanger Thread: implements the life-cycle of the Passanger.
 * @author sergioaguiar
 * @author marcomacedo
 */
public class PassengerThread extends Thread {
     /**
     * Enumerate with the Passanger states.
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
     * Enumerate with the Passanger and Bag Situations states.
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
     * Passanger luggage at start.
     */
    private final int luggageAtStart;
    
    /**
     * Passanger current luggage.
     */
    private int currentLuggage;
    
    /**
     * Array of Bags.
     */
    private Bag[] bags;
    
    /**
     * Instance of the Passanger Arrival Lounge interface.
     */
    private final ALPassenger alPassenger;
    /**
     * Instance of the Passanger Arrival Terminal Exit interface.
     */
    private final ATEPassenger atePassenger;
    /**
     * Instance of the Passanger Arrival Terminal Transfer Quay interface.
     */
    private final ATTQPassenger attqPassenger;
    /**
     * Instance of the Passanger Baggage Collection Point interface.
     */
    private final BCPPassenger bcpPassenger;
    /**
     * Instance of the Passanger Departure Terminal Entrance interface.
     */
    private final DTEPassenger dtePassenger;
    /**
     * Instance of the Passanger Departure Terminal Transfer Quay interface.
     */
    private final DTTQPassenger dttqPassenger;
    /**
     * Instance of the Passanger Baggage Reclaim Office interface.
     */
    private final BROPassenger broPassenger;
    
    /**
     * Constructor: Passanger
     * @param id Passenger id.
     * @param luggageAtStart Passanger luggage at start
     * @param al Passanger Arrival Lounge interface.
     * @param ate Passanger Arrival Terminal Exit interface.
     * @param attq Passanger Arrival Terminal Transfer Quay interface.
     * @param bcp Passanger Baggage Collection Point interface
     * @param dte Passanger Departure Terminal Entrance interface.
     * @param dttq Passanger Departure Terminal Transfer Quay interface.
     * @param bro Passanger Baggage Reclaim Office interface.
     */
    public PassengerThread(int id, int luggageAtStart, ALPassenger al, ATEPassenger ate, ATTQPassenger attq,
                           BCPPassenger bcp, DTEPassenger dte, DTTQPassenger dttq, BROPassenger bro) {

        this.pid = id;
        this.luggageAtStart = luggageAtStart;
        this.currentLuggage = 0;
        this.bags = new Bag[luggageAtStart];

        this.alPassenger = al;
        this.atePassenger = ate;
        this.attqPassenger = attq;
        this.bcpPassenger = bcp;
        this.dtePassenger = dte;
        this.dttqPassenger = dttq;
        this.broPassenger = bro;
    }
    
    /**
     * Get Passanger ID.
     * @return passanger id.
     */
    public int getPassengerID() {
        return this.pid;
    }
    
    /**
     * Collect Bag.
     * @param bag Bag.
     */
    public void collectBag(Bag bag) {
        this.bags[this.currentLuggage] = bag;
        this.currentLuggage++;
    }
    
    /**
     * Array of forfeits bags.
     * @return Bags.
     */
    public Bag[] forfeitBags() {
        this.currentLuggage = 0;
        Bag[] bags = Arrays.copyOf(this.bags, this.bags.length);
        this.bags = new Bag[this.luggageAtStart];
        return bags;
    }
    /**
     * Implements the life cycle of the Passanger.
     */
    @Override
    public void run() {
        if(this.alPassenger.whatShouldIDo(this.pid).equals(PassengerAndBagSituations.FDT.toString())) {
            if(this.luggageAtStart != 0) {
                this.alPassenger.goCollectABag(this.pid);
                while(this.bcpPassenger.goCollectABag(this.pid)) {
                    this.currentLuggage++;
                }
                if(this.currentLuggage != this.luggageAtStart) this.broPassenger.reportMissingBags(this.pid);
            }
            this.atePassenger.goHome(this.pid);
        } else {
            this.attqPassenger.takeABus(this.pid);
            this.attqPassenger.enterTheBus(this.pid);
            this.dttqPassenger.leaveTheBus(this.pid);
            this.dtePassenger.prepareNextLeg(this.pid);
        }
    }
}
