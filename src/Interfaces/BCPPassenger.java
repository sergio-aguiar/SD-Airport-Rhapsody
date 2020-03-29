package Interfaces;
/**
 * Passenger's Baggage Collection Point Interface.
 * 
 * @author sergioaguiar
 * @author marcomacedo
 */
public interface BCPPassenger {
    /**
     * The Passenger tries to collect a bag from the conveyor.
     * @param pid The passenger's ID.
     * @return true if passenger succeeded in collecting a bag and false otherwise.
     */
    public boolean goCollectABag(int pid);
}

