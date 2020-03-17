package Entities;

import Interfaces.ATTQBusDriver;
import Interfaces.DTTQBusDriver;
import SharedRegions.ArrivalTerminalTransferQuay;
import SharedRegions.DepartureTerminalTransferQuay;

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
    private final String[] waitingQueue;
    private final String[] busSeats;

    private final ATTQBusDriver attqBusDriver;
    private final DTTQBusDriver dttqBusDriver;

    public BusDriverThread(ATTQBusDriver attq, DTTQBusDriver dttq, int nPassengers, int tBusSeats) {
        this.state = BusDriverStates.PARKING_AT_THE_ARRIVAL_TERMINAL;

        this.waitingQueue = new String[nPassengers];
        this.busSeats = new String[tBusSeats];

        Arrays.fill(waitingQueue, "-");
        Arrays.fill(busSeats, "-");

        this.attqBusDriver = attq;
        this.dttqBusDriver = dttq;
    }

    public boolean isWaitingQueueFull() {
        for(int i = this.waitingQueue.length - 1; i >= 0; i--)
            if(this.waitingQueue[i].equals("-")) return false;
        return true;
    }

    public boolean isBusFull() {
        for(int i = this.busSeats.length - 1; i >= 0; i--)
            if(this.busSeats[i].equals("-")) return false;
        return true;
    }

    @Override
    public void run() {
        super.run();
    }
}
