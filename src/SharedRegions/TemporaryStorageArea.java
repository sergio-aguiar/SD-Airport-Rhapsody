package SharedRegions;

import Entities.PorterThread;
import Interfaces.TSAPorter;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
/**
 *  Temporary storage area shared region: used by the Porter.
 * @author sergiaguiar
 * @author marcomacedo
 */
public class TemporaryStorageArea implements TSAPorter {

    private final ReentrantLock reentrantLock;
    
    /**
     * Array List with the bags in the temprary storage area.
     */
    private final ArrayList<Integer> tsaBags;
    
    /**
     * Instance of repository.
     */
    private final Repository repository;
    
    /**
     * Temporary storage area constructor.
     * @param repository repository.
     */
    public TemporaryStorageArea(Repository repository) {
        this.reentrantLock = new ReentrantLock(true);
        this.tsaBags = new ArrayList<>();
        this.repository = repository;
    }
    
    /**
     * removes all the bags from the temporary storage area.
     */
    public void nextFlight() {
        this.tsaBags.clear();
    }
    
    /**
     * Porter method: The porter carry the bags to the appropriate store.
     * @param pid Poter id.
     * @param bagID  bag id.
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
