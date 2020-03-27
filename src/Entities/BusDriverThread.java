package Entities;

import Interfaces.ATTQBusDriver;
import Interfaces.DTTQBusDriver;

public class BusDriverThread extends Thread {

    public enum BusDriverStates {
        PARKING_AT_THE_ARRIVAL_TERMINAL("paat"),
        DRIVING_FORWARD("dfwd"),
        PARKING_AT_THE_DEPARTURE_TERMINAL("padt"),
        DRIVING_BACKWARD("dbwd");

        String description;

        BusDriverStates(String description) {
            this.description = description;
        }
    }

    private final int bid;
    private final ATTQBusDriver attqBusDriver;
    private final DTTQBusDriver dttqBusDriver;
    private int passengersBeingTaken;

    public BusDriverThread(int bid, ATTQBusDriver attq, DTTQBusDriver dttq) {
        this.bid = bid;
        this.attqBusDriver = attq;
        this.dttqBusDriver = dttq;
        this.passengersBeingTaken = 0;
    }

    @Override
    public void run() {
        while(!this.attqBusDriver.hasDaysWorkEnded()) {
            this.attqBusDriver.announcingBusBoarding();
            this.passengersBeingTaken = this.attqBusDriver.goToDepartureTerminal(this.bid);
            this.dttqBusDriver.parkTheBusAndLetPassOff(this.bid, this.passengersBeingTaken);
            this.dttqBusDriver.goToArrivalTerminal(this.bid);
            this.attqBusDriver.parkTheBus(this.bid);
        }
    }
}
