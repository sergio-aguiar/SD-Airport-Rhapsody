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

    private int removeFromWaitingQueue(int pid) {
        String[] tmpQueue = new String[this.totalPassengers];
        int found = -1;
        for(int i = 0; i < this.totalPassengers; i++) {
            if(this.busWaitingQueue[i].equals(String.valueOf(pid))) found = i;
            else {
                if(found != -1) tmpQueue[i - 1] = this.busWaitingQueue[i];
                else tmpQueue[i] = this.busWaitingQueue[i];
            }
        }
        tmpQueue[this.totalPassengers - 1] = "-";
        this.busWaitingQueue = Arrays.copyOf(tmpQueue, this.totalPassengers);
        return found;
    }

    private void addToBusSeats(int pid, int seatInBus) {
        this.busSeats[seatInBus] = String.valueOf(pid);
    }

    private int numberOfPassengersInBus() {
        int passengerCount = 0;
        for(String seat : this.busSeats) if(!seat.equals("-")) passengerCount++;
        return passengerCount;
    }

    private int getIntoQueue(int pid) {
        this.addToWaitingQueue(pid, queuedPassengers);
        this.queuedPassengers++;
        return this.queuedPassengers - 1;
    }

    private int getOutOfQueue(int pid) {
        int queuePosition = this.removeFromWaitingQueue(pid);
        this.queuedPassengers--;
        return queuePosition;
    }

    private int getIntoBus(int pid) {
        this.addToBusSeats(pid, this.passengersInBus);
        this.passengersInBus++;
        return this.passengersInBus - 1;
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
    public int enterTheBus(int pid) {
        int busSeat = -1;
        int queuePosition = -1;
        this.reentrantLock.lock();
        try {
            queuePosition = this.getOutOfQueue(pid);
            this.repository.passengerGettingOutOfTheWaitingQueue(queuePosition);
            busSeat = this.getIntoBus(pid);
            this.passengersSignaled--;
            this.repository.passengerEnteringTheBus(pid, this.passengersInBus - 1);
            if(this.passengersSignaled == 0)
                this.busDriverCondition.signal();
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
        return busSeat;
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
            this.repository.busDriverParkingTheBus();
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }

    @Override
    public void takeABus(int pid) {
        int queuePosition = -1;
        this.reentrantLock.lock();
        try {
            queuePosition = this.getIntoQueue(pid);
            this.repository.passengerGettingIntoTheWaitingQueue(pid, queuePosition);
            if(this.queuedPassengers == this.busSeatNumber) this.busDriverCondition.signal();
            this.repository.passengerTakingABus(pid);
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
            this.repository.busDriverGoingToDepartureTerminal();
            busPassengers = this.numberOfPassengersInBus();
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
        return busPassengers;
    }
}
