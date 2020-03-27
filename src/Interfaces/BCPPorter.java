package Interfaces;

import Extras.Bag;
/**
 * Porter Baggage Collection Point Interface.
 * 
 * @author sergioaguiar
 * @author marcomacedo
 */
public interface BCPPorter {
	/**
     * The Porter carry the bags to the appropriate Store.
     * @param pid Porter id.
     * @param bagID Bag id.
     */
    public void carryItToAppropriateStore(int pid, int bagID);
	/**
     * The porter has no more bags to collect.
     * @param pid Porter id.
     */
    public void noMoreBagsToCollect(int pid);
}
