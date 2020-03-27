package Extras;

import Entities.PassengerThread;

/**
 * Identifies the Passenger and his bags.
 * @author sergioaguiar
 * @author marcomacedo
 */
public class Bag {
    /**
     * Passenger id.
     */
    private final int passengerID;
    /**
     * boolean if Porter lost any bags.
     */
    private final boolean lostByPorter;
    /**
     * Passenger and bag situations enum.
     */
    private PassengerThread.PassengerAndBagSituations bagSituation;
    
    /**
     * Constructor Bag.
     * 
     * @param passengerID Passenger id.
     * @param lostByPorter true if any bag lost by Porter, false otherwise.
     * @param bagSituation Bag situation.
     */
    public Bag(int passengerID, boolean lostByPorter, PassengerThread.PassengerAndBagSituations bagSituation) {
        this.passengerID = passengerID;
        this.lostByPorter = lostByPorter;
        this.bagSituation = bagSituation;
    }
    
    /**
     * Get the Passenger ID.
     * @return Passenger Id.
     */
    public int getPassengerID() {
        return this.passengerID;
    }
    
    /**
     * Get the Passenger and Bag Situation.
     * @return bag situation.
     */
    public PassengerThread.PassengerAndBagSituations getBagSituation() {
        return this.bagSituation;
    }
}
