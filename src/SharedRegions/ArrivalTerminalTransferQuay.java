package SharedRegions;

import Entities.BusDriverThread;
import Entities.PassengerThread;
import Interfaces.ATTQBusDriver;
import Interfaces.ATTQPassenger;

import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ArrivalTerminalTransferQuay implements ATTQPassenger, ATTQBusDriver {

    private final ReentrantLock reentrantLock;
    private final Condition busQueueCondition;
    private final Condition busDriverCondition;

    private final int totalPassengers;
    private final int busSeatNumber;

    private int queuedPassengers;
    private int passengersInBus;
    private int passengersSignaled;

    private String[] busSeats;
    private String[] busWaitingQueue;

    private final Repository repository;

    public ArrivalTerminalTransferQuay(Repository repository, int totalPassengers, int busSeatNumber){
        this.reentrantLock = new ReentrantLock(true);
        this.busQueueCondition = this.reentrantLock.newCondition();
        this.busDriverCondition = this.reentrantLock.newCondition();
        this.totalPassengers = totalPassengers;
        this.busSeatNumber = busSeatNumber;
        this.queuedPassengers = 0;
        this.passengersInBus = 0;
        this.passengersSignaled = 0;
        this.busSeats = new String[busSeatNumber];
        this.busWaitingQueue = new String[totalPassengers];
        Arrays.fill(this.busSeats, "-");
        Arrays.fill(this.busWaitingQueue, "-");
        this.repository = repository;
    }

    private void addToWaitingQueue(int pid, int positionInQueue) {
        this.busWaitingQueue[positionInQueue] = String.valueOf(pid);
    }

    private void removeFromWaitingQueue(int pid) {
        String[] tmpQueue = new String[this.totalPassengers];
        boolean found = false;
        for(int i = 0; i < this.totalPassengers; i++) {
            if(this.busWaitingQueue[i].equals(String.valueOf(pid))) found = true;
            else {
                if(found) tmpQueue[i - 1] = this.busWaitingQueue[i];
                else tmpQueue[i] = this.busWaitingQueue[i];
            }
        }
        tmpQueue[this.totalPassengers - 1] = "-";
        this.busWaitingQueue = Arrays.copyOf(tmpQueue, this.totalPassengers);
    }

    private void addToBusSeats(int pid, int seatInBus) {
        this.busSeats[seatInBus] = String.valueOf(pid);
    }

    private void removeFromBusSeats(int pid) {
        String[] tmpSeats = new String[this.busSeatNumber];
        boolean found = false;
        for(int i = 0; i < this.busSeatNumber; i++) {
            if(this.busSeats[i].equals(String.valueOf(pid))) found = true;
            else {
                if(found) tmpSeats[i - 1] = this.busSeats[i];
                else tmpSeats[i] = this.busSeats[i];
            }
        }
        tmpSeats[this.busSeatNumber - 1] = "-";
        this.busSeats = Arrays.copyOf(tmpSeats, this.busSeatNumber);
    }

    private int numberOfPassengersInBus() {
        int passengerCount = 0;
        for(String seat : this.busSeats) if(!seat.equals("-")) passengerCount++;
        return passengerCount;
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
            this.busDriverCondition.awaitNanos(10); //experiment with value
            for(int i = 0; i < this.busSeatNumber && i < this.queuedPassengers; i++) {
                passengersSignaled++;
                this.busQueueCondition.signal();
            }
            if(this.passengersSignaled > 0) this.busDriverCondition.await();
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
            this.getOutOfQueue(pid);
            this.getIntoBus(pid);
            this.passengersSignaled--;
            this.repository.setPassengerState(pid, PassengerThread.PassengerStates.TERMINAL_TRANSFER);
            if(this.passengersSignaled == 0)
                this.busDriverCondition.signal();
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
            if(this.queuedPassengers == this.busSeatNumber) this.busDriverCondition.signal();
            this.repository.setPassengerState(pid, PassengerThread.PassengerStates.AT_THE_ARRIVAL_TRANSFER_TERMINAL);
            this.busQueueCondition.await();
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }

    @Override
    public int goToDepartureTerminal(int bid) {
        int busPassengers = 0;
        this.reentrantLock.lock();
        try {
            this.repository.setBusDriverState(bid, BusDriverThread.BusDriverStates.DRIVING_FORWARD);
            busPassengers = this.numberOfPassengersInBus();
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
        return busPassengers;
    }
}
