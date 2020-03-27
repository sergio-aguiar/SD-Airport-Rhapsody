package Interfaces;
/**
 * Passanger Baggage Collection Point Interface.
 * 
 * @author sergioaguiar
 * @author marcomacedo
 */
public interface BCPPassenger {
    /**
     * The Passanger goes collect a bag.
     * @param pid Passanger id.
     * @return true if passanger goes collect a bag or false otherwise.
     */
    public boolean goCollectABag(int pid);
}

