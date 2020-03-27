package SharedRegions;

import Entities.PorterThread;
import Interfaces.TSAPorter;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class TemporaryStorageArea implements TSAPorter {

    private final ReentrantLock reentrantLock;

    private final ArrayList<Integer> tsaBags;

    private final Repository repository;

    public TemporaryStorageArea(Repository repository) {
        this.reentrantLock = new ReentrantLock(true);
        this.tsaBags = new ArrayList<>();
        this.repository = repository;
    }

    @Override
    public void carryItToAppropriateStore(int pid, int bagID) {
        this.reentrantLock.lock();
        try {
            this.repository.setPorterState(pid, PorterThread.PorterStates.AT_THE_STOREROOM);
            this.tsaBags.add(bagID);
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
}
