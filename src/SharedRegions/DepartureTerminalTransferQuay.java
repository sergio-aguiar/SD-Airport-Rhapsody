package SharedRegions;

import Entities.BusDriverThread;
import Entities.PassengerThread;
import Interfaces.DTTQBusDriver;
import Interfaces.DTTQPassenger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DepartureTerminalTransferQuay implements DTTQPassenger, DTTQBusDriver {

    private final ReentrantLock reentrantLock;
    private final Condition passengerCondition;
    private final Condition busDriverCondition;

    private int passengersThatArrived;
    private int passengersThatLeftTheBus;

    private final Repository repository;

    public DepartureTerminalTransferQuay(Repository repository) {
        this.reentrantLock = new ReentrantLock(true);
        this.passengerCondition = this.reentrantLock.newCondition();
        this.busDriverCondition = this.reentrantLock.newCondition();
        this.passengersThatArrived = 0;
        this.passengersThatLeftTheBus = 0;
        this.repository = repository;
    }

    @Override
    public void goToArrivalTerminal(int bid) {
        this.reentrantLock.lock();
        try {
            this.passengersThatArrived = 0;
            this.passengersThatLeftTheBus = 0;
            this.repository.busDriverGoingToArrivalTerminal();
        } catch(Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }

    @Override
    public void leaveTheBus(int pid, int seat) {
        this.reentrantLock.lock();
        try {
            this.passengerCondition.await();
            this.passengersThatLeftTheBus++;
            if(this.passengersThatLeftTheBus == this.passengersThatArrived) this.busDriverCondition.signal();
            this.repository.passengerLeavingTheBus(pid, seat);
        } catch(Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }

    @Override
    public void parkTheBusAndLetPassOff(int bid, int passengersThatArrived) {
        this.reentrantLock.lock();
        try {
            this.repository.busDriverParkingTheBusAndLettingPassengersOff();
            this.passengersThatArrived = passengersThatArrived;
            this.passengerCondition.signalAll();
            this.busDriverCondition.await();
        } catch(Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
}
