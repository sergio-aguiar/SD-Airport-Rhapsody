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
    
    /**
     * Instance the Repository.
     */
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
    
    /**
     * 
     * @param flightNumber 
     */
    private void bagArrayToStack(int flightNumber) {
        for(int i = 0; i < this.luggagePerFlight[this.flightNumber].length; i++)
            if(this.luggagePerFlight[this.flightNumber][i] != null)
                bagsInThePlane.push(this.luggagePerFlight[this.flightNumber][i]);
    }
    /**
     * 
     */
    public void nextFlight() {
        this.flightNumber++;
        this.bagArrayToStack(this.flightNumber);
    }
    
    /** 
     * Porter method: The porter takes a rest.
     * 
     * @param pid Poter id.
     * @return true if take a rest or false otherwise.
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
        this.repository.setPorterState(pid, PorterThread.PorterStates.AT_THE_PLANES_HOLD);
        if(this.luggageNumberPerFlight[this.flightNumber] != this.luggagePickedUp)
            return this.bagsInThePlane.pop().getBagSituation().toString();
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
            this.repository.setPassengerState(pid, PassengerThread.PassengerStates.AT_THE_LUGGAGE_COLLECTION_POINT);
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Porter method: The porter has no more bags to collect.
     * @param pid Porter id.
     */
    @Override
    public void noMoreBagsToCollect(int pid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
