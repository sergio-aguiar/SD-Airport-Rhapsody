package SharedRegions;

import Entities.BusDriverThread;
import Entities.PassengerThread;
import Interfaces.DTTQBusDriver;
import Interfaces.DTTQPassenger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
/**
 * Departure Terminal Tranfer Quay. Used by passenger and bus driver.
 * @author sergiaguiar
 * @author marcomacedo
 */
public class DepartureTerminalTransferQuay implements DTTQPassenger, DTTQBusDriver {

    private final ReentrantLock reentrantLock;
    private final Condition passengerCondition;
    private final Condition busDriverCondition;
    
    /**
     * Number of passengers that arrived.
     */
    private int passengersThatArrived;
    /**
     * Number of passengers that lef the bus.
     */
    private int passengersThatLeftTheBus;

    private boolean canLeaveTheBus;

    /**
     * Instance of the repository.
     */
    private final Repository repository;
    
    /**
     * Departure Terminal Tranfer Quay constructor.
     * @param repository repository.
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
     * Bus Driver method: The bus driver go to arrival terminal.
     * @param bid bus driver id.
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
     * Passenger method: The Passenger leaves the bus.
     * @param pid passenger id.
     * @param seat  Bus seat.
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
     * Bus driver method: the bus driver parks the bus and let the passegers off.
     * @param bid Bus Driver id.
     * @param passengersThatArrived number of passengers that arrived.
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
