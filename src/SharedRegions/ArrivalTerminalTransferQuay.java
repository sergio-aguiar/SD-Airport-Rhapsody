package SharedRegions;

import Interfaces.ATTQBusDriver;
import Interfaces.ATTQPassenger;

import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ArrivalTerminalTransferQuay implements ATTQPassenger, ATTQBusDriver {
    private Repository repositoryMonitor;
    private final ReentrantLock reentrantLock;
    private final Condition busQueueCondition;
    private final Condition busDriverCondition;

    private int queuedPassengers;
    private int passengersInBus;
    private int passengersTaken;

    private final int busSeatNumber;
    private final int passengerTotal;
    private String[] tmpWaitingQueue;
    private final String[] tmpBusSeats;

    public ArrivalTerminalTransferQuay(int passengerTotal, int busSeatNumber){
        this.reentrantLock = new ReentrantLock(true);
        this.busQueueCondition = reentrantLock.newCondition();
        this.busDriverCondition = reentrantLock.newCondition();
        this.queuedPassengers = 0;
        this.passengersInBus = 0;
        this.passengersTaken = 0;
        this.busSeatNumber = busSeatNumber;
        this.passengerTotal = passengerTotal;
        this.tmpWaitingQueue = new String[passengerTotal];
        this.tmpBusSeats = new String[busSeatNumber];
        Arrays.fill(this.tmpWaitingQueue, "-");
        Arrays.fill(this.tmpBusSeats, "-");
    }

    private void getIntoQueue(int pid) {
        this.tmpWaitingQueue[this.queuedPassengers] = String.valueOf(pid);
        this.queuedPassengers++;
    }

    private void getOutOfQueue(int pid) {
        String[] tmpQueue = new String[this.tmpWaitingQueue.length];
        boolean found = false;
        for(int i = 0; i < this.tmpWaitingQueue.length; i++) {
            if(this.tmpWaitingQueue[i].equals(String.valueOf(pid))) found = true;
            else {
                if(found) tmpQueue[i - 1] = this.tmpWaitingQueue[i];
                else tmpQueue[i] = this.tmpWaitingQueue[i];
            }
        }
        tmpQueue[this.tmpWaitingQueue.length - 1] = "-";
        this.tmpWaitingQueue = Arrays.copyOf(tmpQueue, this.passengerTotal);
        this.queuedPassengers--;
    }

    private void clearQueue() {
        this.queuedPassengers = 0;
        Arrays.fill(this.tmpWaitingQueue, "-");
    }

    private void getIntoBus(int pid) {
        this.tmpBusSeats[this.passengersInBus] = String.valueOf(pid);
        this.passengersInBus++;
    }

    private void clearBus() {
        this.passengersInBus = 0;
        Arrays.fill(this.tmpBusSeats, "-");
    }

    @Override
    public void announcingBusBoarding() {
        this.reentrantLock.lock();
        try {
            this.busDriverCondition.await();
            for(int i = 0; i < this.busSeatNumber; i++) {
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
            if(this.passengersInBus == this.busSeatNumber
                    || this.passengerTotal - this.passengersTaken == 0) {
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
    public void parkTheBus() {
        this.reentrantLock.lock();
        try {
            this.passengersTaken = 0;
            this.clearQueue();
            this.clearBus();
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
            this.busQueueCondition.await();
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }

    @Override
    public String[] goToDepartureTerminal() {
        return this.tmpBusSeats;
    }

    /**
     *
     * @param repositoryMonitor
     */
    public void setRepositoryMonitor(Repository repositoryMonitor) {
        this.repositoryMonitor = repositoryMonitor;
    }
}
