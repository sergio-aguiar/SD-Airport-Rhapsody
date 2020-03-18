package SharedRegions;

import Interfaces.ATTQBusDriver;
import Interfaces.ATTQPassenger;

import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ArrivalTerminalTransferQuay implements ATTQPassenger, ATTQBusDriver {

    private final ReentrantLock reentrantLock;
    private final Condition busQueueCondition;
    private final Condition busDriverCondition;

    private int queuedPassengers;
    private int passengersInBus;
    private int passengersTaken;

    private final int busSeatNumber;
    private final int passengerTotal;
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
        this.tmpBusSeats = new String[busSeatNumber];
        Arrays.fill(this.tmpBusSeats, "-");
    }

    private void getIntoBus(int pid) {
        this.tmpBusSeats[this.passengersInBus] = String.valueOf(pid);
        this.passengersInBus++;
        this.queuedPassengers--;
    }

    private void clearBus() {
        this.passengersInBus = 0;
        Arrays.fill(this.tmpBusSeats, "-");
    }

    @Override
    public void announcingBusBoarding() {
        this.reentrantLock.lock();
        try {
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
    public void goToArrivalTerminal() {

    }

    @Override
    public boolean hasDaysWorkEnded() {
        return false;
    }

    @Override
    public void parkTheBus() {
        this.reentrantLock.lock();
        try {
            this.queuedPassengers = 0;
            this.passengersTaken = 0;
            this.clearBus();
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }

    @Override
    public void takeABus() {
        this.reentrantLock.lock();
        try {
            this.queuedPassengers++;
            if(this.queuedPassengers == this.busSeatNumber) this.busDriverCondition.signal();
            this.busQueueCondition.await();
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
}
