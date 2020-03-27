package Interfaces;
/**
 * Passenger Baggage Reclaim Office Interface.
 * 
 * @author sergioaguiar
 * @author marcomacedo
 */
public interface BROPassenger {
    /**
     * Passenger reports a missing bad.
     * @param pid Passenger's ID.
     * @param missingBags Passenger's amount of missing bags.
     */
    public void reportMissingBags(int pid, int missingBags);
}
