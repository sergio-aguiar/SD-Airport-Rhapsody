package SharedRegions;

import Entities.PassengerThread;
import Interfaces.ATEPassenger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ArrivalTerminalExit implements ATEPassenger {

    private final ReentrantLock reentrantLock;
    private final Condition passengerCondition;

    private final int totalPassengers;
    private int waitingPassengers;

    private final DepartureTerminalEntrance dte;
    private final Repository repository;

    public ArrivalTerminalExit(Repository repository, DepartureTerminalEntrance dte, int totalPassengers) {
        this.reentrantLock = new ReentrantLock(true);
        this.passengerCondition = this.reentrantLock.newCondition();
        this.totalPassengers = totalPassengers;
        this.waitingPassengers = 0;
        this.dte = dte;
        this.repository = repository;
    }

    public int getWaitingPassengers() {
        return this.waitingPassengers;
    }

    public void signalWaitingPassengers() {
        this.passengerCondition.signalAll();
    }

    @Override
    public void goHome(int pid) {
        this.reentrantLock.lock();
        try {
            this.repository.setPassengerState(pid, PassengerThread.PassengerStates.EXITING_THE_ARRIVAL_TERMINAL);
            this.waitingPassengers++;
            if(this.waitingPassengers + this.dte.getWaitingPassengers() == this.totalPassengers) {
                this.dte.signalWaitingPassengers();
                this.passengerCondition.signalAll();
            }
            else this.passengerCondition.await();
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
}
