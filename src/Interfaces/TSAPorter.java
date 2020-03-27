package Interfaces;
/**
 * Porter Temporary Storare Area Interface.
 * 
 * @author sergioaguiar
 * @author marcomacedo
 */
public interface TSAPorter {
	/**
     * The Porter carry it to the appropriate store.
     * @param pid Porter id.
     * @param bagID Bag Id.
     */
    public void carryItToAppropriateStore(int pid, int bagID);
}
