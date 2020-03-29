package SharedRegions;

import Interfaces.DTTQBusDriver;
import Interfaces.DTTQPassenger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
/**
 * Departure Terminal Transfer Quay: Where the bus driver takes the passengers on his bus and where the passengers go to be able to reach the Departure Terminal Entrance.
 * Used by PASSENGER and BUS DRIVER.
 * @author sergiaguiar
 * @author marcomacedo
 */
public class DepartureTerminalTransferQuay implements DTTQPassenger, DTTQBusDriver {
    /**
     * The class's ReentrantLock instance.
     */
    private final ReentrantLock reentrantLock;
    /**
     * The Condition instance where the passengers wait for the bus driver to signal that they have made it to the Departure Terminal Transfer Quay.
     */
    private final Condition passengerCondition;
    /**
     * The Condition instance where the bus driver waits for every passenger to leave the bus.
     */
    private final Condition busDriverCondition;
    /**
     * Total number of passengers that arrived on the bus.
     */
    private int passengersThatArrived;
    /**
     * Total number of passengers that have left the bus.
     */
    private int passengersThatLeftTheBus;
    /**
     * Attribute that states whether the passengers can leave the bus or not.
     */
    private boolean canLeaveTheBus;
    /**
     * The class's Repository instance.
     */
    private final Repository repository;
    /**
     * DepartureTerminalTransferQuay constructor.
     * @param repository A reference to a repository object.
     */
    public DepartureTerminalTransferQuay(Repository repository) {
        this.reentrantLock = new ReentrantLock(true);
        this.passengerCondition = this.reentrantLock.newCondition();
        this.busDriverCondition = this.reentrantLock.newCondition();
        this.passengersThatArrived = 0;
        this.passengersThatLeftTheBus = 0;
        this.canLeaveTheBus = false;
        this.repository = repository;
    }
    /**
     * Function that allows for a transition to a new flight (new plane landing simulation).
     */
    public void prepareForNextFLight() {
        this.passengersThatArrived = 0;
        this.passengersThatLeftTheBus = 0;
        this.canLeaveTheBus = false;
    }
    /**
     * The bus driver drives towards the Arrival Terminal Transfer Quay.
     * @param bid The bus driver's ID.
     */
    @Override
    public void goToArrivalTerminal(int bid) {
        this.reentrantLock.lock();
        try {
            this.passengersThatArrived = 0;
            this.passengersThatLeftTheBus = 0;
            this.repository.busDriverGoingToArrivalTerminal();
        } catch(Exception e) {
            System.out.println("DTTQ: goToArrivalTerminal: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * The passenger leaves the bus and signals the bus driver if he's the last one to do so.
     * @param pid The passenger's ID.
     */
    @Override
    public void leaveTheBus(int pid, int seat) {
        this.reentrantLock.lock();
        try {
            if(!this.canLeaveTheBus) this.passengerCondition.await();
            this.passengersThatLeftTheBus++;
            if(this.passengersThatLeftTheBus == this.passengersThatArrived) this.busDriverCondition.signal();
            this.repository.passengerLeavingTheBus(pid, seat);
        } catch(Exception e) {
            System.out.println("DTTQ: leaveTheBus: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * The bus driver parks the Bus and let's teh passengers off.
     * @param bid The bus driver's ID.
     * @param passengersThatArrived The number of passengers that arrived aboard the bus.
     */
    @Override
    public void parkTheBusAndLetPassOff(int bid, int passengersThatArrived) {
        this.reentrantLock.lock();
        try {
            this.repository.busDriverParkingTheBusAndLettingPassengersOff();
            this.passengersThatArrived = passengersThatArrived;
            this.passengerCondition.signalAll();
            this.canLeaveTheBus = true;
            if(this.passengersThatLeftTheBus < this.passengersThatArrived) this.busDriverCondition.await();
        } catch(Exception e) {
            System.out.println("DTTQ: parkTheBusAndLetPassOff: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
}
