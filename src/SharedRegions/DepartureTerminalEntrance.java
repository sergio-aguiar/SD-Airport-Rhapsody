package SharedRegions;

import Entities.PassengerThread;
import Interfaces.DTEPassenger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DepartureTerminalEntrance implements DTEPassenger {

    private final ReentrantLock reentrantLock;
    private final Condition passengerCondition;

    private final Repository repository;

    public DepartureTerminalEntrance(Repository repository) {
        this.reentrantLock = new ReentrantLock(true);
        this.passengerCondition = this.reentrantLock.newCondition();
        this.repository = repository;
    }

    @Override
    public void prepareNextLeg(int pid) {
        this.reentrantLock.lock();
        try {
            this.repository.setPassengerState(pid, PassengerThread.PassengerStates.ENTERING_THE_DEPARTURE_TERMINAL);
            this.repository.addFlightPassengerDone();
            if(this.repository.getTrtPassengersDone() == this.repository.getTrtPassengers())
                this.passengerCondition.signalAll();
            else this.passengerCondition.await();
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
}
