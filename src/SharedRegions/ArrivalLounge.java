package SharedRegions;

import Extras.Bag;
import Interfaces.ALPassenger;
import Interfaces.ALPorter;

import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
/**
 * Arrival Lounge: Where the Passenger arrives and the Porter awaits a plane to begin working.
 * Used by PORTER and PASSENGER.
 * @author sergiaguiar
 * @author marcomacedo
 */
public class ArrivalLounge implements ALPassenger, ALPorter {
    /**
     * The class's ReentrantLock instance.
     */
    private final ReentrantLock reentrantLock;
    /**
     * The Condition instance where the porter awaits for every passenger to leave the plane.
     */
    private final Condition porterCondition;
    /**
     * The total amount of passengers throughout every planned flight.
     */
    private final int maxCrossFlightPassengers;
    /**
     * A count for the amount of passengers throughout every planned flight who have made it past where the bus driver is needed.
     */
    private int crossFlightPassengerCount;
    /**
     * Total number of passengers per flight.
     */
    private final int totalPassengers;
    /**
     * Number of Passengers that arrived at airport on the current flight.
     */
    private int passengersThatArrived;
    /**
     * The current flight number.
     */
    private int flightNumber;
    /**
     * Total number of flights.
     */
    private int totalFlights;
    /**
     * Array that contains the amount of luggage coming inside of every plane (every flight).
     */
    private final int[] luggageNumberPerFlight;
    /**
     * Array that contains the bags of each passenger per flight.
     */
    private final Bag[][][] luggagePerFlight;
    /**
     * Stack that contains the bags currently in the plane.
     */
    private Stack<Bag> bagsInThePlane;
    /**
     * Number of luggage currently picked up by the porter.
     */
    private int luggagePickedUp;
    /**
     * The class's Repository instance.
     */
    private final Repository repository;
    /**
     * ArrivalLounge constructor.
     * @param repository A reference to a repository object.
     * @param totalPassengers Total number of passengers per flight.
     * @param totalFlights Total number of flights.
     * @param luggageNumberPerFlight Array that contains the amount of luggage coming inside of every plane (every flight).
     * @param luggagePerFlight Array that contains the bags of each passenger per flight.
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
     * Function that fills the plane's bag Stack depending on the current flight number.
     * @param flightNumber Flight number.
     */
    private void bagArrayToStack(int flightNumber) {
        for(int i = 0; i < this.luggagePerFlight[flightNumber].length; i++)
            for(int j = 0; j < this.luggagePerFlight[flightNumber][i].length; j++)
                if(this.luggagePerFlight[flightNumber][i][j] != null)
                    this.bagsInThePlane.push(this.luggagePerFlight[flightNumber][i][j]);
    }
    /**
     * Function that allows for a transition to a new flight (new plane landing simulation).
     */
    public void prepareForNextFLight() {
        this.passengersThatArrived = 0;
        this.flightNumber++;
        this.bagArrayToStack(this.flightNumber);
        this.luggagePickedUp = 0;
    }
    /**
     * Function that verifies if any more passengers in the future need the bus driver's services.
     * @return true if no future passengers need the bus driver's services and false otherwise.
     */
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
    /**
     * Function that increments the count for the number of passengers throughout every planned flight who have made it past where the bus driver is needed.
     */
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
     * The porter checks whether he is still needed in the future. If so, he awaits the next flight. He stops otherwise.
     * @param pid The porter's ID.
     * @return true if the porter isn't needed anymore and false otherwise.
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
     * Passenger's first action when arriving from the plane. Signal's the porter if the final passenger to arrive.
     * @param pid Passenger id.
     * @param situation A string representation of the passenger's travel situation.
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
     * The porter tries to collect a bag from the plane's hold.
     * @param pid The Porter's ID.
     * @return the bag's owner ID as a String, or an empty String if the plane is empty of bags.
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
     * The Passenger decides to move towards the Baggage Collection Point to collect a bag.
     * @param pid The passenger's ID.
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
