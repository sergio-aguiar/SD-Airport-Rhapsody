package Extras;

import Entities.PassengerThread;

public class Bag {

    private final int passengerID;
    private PassengerThread.PassengerAndBagSituations bagSituation;

    public Bag(int passengerID, PassengerThread.PassengerAndBagSituations bagSituation) {
        this.passengerID = passengerID;
        this.bagSituation = bagSituation;
    }
}
