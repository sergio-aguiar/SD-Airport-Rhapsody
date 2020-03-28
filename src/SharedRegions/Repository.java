package SharedRegions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import Entities.*;

public class Repository {

    private int flightNumber;
    private int numberOfLuggageAtThePlane;

    private PorterThread.PorterStates porterState;
    private int numberOfLuggageOnConveyor;
    private int numberOfLuggageAtTheStoreRoom;

    private BusDriverThread.BusDriverStates busDriverState;
    private String[] busSeats;
    private String[] busWaitingQueue;

    private PassengerThread.PassengerStates[] passengerStates;
    private PassengerThread.PassengerAndBagSituations[] passengerSituations;
    private int[] passengerLuggageAtStart;
    private int[] passengerLuggageCollected;

    private int numberOfFDTPassengers;
    private int numberOfTRTPassengers;
    private int numberOfBagsThatShouldHaveBeenTransported;
    private int numberOfBagsThatWereLost;

    private File logFile;
    private BufferedWriter writer;

    public Repository(int flightNumber, int numberOfPassengerLuggageAtThePlane, int busSeatNumber, int totalPassengers,
                      PassengerThread.PassengerAndBagSituations[] passengerSituations, int[] passengerLuggageAtStart)
            throws IOException {

        this.flightNumber = flightNumber;
        this.numberOfLuggageAtThePlane = numberOfPassengerLuggageAtThePlane;

        this.porterState = PorterThread.PorterStates.WAITING_FOR_A_PLANE_TO_LAND;
        this.numberOfLuggageAtTheStoreRoom = 0;
        this.numberOfLuggageOnConveyor = 0;

        this.busDriverState = BusDriverThread.BusDriverStates.PARKING_AT_THE_ARRIVAL_TERMINAL;
        this.busSeats = new String[busSeatNumber];
        this.busWaitingQueue = new String[totalPassengers];
        Arrays.fill(this.busSeats, "-");
        Arrays.fill(this.busWaitingQueue, "-");

        this.passengerStates = new PassengerThread.PassengerStates[6];
        Arrays.fill(this.passengerStates, PassengerThread.PassengerStates.AT_THE_DISEMBARKING_ZONE);
        this.passengerSituations = passengerSituations;
        this.passengerLuggageAtStart = passengerLuggageAtStart;
        this.passengerLuggageCollected = new int[totalPassengers];
        Arrays.fill(this.passengerLuggageCollected, 0);

        this.calculatePassengerSituations();
        this.calculateBagsThatShouldHaveBeenOnThePlane();
        this.numberOfBagsThatWereLost = 0;

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
        //finalReport();
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
        line += " " + this.flightNumber + "  " + this.numberOfLuggageAtThePlane + "  ";
        // Porter states +  number of pieces of luggage presently on the conveyor belt + number of pieces of luggage belonging to passengers in transit presently stored at the storeroom
        line += this.porterState.toString() + "  " + this.numberOfLuggageOnConveyor + "  " + numberOfLuggageAtTheStoreRoom + "   ";
        
        line += this.busDriverState.toString() + "   ";
        for(int i = 0; i < 6; i++)
            line += this.busWaitingQueue[i] + "  " ;
        
        line += "  ";
        for(int i = 0; i <3; i++)
            line += this.busSeats[i] + "  ";
        
        line += "\n";
        for(int i = 0; i<6; i++){
            line += this.passengerStates[i].toString() + " " + this.passengerSituations[i].toString() + "  " + this.passengerLuggageAtStart[i] + "   " + this.passengerLuggageCollected[i] + "  "; 
        }
       // line += "\n";   

        try {
            this.writer.write((line + "\n"));
            this.writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    private void finalReport (){   
        String finalReport = "";      
        finalReport += "Final Report";
        finalReport += "N. of passengers which have this airport as their final destination = " + this.numberOfFDTPassengers + "\n";
        finalReport += "N. of passengers in transit = "+ this.numberOfTRTPassengers + "\n";
        finalReport += "N. of bags that should have been transported in the the planes hold = " + this.numberOfBagsThatShouldHaveBeenTransported + "\n";
        finalReport += "N. of bags that were lost = " + this.numberOfBagsThatWereLost + "\n";
        try {
            this.writer.write((finalReport + "\n"));
        } catch (IOException ex) {
            Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
        }     
    }
    private void calculatePassengerSituations() {
        for(PassengerThread.PassengerAndBagSituations situation : this.passengerSituations)
            if(situation.toString().equals("TRT")) this.numberOfTRTPassengers++;
            else if(situation.toString().equals("FDT")) this.numberOfFDTPassengers++;
    }

    private void calculateBagsThatShouldHaveBeenOnThePlane() {
        for(int passengerBags : this.passengerLuggageAtStart)
            this.numberOfBagsThatShouldHaveBeenTransported += passengerBags;
    }

    private void setPassengerState(int pid, PassengerThread.PassengerStates passengerState) {
        this.passengerStates[pid] = passengerState;
    }

    private void setPorterState(PorterThread.PorterStates porterState) {
        this.porterState = porterState;
    }

    private void setBusDriverState(BusDriverThread.BusDriverStates busDriverState) {
        this.busDriverState = busDriverState;
    }

    public void porterTryCollectingBagFromPlane(boolean success) {
        this.setPorterState(PorterThread.PorterStates.AT_THE_PLANES_HOLD);
        if(success) this.numberOfLuggageAtThePlane--;
        this.log();
    }

    public void porterCarryBagToBaggageCollectionPoint() {
        this.setPorterState(PorterThread.PorterStates.AT_THE_LUGGAGE_BELT_CONVEYOR);
        this.numberOfLuggageOnConveyor++;
        this.log();
    }

    public void porterCarryBagToTemporaryStorageArea() {
        this.setPorterState(PorterThread.PorterStates.AT_THE_STOREROOM);
        this.numberOfLuggageAtTheStoreRoom++;
        this.log();
    }

    public void porterAnnouncingNoMoreBagsToCollect() {
        this.setPorterState(PorterThread.PorterStates.WAITING_FOR_A_PLANE_TO_LAND);
        this.log();
    }

    public void passengerGoingToCollectABag(int pid) {
        this.setPassengerState(pid, PassengerThread.PassengerStates.AT_THE_LUGGAGE_COLLECTION_POINT);
        this.log();
    }

    public void passengerCollectingABag(int pid) {
        this.numberOfLuggageOnConveyor--;
        this.passengerLuggageCollected[pid]++;
        this.log();
    }

    public void passengerGoingHome(int pid) {
        this.setPassengerState(pid, PassengerThread.PassengerStates.EXITING_THE_ARRIVAL_TERMINAL);
        this.log();
    }

    public void passengerEnteringTheBus(int pid, int seat) {
        this.setPassengerState(pid, PassengerThread.PassengerStates.TERMINAL_TRANSFER);
        this.busSeats[seat] = String.valueOf(pid);
        this.log();
    }

    public void passengerTakingABus(int pid) {
        this.setPassengerState(pid, PassengerThread.PassengerStates.AT_THE_ARRIVAL_TRANSFER_TERMINAL);
        this.log();
    }

    public void passengerReportingMissingBags(int pid, int missingBags) {
        this.setPassengerState(pid, PassengerThread.PassengerStates.AT_THE_LUGGAGE_RECLAIM_OFFICE);
        this.numberOfBagsThatWereLost += missingBags;
        this.log();
    }

    public void passengerPreparingNextLeg(int pid) {
        this.setPassengerState(pid, PassengerThread.PassengerStates.ENTERING_THE_DEPARTURE_TERMINAL);
        this.log();
    }

    public void passengerLeavingTheBus(int pid, int seat) {
        this.setPassengerState(pid, PassengerThread.PassengerStates.AT_THE_DEPARTURE_TRANSFER_TERMINAL);
        this.busSeats[seat] = "-";
        this.log();
    }

    public void passengerGettingIntoTheWaitingQueue(int pid, int position) {
        this.busWaitingQueue[position] = String.valueOf(pid);
        this.log();
    }

    public void passengerGettingOutOfTheWaitingQueue(int position) {
        this.busWaitingQueue[position] = "-";
        this.log();
    }

    public void busDriverParkingTheBus() {
        this.setBusDriverState(BusDriverThread.BusDriverStates.PARKING_AT_THE_ARRIVAL_TERMINAL);
        this.log();
    }

    public void busDriverGoingToDepartureTerminal() {
        this.setBusDriverState(BusDriverThread.BusDriverStates.DRIVING_FORWARD);
        this.log();
    }

    public void busDriverGoingToArrivalTerminal() {
        this.setBusDriverState(BusDriverThread.BusDriverStates.DRIVING_BACKWARD);
        this.log();
    }

    public void busDriverParkingTheBusAndLettingPassengersOff() {
        this.setBusDriverState(BusDriverThread.BusDriverStates.PARKING_AT_THE_DEPARTURE_TERMINAL);
        this.log();
    }
}

