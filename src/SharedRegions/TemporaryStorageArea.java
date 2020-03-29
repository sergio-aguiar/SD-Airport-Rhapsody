package SharedRegions;

import Interfaces.TSAPorter;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
/**
 * Temporary Storage Area: Where the porter takes bags currently in transit.
 * Used by PORTER.
 * @author sergiaguiar
 * @author marcomacedo
 */
public class TemporaryStorageArea implements TSAPorter {
    /**
     * The class's ReentrantLock instance.
     */
    private final ReentrantLock reentrantLock;
    /**
     * ArrayList that contains all the bags currently in temporary storage.
     */
    private final ArrayList<Integer> tsaBags;
    /**
     * The class's Repository instance.
     */
    private final Repository repository;
    /**
     * TemporaryStorageArea constructor.
     * @param repository A reference to a repository object.
     */
    public TemporaryStorageArea(Repository repository) {
        this.reentrantLock = new ReentrantLock(true);
        this.tsaBags = new ArrayList<>();
        this.repository = repository;
    }
    /**
     * Function that allows for a transition to a new flight (new plane landing simulation).
     */
    public void prepareForNextFlight() {
        this.tsaBags.clear();
    }
    /**
     * The Porter carries their held bag to the Temporary Storage Area.
     * @param pid The porter's ID.
     * @param bagID The porter's held bag's owner's ID.
     */
    @Override
    public void carryItToAppropriateStore(int pid, int bagID) {
        this.reentrantLock.lock();
        try {
            this.repository.porterCarryBagToTemporaryStorageArea();
            this.tsaBags.add(bagID);
        } catch (Exception e) {
            System.out.println("TSA: carryItToAppropriateStore: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
}
