package Interfaces;
/**
 * Passenger's Baggage Reclaim Office Interface.
 * 
 * @author sergioaguiar
 * @author marcomacedo
 */
public interface BROPassenger {
    /**
     * Passengers report how many missing bags they have.
     * @param pid The passenger's ID.
     * @param missingBags The passenger's amount of missing bags.
     */
    public void reportMissingBags(int pid, int missingBags);
}
