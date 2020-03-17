package Entities;

import Extras.Bag;
import Interfaces.*;
import SharedRegions.*;

import java.util.Arrays;

public class PassengerThread extends Thread {

    private enum PassengerStates {
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
    }

    private PassengerStates state;
    private PassengerAndBagSituations situation;
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

    public PassengerThread(int id,PassengerAndBagSituations ps, int luggageAtStart, ALPassenger al, ATEPassenger ate,
                           ATTQPassenger attq, BCPPassenger bcp, DTEPassenger dte, DTTQPassenger dttq) {
        this.state = PassengerStates.AT_THE_DISEMBARKING_ZONE;
        this.situation = ps;

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
        super.run();
    }
}
