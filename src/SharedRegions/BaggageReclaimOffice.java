package SharedRegions;

import Entities.PassengerThread;
import Interfaces.BROPassenger;

import java.util.concurrent.locks.ReentrantLock;

public class BaggageReclaimOffice implements BROPassenger {

    private final ReentrantLock reentrantLock;

    private final Repository repository;

    public BaggageReclaimOffice(Repository repository) {
        this.reentrantLock = new ReentrantLock(true);
        this.repository = repository;
    }

    @Override
    public void reportMissingBags(int pid) {
        this.reentrantLock.lock();
        try {
            this.repository.setPassengerState(pid, PassengerThread.PassengerStates.AT_THE_LUGGAGE_RECLAIM_OFFICE);
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
}
