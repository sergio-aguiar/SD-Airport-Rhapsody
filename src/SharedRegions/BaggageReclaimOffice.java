package SharedRegions;

import Interfaces.BROPassenger;

import java.util.concurrent.locks.ReentrantLock;
/**Baggage Reclaim Office: Where passengers report missing bags.
 * Used by PASSENGER.
 * @author sergiaguiar
 * @author marcomacedo
 */
public class BaggageReclaimOffice implements BROPassenger {
    /**
     * The class's ReentrantLock instance.
     */
    private final ReentrantLock reentrantLock;
    /**
     * The class's Repository instance.
     */
    private final Repository repository;
    /**
     * BaggageReclaimOffice constructor.
     * @param repository repository.
     */
    public BaggageReclaimOffice(Repository repository) {
        this.reentrantLock = new ReentrantLock(true);
        this.repository = repository;
    }
    /**
     * Passengers report how many missing bags they have.
     * @param pid The passenger's ID.
     * @param missingBags The passenger's amount of missing bags.
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
