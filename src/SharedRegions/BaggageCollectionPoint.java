package SharedRegions;

import Entities.PassengerThread;
import Entities.PorterThread;
import Extras.Bag;
import Interfaces.BCPPassenger;
import Interfaces.BCPPorter;

import java.util.concurrent.locks.ReentrantLock;

public class BaggageCollectionPoint implements BCPPassenger, BCPPorter {

    private final ReentrantLock reentrantLock;

    private final Repository repository;

    public BaggageCollectionPoint(Repository repository) {
        this.reentrantLock = new ReentrantLock(true);
        this.repository = repository;
    }

    @Override
    public boolean goCollectABag(int pid) {
        boolean success = false;
        this.reentrantLock.lock();
        try {
            if(this.repository.isPassengerBagInBaggageCollectionPoint(pid)) success = true;
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
        return success;
    }

    @Override
    public void carryItToAppropriateStore(int pid) {
        this.reentrantLock.lock();
        try {
            this.repository.setPorterState(pid, PorterThread.PorterStates.AT_THE_LUGGAGE_BELT_CONVEYOR);
            this.repository.carryBagToBaggageCollectionPoint();
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
}
