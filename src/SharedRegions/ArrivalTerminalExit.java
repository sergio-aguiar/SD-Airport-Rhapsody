package SharedRegions;

import Entities.PassengerThread;
import Entities.PorterThread;
import Interfaces.ATEPassenger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ArrivalTerminalExit implements ATEPassenger {

    private final ReentrantLock reentrantLock;
    private final Condition passengerCondition;

    private final Repository repository;

    public ArrivalTerminalExit(Repository repository) {
        this.reentrantLock = new ReentrantLock(true);
        this.passengerCondition = this.reentrantLock.newCondition();
        this.repository = repository;
    }

    @Override
    public void goHome(int pid) {
        this.reentrantLock.lock();
        try {
            this.repository.setPassengerState(pid, PassengerThread.PassengerStates.EXITING_THE_ARRIVAL_TERMINAL);
            this.repository.addFlightPassengerDone();
            if(this.repository.getFdtPassengersDone() == this.repository.getFdtPassengers())
                this.passengerCondition.signalAll();
            else this.passengerCondition.await();
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
}
