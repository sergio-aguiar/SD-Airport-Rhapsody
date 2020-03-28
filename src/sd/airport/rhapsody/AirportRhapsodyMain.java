package sd.airport.rhapsody;

import Entities.BusDriverThread;
import Entities.PassengerThread;
import Entities.PorterThread;
import Extras.Bag;
import Interfaces.*;
import SharedRegions.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;

public class AirportRhapsodyMain {

    private static final int k = 2; // K plane landings
    private static final int n = 6; // N passengers
    private static final int m = 2; // 0 to M luggage per passenger
    private static final int t = 3; // T bus seats

    private static final double FDTProb = 0.5;

    private static int flightNumber = 0;

    private static PassengerThread[] passengers = new PassengerThread[n];
    private static BusDriverThread busDriver;
    private static PorterThread porter;

    private static final PassengerThread.PassengerAndBagSituations[][] passengerSituations
            = new PassengerThread.PassengerAndBagSituations[k][n];
    private static final int[][] passengerLuggage = new int[k][n];
    private static final int[][] passengerLuggageAfterMissing = new int[k][n];
    private static Bag[][][] passengerBags = new Bag[k][n][m];
    private static final int[] totalLuggagePerFlight = new int[k];

    private static ArrivalLounge arrivalLoungeMonitor;
    private static ArrivalTerminalExit arrivalTerminalExitMonitor;
    private static ArrivalTerminalTransferQuay arrivalTerminalTransferQuayMonitor;
    private static BaggageCollectionPoint baggageCollectionPointMonitor;
    private static BaggageReclaimOffice baggageReclaimOfficeMonitor;
    private static DepartureTerminalEntrance departureTerminalEntranceMonitor;
    private static DepartureTerminalTransferQuay departureTerminalTransferQuayMonitor;
    private static Repository repositoryMonitor;
    private static TemporaryStorageArea temporaryStorageAreaMonitor;

    public static void main(String[] args) {
        Arrays.fill(totalLuggagePerFlight, 0);
        generateStartingData();

        try {
            repositoryMonitor = new Repository(0, totalLuggagePerFlight[0], t, n, passengerSituations[0],
                    passengerLuggage[0]);
        } catch (IOException e) {
            System.err.println(e.toString());
            System.out.println("Repository Error!");
        }

        arrivalLoungeMonitor = new ArrivalLounge(repositoryMonitor, n, totalLuggagePerFlight, passengerBags);
        arrivalTerminalExitMonitor = new ArrivalTerminalExit(repositoryMonitor, n);
        arrivalTerminalTransferQuayMonitor = new ArrivalTerminalTransferQuay(repositoryMonitor, n, t);
        baggageCollectionPointMonitor = new BaggageCollectionPoint(repositoryMonitor, n);
        baggageReclaimOfficeMonitor = new BaggageReclaimOffice(repositoryMonitor);
        departureTerminalEntranceMonitor = new DepartureTerminalEntrance(repositoryMonitor, n);
        departureTerminalTransferQuayMonitor = new DepartureTerminalTransferQuay(repositoryMonitor);
        temporaryStorageAreaMonitor = new TemporaryStorageArea(repositoryMonitor);

        arrivalTerminalExitMonitor.setDte(departureTerminalEntranceMonitor);
        departureTerminalEntranceMonitor.setAte(arrivalTerminalExitMonitor);


        busDriver = new BusDriverThread(0, arrivalTerminalTransferQuayMonitor, departureTerminalTransferQuayMonitor);
        porter = new PorterThread(0, arrivalLoungeMonitor, baggageCollectionPointMonitor, temporaryStorageAreaMonitor);

        for(int passenger = 0; passenger < n; passenger++) {
            passengers[passenger] = new PassengerThread(passenger, passengerLuggage[0][passenger],
                    passengerSituations[0][passenger], arrivalLoungeMonitor, arrivalTerminalExitMonitor,
                    arrivalTerminalTransferQuayMonitor, baggageCollectionPointMonitor, departureTerminalEntranceMonitor,
                    departureTerminalTransferQuayMonitor,  baggageReclaimOfficeMonitor);
        }

        busDriver.start();
        porter.start();
        for(PassengerThread passengerThread : passengers) passengerThread.start();

        try {
            for (PassengerThread passengerThread : passengers) passengerThread.join();
            busDriver.join();
            porter.join();
        } catch(InterruptedException e) {
            System.err.println(e.toString());
            System.out.println("ERROR ON THREADS");
        }
    }

    private static void generateStartingData() {
        for(int flight = 0; flight < k; flight++) {
            for(int passenger = 0; passenger < n; passenger++) {
                passengerSituations[flight][passenger] = (Math.random() < FDTProb)
                        ? PassengerThread.PassengerAndBagSituations.FDT : PassengerThread.PassengerAndBagSituations.TRT;
                passengerLuggage[flight][passenger] = (int) (Math.random() * m);
                passengerLuggageAfterMissing[flight][passenger] = passengerLuggage[flight][passenger]
                        - (int) (Math.random() * passengerLuggage[flight][passenger]);
                totalLuggagePerFlight[flight] += passengerLuggageAfterMissing[flight][passenger];

                for(int bag = 0; bag < passengerLuggageAfterMissing[flight][passenger]; bag++) {
                    passengerBags[flight][passenger][bag] = new Bag(passenger, passengerSituations[flight][passenger]);
                }
            }
        }
    }
}
