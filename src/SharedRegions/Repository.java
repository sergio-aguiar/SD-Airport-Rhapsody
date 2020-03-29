package SharedRegions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import Entities.*;

/**
 * Repository: Where the logger and general running state are located.
 *
 * @author sergiaguiar
 * @author marcomacedo
 */
public class Repository {
    /**
     * The class's ReentrantLock instance.
     */
    private ReentrantLock reentrantLock;
    /**
     * The current flight number.
     */
    private int flightNumber;
    /**
     * The current amount of luggage on the plane.
     */
    private int numberOfLuggageAtThePlane;
    /**
     * The attribute that states whether the porter as been initiated or not.
     */
    private boolean porterInitiated;
    /**
     * The porter's current state.
     */
    private PorterThread.PorterStates porterState;
    /**
     * The current number of luggage on the conveyor belt.
     */
    private int numberOfLuggageOnConveyor;
    /**
     * The current number of luggage on the store room.
     */
    private int numberOfLuggageAtTheStoreRoom;
    /**
     * The attribute that states whether the bus driver has been initiated or not.
     */
    private boolean busDriverInitiated;
    /**
     * The bus driver's current state.
     */
    private BusDriverThread.BusDriverStates busDriverState;
    /**
     * Array with the bus seat positions.
     */
    private String[] busSeats;
    /**
     * Array with the bus waiting queue's positions.
     */
    private String[] busWaitingQueue;
    /**
     * Array with the attributes that state whether each passenger has been initiated or not.
     */
    private boolean[] passengersInitiated;
    /**
     * Array with each passenger's current state.
     */
    private PassengerThread.PassengerStates[] passengerStates;
    /**
     * Array with each passenger's current flight situation.
     */
    private PassengerThread.PassengerAndBagSituations[] passengerSituations;
    /**
     * Array with the total amount of luggage per flight.
     */
    private int[] passengerLuggageAtStart;
    /**
     * Array with the current amount of collected luggage per flight.
     */
    private int[] passengerLuggageCollected;
    /**
     * The total number of final destination passengers.
     */
    private int numberOfFDTPassengers;
    /**
     * The total number of passengers in transit.
     */
    private int numberOfTRTPassengers;
    /**
     * The total number of bags that should have been transported at the start of each flight.
     */
    private int numberOfBagsThatShouldHaveBeenTransported;
    /**
     * The current amount of lost bags.
     */
    private int numberOfBagsThatWereLost;
    /**
     * The class's FIle instance.
     */
    private File logFile;
    /**
     * The class's BufferedWriter instance.
     */
    private BufferedWriter writer;
    /**
     * Repository constructor.
     * @param flightNumber The current flight number.
     * @param numberOfPassengerLuggageAtThePlane The number of passenger luggage on the plane.
     * @param busSeatNumber The number of bus seats.
     * @param totalPassengers The total number of passengers per flight.
     * @param passengerSituations Array with each passenger's situation.
     * @param passengerLuggageAtStart Array with the total amount of luggage per flight.
     */
    public Repository(int flightNumber, int numberOfPassengerLuggageAtThePlane, int busSeatNumber, int totalPassengers,
                      PassengerThread.PassengerAndBagSituations[] passengerSituations, int[] passengerLuggageAtStart)
            throws IOException {

        this.reentrantLock = new ReentrantLock(true);

        this.flightNumber = flightNumber;
        this.numberOfLuggageAtThePlane = numberOfPassengerLuggageAtThePlane;

        this.porterInitiated = false;
        this.porterState = PorterThread.PorterStates.WAITING_FOR_A_PLANE_TO_LAND;
        this.numberOfLuggageAtTheStoreRoom = 0;
        this.numberOfLuggageOnConveyor = 0;

        this.busDriverInitiated = false;
        this.busDriverState = BusDriverThread.BusDriverStates.PARKING_AT_THE_ARRIVAL_TERMINAL;
        this.busSeats = new String[busSeatNumber];
        this.busWaitingQueue = new String[totalPassengers];
        Arrays.fill(this.busSeats, "-");
        Arrays.fill(this.busWaitingQueue, "-");

        this.passengersInitiated = new boolean[totalPassengers];
        Arrays.fill(this.passengersInitiated, false);
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
    /**
     * Function that closes the BufferedWriter instance.
     */
    private void close() {
        try {
            this.writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Function that writes the current info onto the log file.
     */
    private void log() {
        this.reentrantLock.lock();
        try {

            String line = "";
            //Plane number + number of luggage at the planes
            line += " " + this.flightNumber + "  " + this.numberOfLuggageAtThePlane + "  ";

            // Porter states +  number of pieces of luggage presently on the conveyor belt + number of pieces of luggage belonging to passengers in transit presently stored at the storeroom
            if(this.porterInitiated) line += this.porterState.toString() + "  " + this.numberOfLuggageOnConveyor + "  " + numberOfLuggageAtTheStoreRoom + "   ";
            else line += "---- -- --   ";

            if(this.busDriverInitiated) line += this.busDriverState.toString() + "   ";
            else line += "----   ";
            for(int i = 0; i < 6; i++)
                line += this.busWaitingQueue[i] + "  " ;

            line += "  ";
            for(int i = 0; i <3; i++)
                line += this.busSeats[i] + "  ";

            line += "\n";
            for(int i = 0; i<6; i++){
                if(this.passengersInitiated[i]) line += this.passengerStates[i].toString() + " " + this.passengerSituations[i].toString() + "  " + this.passengerLuggageAtStart[i] + "   " + this.passengerLuggageCollected[i] + "  ";
                else line += "--- ---  -   -  ";
            }
            // line += "\n";

            try {
                this.writer.write((line + "\n"));
                this.writer.flush();
            } catch (IOException ex) {
                Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (Exception e) {
            System.out.print("ATTQ: parkTheBus: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Function that finalizes a log file.
     */
    public void finalReport (){
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

        this.close();
    }
    /**
     * Function that calculate's the number of final destination passengers/passengers in transit based off on their flight situation.
     */
    private void calculatePassengerSituations() {
        for(PassengerThread.PassengerAndBagSituations situation : this.passengerSituations)
            if(situation.toString().equals("TRT")) this.numberOfTRTPassengers++;
            else if(situation.toString().equals("FDT")) this.numberOfFDTPassengers++;
    }
    /**
     * Function that calculates the amount of bags that should have been on the plane.
     */
    private void calculateBagsThatShouldHaveBeenOnThePlane() {
        for(int passengerBags : this.passengerLuggageAtStart)
            this.numberOfBagsThatShouldHaveBeenTransported += passengerBags;
    }
    /**
     * Function that sets a passenger's current state.
     * @param pid The passenger's ID.
     * @param passengerState The passenger's current state.
     */
    private void setPassengerState(int pid, PassengerThread.PassengerStates passengerState) {
        this.passengerStates[pid] = passengerState;
    }
    /**
     * Function that sets the porter's current state.
     * @param porterState The porter's current state.
     */
    private void setPorterState(PorterThread.PorterStates porterState) {
        this.porterState = porterState;
    }
    /**
     * Function that sets the bus driver's current state.
     * @param busDriverState The bus driver's current state.
     */
    private void setBusDriverState(BusDriverThread.BusDriverStates busDriverState) {
        this.busDriverState = busDriverState;
    }
    /**
     * Function that removes a passenger from the bus waiting queue.
     * @param pid The passenger's ID.
     */
    private void removePassengerFromQueue(int pid) {
        String[] tmpQueue = new String[this.passengersInitiated.length];
        int found = -1;
        for(int i = 0; i < this.passengersInitiated.length; i++) {
            if(this.busWaitingQueue[i].equals(String.valueOf(pid))) found = i;
            else {
                if(found != -1) tmpQueue[i - 1] = this.busWaitingQueue[i];
                else tmpQueue[i] = this.busWaitingQueue[i];
            }
        }
        tmpQueue[this.passengersInitiated.length - 1] = "-";
        this.busWaitingQueue = Arrays.copyOf(tmpQueue, this.passengersInitiated.length);
    }
    /**
     * Function that informs that the porter has been initiated.
     */
    public void porterInitiated() {
        this.reentrantLock.lock();
        try {
            this.porterInitiated = true;
        } catch (Exception e) {
            System.out.println("Repository: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Function that updates info based on method result: tryToCollectBag()
     * @param success Whether the bag colelction attempt was successful or not.
     */
    public void porterTryCollectingBagFromPlane(boolean success) {
        this.reentrantLock.lock();
        try {
            this.setPorterState(PorterThread.PorterStates.AT_THE_PLANES_HOLD);
            if(success) this.numberOfLuggageAtThePlane--;
            this.log();
        } catch (Exception e) {
            System.out.println("Repository: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Function that updates info based on method result: carryBagToAppropriateStore()
     */
    public void porterCarryBagToBaggageCollectionPoint() {
        this.reentrantLock.lock();
        try {
            this.setPorterState(PorterThread.PorterStates.AT_THE_LUGGAGE_BELT_CONVEYOR);
            this.numberOfLuggageOnConveyor++;
            this.log();
        } catch (Exception e) {
            System.out.println("Repository: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Function that updates info based on method result: carryBagToAppropriateStore()
     */
    public void porterCarryBagToTemporaryStorageArea() {
        this.reentrantLock.lock();
        try {
            this.setPorterState(PorterThread.PorterStates.AT_THE_STOREROOM);
            this.numberOfLuggageAtTheStoreRoom++;
            this.log();
        } catch (Exception e) {
            System.out.println("Repository: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Function that updates info based on method result: noMoreBagsToCollect()
     */
    public void porterAnnouncingNoMoreBagsToCollect() {
        this.reentrantLock.lock();
        try {
            this.setPorterState(PorterThread.PorterStates.WAITING_FOR_A_PLANE_TO_LAND);
            this.log();
        } catch (Exception e) {
            System.out.println("Repository: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Function that informs that a passenger has been initiated.
     * @param pid The passenger's ID.
     */
    public void passengerInitiated(int pid) {
        this.reentrantLock.lock();
        try {
            this.passengersInitiated[pid] = true;
        } catch (Exception e) {
            System.out.println("Repository: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Function that updates info based on method result: GoCollectABag()
     * @param pid The passenger's ID.
     */
    public void passengerGoingToCollectABag(int pid) {
        this.reentrantLock.lock();
        try {
            this.setPassengerState(pid, PassengerThread.PassengerStates.AT_THE_LUGGAGE_COLLECTION_POINT);
            this.log();
        } catch (Exception e) {
            System.out.println("Repository: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Function that updates info based on method result: tryToCollectABag()
     * @param pid The passenger's ID.
     */
    public void passengerCollectingABag(int pid) {
        this.reentrantLock.lock();
        try {
            this.numberOfLuggageOnConveyor--;
            this.passengerLuggageCollected[pid]++;
            this.log();
        } catch (Exception e) {
            System.out.println("Repository: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Function that updates info based on method result: goHome()
     * @param pid The passenger's ID.
     */
    public void passengerGoingHome(int pid) {
        this.reentrantLock.lock();
        try {
            this.setPassengerState(pid, PassengerThread.PassengerStates.EXITING_THE_ARRIVAL_TERMINAL);
            this.log();
        } catch (Exception e) {
            System.out.println("Repository: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Function that updates info based on method result: EnterTheBus()
     * @param pid The passenger's ID.
     */
    public void passengerEnteringTheBus(int pid, int seat) {
        this.reentrantLock.lock();
        try {
            this.removePassengerFromQueue(pid);
            this.setPassengerState(pid, PassengerThread.PassengerStates.TERMINAL_TRANSFER);
            this.busSeats[seat] = String.valueOf(pid);
            this.log();
        } catch (Exception e) {
            System.out.println("Repository: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Function that updates info based on method result: takeABus()
     * @param pid The passenger's ID.
     */
    public void passengerTakingABus(int pid) {
        this.reentrantLock.lock();
        try {
            this.setPassengerState(pid, PassengerThread.PassengerStates.AT_THE_ARRIVAL_TRANSFER_TERMINAL);
            this.log();
        } catch (Exception e) {
            System.out.println("Repository: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Function that updates info based on method result: reportMissingBags()
     * @param pid The passenger's ID.
     */
    public void passengerReportingMissingBags(int pid, int missingBags) {
        this.reentrantLock.lock();
        try {
            this.setPassengerState(pid, PassengerThread.PassengerStates.AT_THE_LUGGAGE_RECLAIM_OFFICE);
            this.numberOfBagsThatWereLost += missingBags;
            this.log();
        } catch (Exception e) {
            System.out.println("Repository: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Function that updates info based on method result: prepareForNextLeg()
     * @param pid The passenger's ID.
     */
    public void passengerPreparingNextLeg(int pid) {
        this.reentrantLock.lock();
        try {
            this.setPassengerState(pid, PassengerThread.PassengerStates.ENTERING_THE_DEPARTURE_TERMINAL);
            this.log();
        } catch (Exception e) {
            System.out.println("Repository: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Function that updates info based on method result: LeaveTheBus()
     * @param pid The passenger's ID.
     * @param seat The passenger's bus seat.
     */
    public void passengerLeavingTheBus(int pid, int seat) {
        this.reentrantLock.lock();
        try {
            this.setPassengerState(pid, PassengerThread.PassengerStates.AT_THE_DEPARTURE_TRANSFER_TERMINAL);
            this.busSeats[seat] = "-";
            this.log();
        } catch (Exception e) {
            System.out.println("Repository: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Function that updates info based on method result: TakeABus()
     * @param pid The passenger's ID.
     * @param position The passenger's position.
     */
    public void passengerGettingIntoTheWaitingQueue(int pid, int position) {
        this.reentrantLock.lock();
        try {
            this.busWaitingQueue[position] = String.valueOf(pid);
            this.log();
        } catch (Exception e) {
            System.out.println("Repository: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Function that informs that the bus driver has been initiated.
     */
    public void busDriverInitiated() {
        this.reentrantLock.lock();
        try {
            this.busDriverInitiated = true;
        } catch (Exception e) {
            System.out.println("Repository: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Function that updates info based on method result: parkTheBus()
     */
    public void busDriverParkingTheBus() {
        this.reentrantLock.lock();
        try {
            this.setBusDriverState(BusDriverThread.BusDriverStates.PARKING_AT_THE_ARRIVAL_TERMINAL);
            this.log();
        } catch (Exception e) {
            System.out.println("Repository: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Function that updates info based on method result: goToDepartureTerminal()
     */
    public void busDriverGoingToDepartureTerminal() {
        this.reentrantLock.lock();
        try {
            this.setBusDriverState(BusDriverThread.BusDriverStates.DRIVING_FORWARD);
            this.log();
        } catch (Exception e) {
            System.out.println("Repository: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Function that updates info based on method result: goToArrivalTerminal()
     */
    public void busDriverGoingToArrivalTerminal() {
        this.reentrantLock.lock();
        try {
            this.setBusDriverState(BusDriverThread.BusDriverStates.DRIVING_BACKWARD);
            this.log();
        } catch (Exception e) {
            System.out.println("Repository: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Function that updates info based on method result: parkTheBusAndLetPassOff()
     */
    public void busDriverParkingTheBusAndLettingPassengersOff() {
        this.reentrantLock.lock();
        try {
            this.setBusDriverState(BusDriverThread.BusDriverStates.PARKING_AT_THE_DEPARTURE_TERMINAL);
            this.log();
        } catch (Exception e) {
            System.out.println("Repository: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Function that allows for a transition to a new flight (new plane landing simulation).
     */
    public void prepareForNextFlight(int numberOfPassengerLuggageAtThePlane, PassengerThread.PassengerAndBagSituations[]
                                     passengerSituations) {
        this.flightNumber++;
        this.numberOfLuggageAtThePlane = numberOfPassengerLuggageAtThePlane;
        this.porterState = PorterThread.PorterStates.WAITING_FOR_A_PLANE_TO_LAND;
        this.numberOfLuggageAtTheStoreRoom = 0;
        this.numberOfLuggageOnConveyor = 0;
        this.busDriverState = BusDriverThread.BusDriverStates.PARKING_AT_THE_ARRIVAL_TERMINAL;
        Arrays.fill(this.busSeats, "-");
        Arrays.fill(this.busWaitingQueue, "-");
        Arrays.fill(this.passengersInitiated, false);
        Arrays.fill(this.passengerStates, PassengerThread.PassengerStates.AT_THE_DISEMBARKING_ZONE);
        this.passengerSituations = passengerSituations;
        Arrays.fill(this.passengerLuggageCollected, 0);
        this.calculatePassengerSituations();
        this.calculateBagsThatShouldHaveBeenOnThePlane();
        this.numberOfBagsThatWereLost = 0;
    }
}

