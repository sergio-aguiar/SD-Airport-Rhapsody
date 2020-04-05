package SharedRegions;

import Interfaces.DTEPassenger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/** Departure Terminal Entrance: Where passengers await the last one to reach their destination within the airport to signal them that they can leave.
 * Used by PASSENGER.
 * @author sergiaguiar
 * @author marcomacedo
 */
public class DepartureTerminalEntrance implements DTEPassenger {
    /**
     * The class's ReentrantLock instance.
     */
    private final ReentrantLock reentrantLock;
    /**
     * The Condition instance where the passengers wait for the last passenger to arrive at their final destination in the airport.
     */
    private final Condition passengerCondition;
    /**
     * Attribute that states whether all passengers have arrived at their final destination within the airport.
     */
    private int allDone;
    /**
     * Attribute that states whether all passengers were signalled by the last one to arrive yet.
     */
    private boolean allSignaled;
    /**
     * Total number of passengers per flight.
     */
    private final int totalPassengers;
    /**
     * Number of passengers waiting for the last one to arrive at their final destination inside the airport.
     */
    private int waitingPassengers;
    /**
     * The class's instance of the Arrival Terminal Exit.
     */
    private ArrivalTerminalExit ate;
    /**
     * Instance of Repository.
     */
    private final Repository repository;
    
    /**
     * DepartureTerminalEntrance constructor.
     * @param repository A reference to a repository object.
     * @param totalPassengers Total number of passengers per flight.
     */
    public DepartureTerminalEntrance(Repository repository, int totalPassengers) {
        this.reentrantLock = new ReentrantLock(true);
        this.passengerCondition = this.reentrantLock.newCondition();
        this.allDone = -1;
        this.allSignaled = false;
        this.totalPassengers = totalPassengers;
        this.waitingPassengers = 0;
        this.repository = repository;
    }
     /**
     * Function that gets the number of passengers waiting for the last one to arrive at their final destination inside the airport.
     * @return the number of passengers waiting for the last one to arrive at their final destination inside the airport.
     */
    public int getWaitingPassengers() {
        int tmpWaitingPassengers = 0;
        this.reentrantLock.lock();
        try {
            tmpWaitingPassengers = this.waitingPassengers;
        } catch (Exception e) {
            System.out.println("DTE: getWaitingPassengers: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
        return tmpWaitingPassengers;
    }
    /**
     * Function that signals every waiting passenger.
     */
    public void signalWaitingPassengers() {
        this.reentrantLock.lock();
        try {
            this.allSignaled = true;
            this.passengerCondition.signalAll();
        } catch (Exception e) {
            System.out.println("DTE: signalWaitingPassengers: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Function that sets the reference to a ArrivalTerminalExit object.
     * @param ate A reference to an ArrivalTerminalExit object.
     */
    public void setAte(ArrivalTerminalExit ate) {
        this.ate = ate;
    }
    /**
     * Function that allows for a transition to a new flight (new plane landing simulation).
     */
    public void prepareForNextFlight() {
        this.allDone = -1;
        this.allSignaled = false;
        this.waitingPassengers = 0;
    }
    /**
     * The passenger checks if they is the last to make it to their destination inside the airport. If so, they signal all others to leave together. Otherwise, they wait for the last one to signal them.
     * @param pid The passenger's ID.
     */
    @Override
    public void prepareNextLeg(int pid) {
        this.repository.passengerPreparingNextLeg(pid);

        this.reentrantLock.lock();
        try {
            this.waitingPassengers++;
        } catch (Exception e) {
            System.out.println("DTE1: goHome: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }

        int ateWaitingPassengers = this.ate.getWaitingPassengers();
        if(this.waitingPassengers + ateWaitingPassengers == this.totalPassengers) this.allSignaled = true;

        this.reentrantLock.lock();
        try {
            if(this.allSignaled) this.passengerCondition.signalAll();
            else this.passengerCondition.await();
        } catch (Exception e) {
            System.out.println("DTE2: goHome: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
        if(this.allSignaled) this.ate.signalWaitingPassengers();
    }
}