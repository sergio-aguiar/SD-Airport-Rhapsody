package Extras;

import Entities.PassengerThread;

public class Bag {

    private final int passengerID;
    private final boolean lostByPorter;
    private PassengerThread.PassengerAndBagSituations bagSituation;

    public Bag(int passengerID, boolean lostByPorter, PassengerThread.PassengerAndBagSituations bagSituation) {
        this.passengerID = passengerID;
        this.lostByPorter = lostByPorter;
        this.bagSituation = bagSituation;
    }

    public int getPassengerID() {
        return this.passengerID;
    }

    public PassengerThread.PassengerAndBagSituations getBagSituation() {
        return this.bagSituation;
    }
}
