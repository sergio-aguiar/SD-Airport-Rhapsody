package SharedRegions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import Entities.*;
import Extras.Bag;

public class Repository {

    private int flightNumber;

    private final int numberOfPassengers;
    private final int maxNumberOfLuggagePerPassenger;
    private final int busSeatNumber;

    private int numberOfPassengerLuggageAtStart;
    private int numberOfPassengerLuggagePresentlyCollected;

    private int numberOfLuggageAtThePlane;
    private int numberOfLuggageOnConveyor;
    private int numberOfLuggageAtTheStoreRoom;

    private Bag porterHeldBag;

    private String[] busSeats;
    private String[] busWaitingQueue;

    private PassengerThread.PassengerStates[] passengerStates;
    private BusDriverThread.BusDriverStates[] busDriverStates;
    private PorterThread.PorterStates[] porterStates;

    private PassengerThread.PassengerAndBagSituations[] passengerSituations;

    private File logFile;
    private BufferedWriter writer;

    public Repository(int numberOfPassengerLuggageAtStart, int maxNumberOfLuggagePerPassenger, int flightNumber,
                      int busSeatNumber, int numberOfPassengers, String[] passengerSituations) throws IOException {

        this.flightNumber = flightNumber;

        this.numberOfPassengers = numberOfPassengers;
        this.maxNumberOfLuggagePerPassenger = maxNumberOfLuggagePerPassenger;
        this.busSeatNumber = busSeatNumber;

        this.numberOfPassengerLuggageAtStart = numberOfPassengerLuggageAtStart;
        this.numberOfPassengerLuggagePresentlyCollected = 0;

        this.numberOfLuggageAtThePlane = numberOfPassengerLuggageAtStart;
        this.numberOfLuggageAtTheStoreRoom = 0;
        this.numberOfLuggageOnConveyor = 0;

        this.busSeats = new String[busSeatNumber];
        this.busWaitingQueue = new String[numberOfPassengers];

        Arrays.fill(this.busSeats, "-");
        Arrays.fill(this.busWaitingQueue, "-");

        this.passengerStates = new PassengerThread.PassengerStates[6];
        this.busDriverStates = new BusDriverThread.BusDriverStates[1];
        this.porterStates = new PorterThread.PorterStates[1];

        Arrays.fill(this.passengerStates, PassengerThread.PassengerStates.AT_THE_DISEMBARKING_ZONE);
        Arrays.fill(this.busDriverStates, BusDriverThread.BusDriverStates.PARKING_AT_THE_ARRIVAL_TERMINAL);
        Arrays.fill(this.porterStates, PorterThread.PorterStates.WAITING_FOR_A_PLANE_TO_LAND);

        this.passengerSituations = Arrays.copyOf(this.stringArrayToSituationArray(passengerSituations), passengerSituations.length);

        // open data stream to log file
        this.logFile = new File("logFile_" + System.nanoTime() + ".txt");
        this.writer = new BufferedWriter(new FileWriter(this.logFile));

        // write static elements
        this.writer.write("              AIRPORT RHAPSODY - Description of the internal state of the problem\n");
        this.writer.write(" \n");
        this.writer.write("PLANE    PORTER                  DRIVER\n");
        this.writer.write("FN BN  Stat CB SR   Stat  Q1 Q2 Q3 Q4 Q5 Q6  S1 S2 S3\n");
        this.writer.write("                                                         PASSENGERS\n");
        this.writer.write("St1 Si1 NR1 NA1 St2 Si2 NR2 NA2 St3 Si3 NR3 NA3 St4 Si4 NR4 NA4 St5 Si5 NR5 NA5 St6 Si6 NR6 NA6 \n");

        log();
    }

    private boolean close() {
        try {
            this.writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    private void log() {

        String line = "";
        //Plane number + number of luggage at the planes
        line += this.flightNumber + " " + this.numberOfLuggageAtThePlane + "   ";
        // Porter states
        line += this.porterStates[0] + " ";
        
        try {
            this.writer.write((line + "\n"));
        } catch (IOException ex) {
            Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private PassengerThread.PassengerAndBagSituations[] stringArrayToSituationArray(String[] situations) {
        PassengerThread.PassengerAndBagSituations[] tmpSituations = new PassengerThread.PassengerAndBagSituations[situations.length];
        for(int i = 0; i < situations.length; i++)
            tmpSituations[i] = PassengerThread.PassengerAndBagSituations.valueOf(situations[i]);
        return tmpSituations;
    }

    public void addToWaitingQueue(int pid, int positionInQueue) {
        this.busWaitingQueue[positionInQueue] = String.valueOf(pid);
    }

    public void removeFromWaitingQueue(int pid) {
        String[] tmpQueue = new String[this.numberOfPassengers];
        boolean found = false;
        for(int i = 0; i < this.numberOfPassengers; i++) {
            if(this.busWaitingQueue[i].equals(String.valueOf(pid))) found = true;
            else {
                if(found) tmpQueue[i - 1] = this.busWaitingQueue[i];
                else tmpQueue[i] = this.busWaitingQueue[i];
            }
        }
        tmpQueue[this.numberOfPassengers - 1] = "-";
        this.busWaitingQueue = Arrays.copyOf(tmpQueue, this.numberOfPassengers);
    }

    public void clearBusWaitingQueue() {
        Arrays.fill(this.busWaitingQueue, "-");
    }

    public void addToBusSeats(int pid, int seatInBus) {
        this.busSeats[seatInBus] = String.valueOf(pid);
    }

    public void removeFromBusSeats(int pid) {
        String[] tmpSeats = new String[this.busSeatNumber];
        boolean found = false;
        for(int i = 0; i < this.busSeatNumber; i++) {
            if(this.busSeats[i].equals(String.valueOf(pid))) found = true;
            else {
                if(found) tmpSeats[i - 1] = this.busSeats[i];
                else tmpSeats[i] = this.busSeats[i];
            }
        }
        tmpSeats[this.busSeatNumber - 1] = "-";
        this.busSeats = Arrays.copyOf(tmpSeats, this.busSeatNumber);
    }

    public void clearBusSeats() {
        Arrays.fill(this.busSeats, "-");
    }

    public int numberOfPassengersInBus() {
        int passengerCount = 0;
        for(String seat : this.busSeats) if(!seat.equals("-")) passengerCount++;
        return passengerCount;
    }

    public void setPassengerState(int pid, PassengerThread.PassengerStates passengerState) {
        this.passengerStates[pid] = passengerState;
    }

    public void setPorterState(int pid, PorterThread.PorterStates porterState) {
        this.porterStates[pid] = porterState;
    }

    public void setBusDriverState(int bid, BusDriverThread.BusDriverStates busDriverState) {
        this.busDriverStates[bid] = busDriverState;
    }

    public int getNumberOfPassengers() {
        return this.numberOfPassengers;
    }

    public int getBusSeatNumber() {
        return this.busSeatNumber;
    }

    public PassengerThread.PassengerAndBagSituations getPassengerSituation(int pid) {
        return this.passengerSituations[pid];
    }
}

