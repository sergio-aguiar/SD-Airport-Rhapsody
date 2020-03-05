package Entities;

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

    private final ArrivalLounge arrivalLounge;
    private final ArrivalTerminalExit arrivalTerminalExit;
    private final ArrivalTerminalTransferQuay arrivalTerminalTransferQuay;
    private final BaggageCollectionPoint baggageCollectionPoint;
    private final DepartureTerminalEntrance departureTerminalEntrance;
    private final DepartureTerminalTransferQuay departureTerminalTransferQuay;

    public PassengerThread(int id, int luggageAtStart, ArrivalLounge al, ArrivalTerminalExit ate,
                           ArrivalTerminalTransferQuay attq, BaggageCollectionPoint bcp, DepartureTerminalEntrance dte,
                           DepartureTerminalTransferQuay dttq) {
        this.state = PassengerStates.AT_THE_DISEMBARKING_ZONE;

        this.id = id;
        this.luggageAtStart = luggageAtStart;
        this.currentLuggage = 0;

        this.arrivalLounge = al;
        this.arrivalTerminalExit = ate;
        this.arrivalTerminalTransferQuay = attq;
        this.baggageCollectionPoint = bcp;
        this.departureTerminalEntrance = dte;
        this.departureTerminalTransferQuay = dttq;
    }

}
