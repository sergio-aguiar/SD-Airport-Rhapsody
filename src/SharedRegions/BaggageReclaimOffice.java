package SharedRegions;

import Entities.PassengerThread;
import Interfaces.BROPassenger;

import java.util.concurrent.locks.ReentrantLock;

/**Baggage Reclaim Office shared region: Used by passenger for reclaim missing bags.
 * @author sergiaguiar
 * @author marcomacedo
 */
public class BaggageReclaimOffice implements BROPassenger {

    private final ReentrantLock reentrantLock;
    
    /**
     * Instance of the repository.
     */
    private final Repository repository;
    
    /**
     * Baggage Reclaim Office constructor.
     * @param repository repository.
     */
    public BaggageReclaimOffice(Repository repository) {
        this.reentrantLock = new ReentrantLock(true);
        this.repository = repository;
    }
    
    /**
     * Passenger method: The passengers reports a missing bad.
     * @param pid Passenger id.
     * @param missingBags number missing bags.
     */
    @Override
    public void reportMissingBags(int pid, int missingBags) {
        this.reentrantLock.lock();
        try {
            this.repository.passengerReportingMissingBags(pid, missingBags);
        } catch (Exception e) {
            System.out.println("BRO: reportMissingBags: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
}
