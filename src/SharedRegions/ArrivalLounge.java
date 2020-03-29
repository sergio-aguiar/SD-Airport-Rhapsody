package SharedRegions;

import Entities.PassengerThread;
import Entities.PorterThread;
import Extras.Bag;
import Interfaces.ALPassenger;
import Interfaces.ALPorter;

import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
/**
 * Arrival Lounge: Where the Passenger arrives and the Porter stays.
 * Used by PORTER and PASSENGER.
 * @author sergiaguiar
 * @author marcomacedo
 */
public class ArrivalLounge implements ALPassenger, ALPorter {

    private final ReentrantLock reentrantLock;
    private final Condition porterCondition;

    private final int maxCrossFlightPassengers;
    private int crossFlightPassengerCount;

    /**
     * Number of total passengers.
     */
    private final int totalPassengers;
    /**
     * Number of Passengers that arrived at airport.
     */
    private int passengersThatArrived;
    /**
     * Flight number.
     */
    private int flightNumber;

    private int totalFlights;
    /**
     * Array of luggage number per flight.
     */
    private final int[] luggageNumberPerFlight;
    
    /**
     * Lugagge per flight.
     */
    private final Bag[][][] luggagePerFlight;
    
    /**
     * Stack with Bags in the plane.
     */
    private Stack<Bag> bagsInThePlane;
    /**
     * Number of luggage picked up.
     */
    private int luggagePickedUp;
    /**
     * Instace of the Repository.
     */
    private final Repository repository;
    /**
     * Arrival Lounge constructor.
     * @param repository repository.
     * @param totalPassengers Number os total passengers.
     * @param luggageNumberPerFlight Luggage number per flight.
     * @param luggagePerFlight Luggage per flight.
     */
    public ArrivalLounge(Repository repository, int totalPassengers, int totalFlights, int[] luggageNumberPerFlight,
                         Bag[][][] luggagePerFlight) {
        this.reentrantLock = new ReentrantLock();
        this.porterCondition = this.reentrantLock.newCondition();
        this.maxCrossFlightPassengers = totalFlights * totalPassengers;
        this.crossFlightPassengerCount = 0;
        this.totalPassengers = totalPassengers;
        this.totalFlights = totalFlights;
        this.passengersThatArrived = 0;
        this.flightNumber = 0;
        this.luggageNumberPerFlight = luggageNumberPerFlight;
        this.luggagePerFlight = luggagePerFlight;
        this.bagsInThePlane = new Stack<>();
        this.bagArrayToStack(0);
        this.luggagePickedUp = 0;
        this.repository = repository;
    }
    /**
     * Bag array to Stack.
     * @param flightNumber Flight number.
     */
    private void bagArrayToStack(int flightNumber) {
        for(int i = 0; i < this.luggagePerFlight[flightNumber].length; i++)
            for(int j = 0; j < this.luggagePerFlight[flightNumber][i].length; j++)
                if(this.luggagePerFlight[flightNumber][i][j] != null)
                    this.bagsInThePlane.push(this.luggagePerFlight[flightNumber][i][j]);
    }
    
    /**
     * Incremet the flight number to the next flight.
     */
    public void nextFlight() {
        this.flightNumber++;
        this.bagArrayToStack(this.flightNumber);
    }

    public boolean passengersNoLongerNeedTheBus() {
        boolean maxReached = false;
        this.reentrantLock.lock();
        try {
            maxReached = this.maxCrossFlightPassengers == this.crossFlightPassengerCount;
        } catch (Exception e) {
            System.out.println("AL: incrementCrossFLightPassengerCount: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
        return maxReached;
    }

    public void incrementCrossFlightPassengerCount() {
        this.reentrantLock.lock();
        try {
            this.crossFlightPassengerCount++;
        } catch (Exception e) {
            System.out.println("AL: incrementCrossFLightPassengerCount: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }

    /** 
     * Porter method: The porter takes a rest.
     * 
     * @param pid Porter id.
     * @return true if taking a rest and false otherwise.
     */
    @Override
    public boolean takeARest(int pid) {
        boolean done = (this.flightNumber == this.totalFlights - 1 && this.bagsInThePlane.size() == 0);
        this.reentrantLock.lock();
        try {
            this.repository.porterInitiated();
            if(!done) this.porterCondition.await();
        } catch (Exception e) {
            System.out.println("AL: takeARest: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
        return done;
    }
	/**
     * Passenger method: the passenger is in Transit or Final destination.
     * @param pid Passenger id.
     */
    @Override
    public void whatShouldIDo(int pid, String situation) {
        this.reentrantLock.lock();
        try {
            this.repository.passengerInitiated(pid);
            this.passengersThatArrived++;
            if(situation.equals("FDT")) this.incrementCrossFlightPassengerCount();
            if(this.passengersThatArrived == this.totalPassengers) this.porterCondition.signal();
        } catch (Exception e) {
            System.out.println("AL: whatShouldIDo: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Porter method: the Porter tries to collect a Bag.
     * @param pid Porter id.
     * @return 
     */
    @Override
    public String tryToCollectABag(int pid) {
        String returnVal = "";
        this.reentrantLock.lock();
        try {
            if(!this.bagsInThePlane.isEmpty()) {
                returnVal = this.bagsInThePlane.pop().toString();
                this.repository.porterTryCollectingBagFromPlane(true);
            }
            this.repository.porterTryCollectingBagFromPlane(false);
        } catch (Exception e) {
            System.out.println("AL: tryToCollectABag: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
        return returnVal;
    }
    /**
     * Passenger method: the Passenger goes collect a bag.
     * @param pid Passenger id.
     */
    @Override
    public void goCollectABag(int pid) {
        this.reentrantLock.lock();
        try {
            this.repository.passengerGoingToCollectABag(pid);
        } catch (Exception e) {
            System.out.println("AL: goCollectABag: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
}
