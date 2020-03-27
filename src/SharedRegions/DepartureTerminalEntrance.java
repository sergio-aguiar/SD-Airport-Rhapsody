package SharedRegions;

import Entities.PassengerThread;
import Interfaces.DTEPassenger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DepartureTerminalEntrance implements DTEPassenger {

    private final ReentrantLock reentrantLock;
    private final Condition passengerCondition;

    private final int totalPassengers;
    private int waitingPassengers;

    private final ArrivalTerminalExit ate;

    private final Repository repository;

    public DepartureTerminalEntrance(Repository repository, ArrivalTerminalExit ate, int totalPassengers) {
        this.reentrantLock = new ReentrantLock(true);
        this.passengerCondition = this.reentrantLock.newCondition();
        this.totalPassengers = totalPassengers;
        this.waitingPassengers = 0;
        this.ate = ate;
        this.repository = repository;
    }

    public int getWaitingPassengers() {
        return this.waitingPassengers;
    }

    public void signalWaitingPassengers() {
        this.passengerCondition.signalAll();
    }

    @Override
    public void prepareNextLeg(int pid) {
        this.reentrantLock.lock();
        try {
            this.repository.passengerPreparingNextLeg(pid);
            this.waitingPassengers++;
            if(this.waitingPassengers + this.ate.getWaitingPassengers() == this.totalPassengers) {
                this.ate.signalWaitingPassengers();
                this.passengerCondition.signalAll();
                this.waitingPassengers = 0;
            }
            else this.passengerCondition.await();
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
}
