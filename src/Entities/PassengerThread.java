package Entities;

import Extras.Bag;
import Interfaces.*;
import SharedRegions.*;

import java.util.Arrays;

public class PassengerThread extends Thread {

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

    private final int pid;
    private final int luggageAtStart;
    private int currentLuggage;
    private Bag[] bags;

    private final ALPassenger alPassenger;
    private final ATEPassenger atePassenger;
    private final ATTQPassenger attqPassenger;
    private final BCPPassenger bcpPassenger;
    private final DTEPassenger dtePassenger;
    private final DTTQPassenger dttqPassenger;
    private final BROPassenger broPassenger;

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

    public int getPassengerID() {
        return this.pid;
    }

    public void collectBag(Bag bag) {
        this.bags[this.currentLuggage] = bag;
        this.currentLuggage++;
    }

    public Bag[] forfeitBags() {
        this.currentLuggage = 0;
        Bag[] bags = Arrays.copyOf(this.bags, this.bags.length);
        this.bags = new Bag[this.luggageAtStart];
        return bags;
    }

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
