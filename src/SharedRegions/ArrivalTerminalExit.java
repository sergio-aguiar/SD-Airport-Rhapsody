package SharedRegions;

import Entities.PassengerThread;
import Entities.PorterThread;
import Interfaces.ATEPassenger;

import java.util.concurrent.locks.ReentrantLock;

public class ArrivalTerminalExit implements ATEPassenger {

    private final ReentrantLock reentrantLock;

    private final Repository repository;

    public ArrivalTerminalExit(Repository repository) {
        this.reentrantLock = new ReentrantLock(true);
        this.repository = repository;
    }

    @Override
    public void goHome(int pid) {
        this.reentrantLock.lock();
        try {
            this.repository.setPassengerState(pid, PassengerThread.PassengerStates.EXITING_THE_ARRIVAL_TERMINAL);
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
}
