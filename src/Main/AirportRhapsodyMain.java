package Main;

import Entities.BusDriverThread;
import Entities.PassengerThread;
import Entities.PorterThread;
import Extras.Bag;
import SharedRegions.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
/**
 * AirportRhapsodyMain : The project's main file.
 *
 * @author sergiaguiar
 * @author marcomacedo
 */
public class AirportRhapsodyMain {
    /**
     * The number of plane landings.
     */
    private static final int k = 5;
    /**
     * The number of passengers.
     */
    private static final int n = 6;
    /**
     * The maximum number of luggage a passenger can carry.
     */
    private static final int m = 2;
    /**
     * The number of seats on the bus.
     */
    private static final int t = 3;
    /**
     * The probability of a passenger currently being at their final destination.
     */
    private static final double probFDT = 0.5;
    /**
     * The array that contains every flight's passenger threads.
     */
    private static PassengerThread[][] passengers = new PassengerThread[k][n];
    /**
     * The array that contains every flight's passenger's flight situations.
     */
    private static final PassengerThread.PassengerAndBagSituations[][] passengerSituations
            = new PassengerThread.PassengerAndBagSituations[k][n];
    /**
     * The array that contains every flight's passenger's total amount of luggage.
     */
    private static final int[][] passengerLuggage = new int[k][n];
    /**
     * The array that contains every flight's passenger's total amount of luggage after some went missing.
     */
    private static final int[][] passengerLuggageAfterMissing = new int[k][n];
    /**
     * The array that contains every flight's passenger's bags.
     */
    private static Bag[][][] passengerBags = new Bag[k][n][m];
    /**
     * The array that contains every flight's amount of bags.
     */
    private static final int[] totalLuggagePerFlight = new int[k];
    /**
     * The class's Repository instance.
     */
    private static Repository repositoryMonitor;
    /**
     * The class's main function.
     * @param args The function's command line arguments (not used).
     */
    public static void main(String[] args) {
        Arrays.fill(totalLuggagePerFlight, 0);
        generateStartingData();

        try {
            repositoryMonitor = new Repository(0, totalLuggagePerFlight[0], t, n, passengerSituations[0],
                    passengerLuggage[0], passengerBags);
        } catch (IOException e) {
            System.err.println("Main: Starting the repository: " + e.toString());
        }

        ArrivalLounge arrivalLoungeMonitor = new ArrivalLounge(repositoryMonitor, n, k, passengerBags);
        ArrivalTerminalExit arrivalTerminalExitMonitor = new ArrivalTerminalExit(repositoryMonitor, n);
        ArrivalTerminalTransferQuay arrivalTerminalTransferQuayMonitor = new ArrivalTerminalTransferQuay(repositoryMonitor, n, t, arrivalLoungeMonitor);
        BaggageCollectionPoint baggageCollectionPointMonitor = new BaggageCollectionPoint(repositoryMonitor, n);
        BaggageReclaimOffice baggageReclaimOfficeMonitor = new BaggageReclaimOffice(repositoryMonitor);
        DepartureTerminalEntrance departureTerminalEntranceMonitor = new DepartureTerminalEntrance(repositoryMonitor, n);
        DepartureTerminalTransferQuay departureTerminalTransferQuayMonitor = new DepartureTerminalTransferQuay(repositoryMonitor);
        TemporaryStorageArea temporaryStorageAreaMonitor = new TemporaryStorageArea(repositoryMonitor);

        arrivalTerminalExitMonitor.setDte(departureTerminalEntranceMonitor);
        departureTerminalEntranceMonitor.setAte(arrivalTerminalExitMonitor);

        BusDriverThread busDriver = new BusDriverThread(0, arrivalTerminalTransferQuayMonitor, departureTerminalTransferQuayMonitor);
        PorterThread porter = new PorterThread(0, arrivalLoungeMonitor, baggageCollectionPointMonitor, temporaryStorageAreaMonitor);

        for(int f = 0; f < k; f++) {
            for(int passenger = 0; passenger < n; passenger++) {
                passengers[f][passenger] = new PassengerThread(passenger, passengerLuggage[f][passenger],
                        passengerSituations[f][passenger], arrivalLoungeMonitor, arrivalTerminalExitMonitor,
                        arrivalTerminalTransferQuayMonitor, baggageCollectionPointMonitor, departureTerminalEntranceMonitor,
                        departureTerminalTransferQuayMonitor, baggageReclaimOfficeMonitor);
            }
        }

        busDriver.start();
        porter.start();

        for(int flight = 0; flight < k; flight++) {
            if(flight != 0) {
                repositoryMonitor.prepareForNextFlight(totalLuggagePerFlight[flight], passengerSituations[flight]);
                arrivalLoungeMonitor.prepareForNextFlight();
                arrivalTerminalExitMonitor.prepareForNextFlight();
                arrivalTerminalTransferQuayMonitor.prepareForNextFlight();
                baggageCollectionPointMonitor.prepareForNextFlight();
                departureTerminalEntranceMonitor.prepareForNextFlight();
                departureTerminalTransferQuayMonitor.prepareForNextFlight();
                temporaryStorageAreaMonitor.prepareForNextFlight();
            }

            for(PassengerThread passengerThread : passengers[flight]) passengerThread.start();

            try {
                for (PassengerThread passengerThread : passengers[flight]) passengerThread.join();
            } catch(InterruptedException e) {
                System.err.println("Main: Interrupted - joins1: " + e.toString());
            }
        }

        try {
            busDriver.join();
            porter.join();
        } catch(InterruptedException e) {
            System.out.println("Main: Interrupted - joins2: " + e.toString());
        }

        repositoryMonitor.finalReport();
    }
    /**
     * Function that pseudo-randomly generates the initial case for every flight.
     */
    private static void generateStartingData() {
        Random random = new Random();
        for(int flight = 0; flight < k; flight++) {
            for(int passenger = 0; passenger < n; passenger++) {
                passengerSituations[flight][passenger] = (random.nextDouble() < probFDT)
                        ? PassengerThread.PassengerAndBagSituations.FDT : PassengerThread.PassengerAndBagSituations.TRT;
                passengerLuggage[flight][passenger] = random.nextInt(m + 1);
                int bound = (passengerLuggage[flight][passenger] == 0) ? 1 : passengerLuggage[flight][passenger];
                passengerLuggageAfterMissing[flight][passenger] = passengerLuggage[flight][passenger]
                        - random.nextInt(bound);
                totalLuggagePerFlight[flight] += passengerLuggageAfterMissing[flight][passenger];

                for(int bag = 0; bag < passengerLuggageAfterMissing[flight][passenger]; bag++) {
                    passengerBags[flight][passenger][bag] = new Bag(passenger, passengerSituations[flight][passenger]);
                }
            }
        }
    }
}
