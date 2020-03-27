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
 * @author sergiaguiar.
 * @author marcomacedo
 */
public class ArrivalLounge implements ALPassenger, ALPorter {

    private final ReentrantLock reentrantLock;
    private final Condition porterCondition;

    private final int totalPassengers;
    private int passengersThatArrived;

    private int flightNumber;

    private final int[] luggageNumberPerFlight;
    private final Bag[][] luggagePerFlight;
    private Stack<Bag> bagsInThePlane;
    private int luggagePickedUp;

    private final Repository repository;

    public ArrivalLounge(Repository repository, int totalPassengers, int[] luggageNumberPerFlight,
                         Bag[][] luggagePerFlight) {
        this.reentrantLock = new ReentrantLock();
        this.porterCondition = this.reentrantLock.newCondition();
        this.totalPassengers = totalPassengers;
        this.passengersThatArrived = 0;
        this.flightNumber = 0;
        this.luggageNumberPerFlight = luggageNumberPerFlight;
        this.luggagePerFlight = luggagePerFlight;
        this.bagsInThePlane = new Stack<>();
        this.bagArrayToStack(0);
        this.luggagePickedUp = 0;
        this.repository = repository;
    }

    private void bagArrayToStack(int flightNumber) {
        for(int i = 0; i < this.luggagePerFlight[this.flightNumber].length; i++)
            if(this.luggagePerFlight[this.flightNumber][i] != null)
                this.bagsInThePlane.push(this.luggagePerFlight[this.flightNumber][i]);
    }

    public void nextFlight() {
        this.flightNumber++;
        this.bagArrayToStack(this.flightNumber);
    }
	  
    /** 
     * Porter method: The porter takes a rest.
     * 
     * @param pid Porter id.
     * @return true if taking a rest and false otherwise.
     */
    @Override
    public boolean takeARest(int pid) {
        boolean done = false;
        this.reentrantLock.lock();
        try {
            this.porterCondition.await();
        } catch (Exception e) {
            System.out.print(e.toString());
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
    public void whatShouldIDo(int pid) {
        this.passengersThatArrived++;
        if(this.passengersThatArrived == this.totalPassengers) this.porterCondition.signal();
    }
	 /**
     * Porter method: the Porter tries to collect a Bag.
     * @param pid Porter id.
     * @return 
     */
    @Override
    public String tryToCollectABag(int pid) {
        if(this.luggageNumberPerFlight[this.flightNumber] != this.luggagePickedUp) {
            this.repository.porterTryCollectingBagFromPlane(true);
            return this.bagsInThePlane.pop().toString();
        }
        this.repository.porterTryCollectingBagFromPlane(false);
        return null;
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
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
}
