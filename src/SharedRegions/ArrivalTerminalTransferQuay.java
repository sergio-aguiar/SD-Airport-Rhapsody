package SharedRegions;

import Entities.BusDriverThread;
import Entities.PassengerThread;
import Interfaces.ATTQBusDriver;
import Interfaces.ATTQPassenger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ArrivalTerminalTransferQuay implements ATTQPassenger, ATTQBusDriver {

    private final ReentrantLock reentrantLock;
    private final Condition busQueueCondition;
    private final Condition busDriverCondition;

    private int queuedPassengers;
    private int passengersInBus;
    private int passengersTaken;

    private final Repository repository;

    public ArrivalTerminalTransferQuay(Repository repository){
        this.reentrantLock = new ReentrantLock(true);
        this.busQueueCondition = this.reentrantLock.newCondition();
        this.busDriverCondition = this.reentrantLock.newCondition();
        this.queuedPassengers = 0;
        this.passengersInBus = 0;
        this.passengersTaken = 0;
        this.repository = repository;
    }

    private void getIntoQueue(int pid) {
        this.repository.addToWaitingQueue(pid, queuedPassengers);
        this.queuedPassengers++;
    }

    private void getOutOfQueue(int pid) {
        this.repository.removeFromWaitingQueue(pid);
        this.queuedPassengers--;
    }

    private void getIntoBus(int pid) {
        this.repository.addToBusSeats(pid, this.passengersInBus);
        this.passengersInBus++;
    }

    @Override
    public void announcingBusBoarding() {
        this.reentrantLock.lock();
        try {
            this.busDriverCondition.await();
            for(int i = 0; i < this.repository.getBusSeatNumber()
                    || (this.repository.getNumberOfPassengers() - this.passengersTaken == this.queuedPassengers
                            && i < this.repository.getNumberOfPassengers() - this.passengersTaken); i++) {
                this.busQueueCondition.signal();
            }
            this.busDriverCondition.await();
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }

    @Override
    public void enterTheBus(int pid) {
        this.reentrantLock.lock();
        try {
            this.passengersInBus++;
            this.passengersTaken++;
            this.getOutOfQueue(pid);
            this.getIntoBus(pid);
            this.repository.setPassengerState(pid, PassengerThread.PassengerStates.TERMINAL_TRANSFER);
            if(this.passengersInBus == this.repository.getBusSeatNumber()
                    || this.repository.getNumberOfPassengers() - this.passengersTaken == 0) {
                this.busDriverCondition.signal();
            }
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }

    @Override
    public boolean hasDaysWorkEnded() {
        return false;
    }

    @Override
    public void parkTheBus(int bid) {
        this.reentrantLock.lock();
        try {
            this.passengersInBus = 0;
            this.repository.setBusDriverState(bid, BusDriverThread.BusDriverStates.PARKING_AT_THE_ARRIVAL_TERMINAL);
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }

    @Override
    public void takeABus(int pid) {
        this.reentrantLock.lock();
        try {
            this.getIntoQueue(pid);
            if(this.queuedPassengers == this.repository.getBusSeatNumber()) this.busDriverCondition.signal();
            this.repository.setPassengerState(pid, PassengerThread.PassengerStates.AT_THE_ARRIVAL_TRANSFER_TERMINAL);
            this.busQueueCondition.await();
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }

    @Override
    public void goToDepartureTerminal(int bid) {
        this.reentrantLock.lock();
        try {
            this.repository.setBusDriverState(bid, BusDriverThread.BusDriverStates.DRIVING_FORWARD);
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
}
