package Entities;

import Interfaces.ATTQBusDriver;
import Interfaces.DTTQBusDriver;
import SharedRegions.ArrivalTerminalTransferQuay;
import SharedRegions.DepartureTerminalTransferQuay;

import java.lang.reflect.Array;
import java.util.Arrays;

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
    private String[] busSeats;

    private final ATTQBusDriver attqBusDriver;
    private final DTTQBusDriver dttqBusDriver;

    public BusDriverThread(ATTQBusDriver attq, DTTQBusDriver dttq, int nPassengers, int tBusSeats) {
        this.state = BusDriverStates.PARKING_AT_THE_ARRIVAL_TERMINAL;

        this.busSeats = new String[tBusSeats];

        Arrays.fill(busSeats, "-");

        this.attqBusDriver = attq;
        this.dttqBusDriver = dttq;
    }

    private boolean isBusFull() {
        for(int i = this.busSeats.length - 1; i >= 0; i--)
            if(this.busSeats[i].equals("-")) return false;
        return true;
    }

    @Override
    public void run() {
        while(!this.attqBusDriver.hasDaysWorkEnded()) {
            this.attqBusDriver.announcingBusBoarding();
            this.busSeats = Arrays.copyOf(this.attqBusDriver.goToDepartureTerminal(), this.busSeats.length);
            this.state = BusDriverStates.DRIVING_FORWARD;
        }
    }
}
