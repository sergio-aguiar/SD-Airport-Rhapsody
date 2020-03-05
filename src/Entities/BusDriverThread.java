package Entities;

import SharedRegions.ArrivalTerminalTransferQuay;
import SharedRegions.DepartureTerminalTransferQuay;

public class BusDriverThread extends Thread {

    private enum BusDriverStates {
        PARKING_AT_THE_ARRIVAL_TERMINAL("paat"),
        DRIVING_FORWARD("dfwd"),
        PARKING_AT_THE_DEPARTURE_TERMINAL("padt"),
        DRIVING_BACKWARD("dbwd");

        String description;

        BusDriverStates(String description) {
            this.description = description;
        }
    }

    private BusDriverStates state;
    private final char[] waitingQueue;
    private final char[] busSeats;

    private final ArrivalTerminalTransferQuay arrivalTerminalTransferQuay;
    private final DepartureTerminalTransferQuay departureTerminalTransferQuay;

    public BusDriverThread(ArrivalTerminalTransferQuay attq, DepartureTerminalTransferQuay dttq) {
        this.state = BusDriverStates.PARKING_AT_THE_ARRIVAL_TERMINAL;

        this.waitingQueue = new char[6];
        this.busSeats = new char[6];

        this.arrivalTerminalTransferQuay = attq;
        this.departureTerminalTransferQuay = dttq;
    }
}
