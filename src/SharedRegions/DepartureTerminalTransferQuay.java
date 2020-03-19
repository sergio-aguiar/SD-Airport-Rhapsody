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

    private void getOutOfBus(int pid) {
        this.repository.removeFromBusSeats(pid);
        this.passengersThatLeftTheBus++;
        if(this.passengersThatLeftTheBus == this.passengersThatArrived) this.busDriverCondition.signal();
    }

    @Override
    public void goToArrivalTerminal(int bid) {
        this.reentrantLock.lock();
        try {
            this.repository.setBusDriverState(bid, BusDriverThread.BusDriverStates.DRIVING_BACKWARD);
        } catch(Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }

    @Override
    public void leaveTheBus(int pid) {
        this.reentrantLock.lock();
        try {
            this.passengerCondition.await();
            this.getOutOfBus(pid);
            this.repository.setPassengerState(pid, PassengerThread.PassengerStates.AT_THE_DEPARTURE_TRANSFER_TERMINAL);
        } catch(Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }

    @Override
    public void parkTheBusAndLetPassOff(int bid) {
        this.reentrantLock.lock();
        try {
            this.repository.setBusDriverState(bid, BusDriverThread.BusDriverStates.PARKING_AT_THE_DEPARTURE_TERMINAL);
            this.passengersThatArrived = this.repository.numberOfPassengersInBus();
            this.passengerCondition.signalAll();
            this.busDriverCondition.await();
        } catch(Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
}
