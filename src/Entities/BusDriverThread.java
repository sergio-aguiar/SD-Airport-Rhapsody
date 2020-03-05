package Entities;

import Interfaces.ATTQBusDriver;
import Interfaces.DTTQBusDriver;
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

    private enum QueueAndSeatStates {
        PASSENGER_0('0'),
        PASSENGER_1('1'),
        PASSENGER_2('2'),
        PASSENGER_3('3'),
        PASSENGER_4('4'),
        PASSENGER_5('5'),
        NO_PASSENGER('-');

        char description;

        QueueAndSeatStates(char description) { this.description = description; }
    }

    private BusDriverStates state;
    private final QueueAndSeatStates[] waitingQueue;
    private final QueueAndSeatStates[] busSeats;

    private final ATTQBusDriver attqBusDriver;
    private final DTTQBusDriver dttqBusDriver;

    public BusDriverThread(ATTQBusDriver attq, DTTQBusDriver dttq) {
        this.state = BusDriverStates.PARKING_AT_THE_ARRIVAL_TERMINAL;

        this.waitingQueue = new QueueAndSeatStates[] {
                QueueAndSeatStates.NO_PASSENGER,
                QueueAndSeatStates.NO_PASSENGER,
                QueueAndSeatStates.NO_PASSENGER,
                QueueAndSeatStates.NO_PASSENGER,
                QueueAndSeatStates.NO_PASSENGER,
                QueueAndSeatStates.NO_PASSENGER
        };
        this.busSeats = new QueueAndSeatStates[] {
                QueueAndSeatStates.NO_PASSENGER,
                QueueAndSeatStates.NO_PASSENGER,
                QueueAndSeatStates.NO_PASSENGER
        };

        this.attqBusDriver = attq;
        this.dttqBusDriver = dttq;
    }

    @Override
    public void run() {
        super.run();
    }
}
