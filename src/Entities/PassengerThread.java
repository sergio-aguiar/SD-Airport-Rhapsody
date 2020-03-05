package Entities;

import Interfaces.*;
import SharedRegions.*;

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

    private enum PassengerSituations {
        TRT("TRT"),
        FDT("FDT");

        String description;

        PassengerSituations(String description) {
            this.description = description;
        }
    }

    private PassengerStates state;
    private final int id;
    private final int luggageAtStart;
    private int currentLuggage;

    private final ALPassenger alPassenger;
    private final ATEPassenger atePassenger;
    private final ATTQPassenger attqPassenger;
    private final BCPPassenger bcpPassenger;
    private final DTEPassenger dtePassenger;
    private final DTTQPassenger dttqPassenger;

    public PassengerThread(int id, int luggageAtStart, ALPassenger al, ATEPassenger ate, ATTQPassenger attq,
                           BCPPassenger bcp, DTEPassenger dte, DTTQPassenger dttq) {
        this.state = PassengerStates.AT_THE_DISEMBARKING_ZONE;

        this.id = id;
        this.luggageAtStart = luggageAtStart;
        this.currentLuggage = 0;

        this.alPassenger = al;
        this.atePassenger = ate;
        this.attqPassenger = attq;
        this.bcpPassenger = bcp;
        this.dtePassenger = dte;
        this.dttqPassenger = dttq;
    }

    @Override
    public void run() {
        super.run();
    }
}
