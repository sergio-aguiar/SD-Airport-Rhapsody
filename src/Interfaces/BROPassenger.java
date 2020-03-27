package Interfaces;
/**
 * Passanger Baggage Reclaim Office Interface.
 * 
 * @author sergioaguiar
 * @author marcomacedo
 */
public interface BROPassenger {
    /**
     * Passager reports a missing bad.
     * @param pid Passanger id.
     */
    public void reportMissingBags(int pid);
}
