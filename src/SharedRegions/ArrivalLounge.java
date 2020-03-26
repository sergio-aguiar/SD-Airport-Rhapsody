package SharedRegions;

import Entities.PassengerThread;
import Entities.PorterThread;
import Extras.Bag;
import Interfaces.ALPassenger;
import Interfaces.ALPorter;

import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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
                bagsInThePlane.push(this.luggagePerFlight[this.flightNumber][i]);
    }

    public void nextFlight() {
        this.flightNumber++;
        this.bagArrayToStack(this.flightNumber);
    }

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

    @Override
    public void whatShouldIDo(int pid) {
        this.passengersThatArrived++;
        if(this.passengersThatArrived == this.totalPassengers) this.porterCondition.signal();
    }

    @Override
    public String tryToCollectABag(int pid) {
        this.repository.setPorterState(pid, PorterThread.PorterStates.AT_THE_PLANES_HOLD);
        if(this.luggageNumberPerFlight[this.flightNumber] != this.luggagePickedUp)
            return this.bagsInThePlane.pop().getBagSituation().toString();
        return null;
    }

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
}
