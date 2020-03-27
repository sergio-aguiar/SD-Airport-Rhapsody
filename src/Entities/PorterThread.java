package Entities;

import Exceptions.PorterDoneException;
import Interfaces.ALPorter;
import Interfaces.BCPPorter;
import Interfaces.TSAPorter;

/**
 * Porter Thread: implements the life-cycle of the Porter.
 * @author sergioaguiar
 * @author marcomacedo
 */
public class PorterThread extends Thread {
    /**
     * Enumerate with the Porter states.
     */
    public enum PorterStates {
        WAITING_FOR_A_PLANE_TO_LAND("wptl"),
        AT_THE_PLANES_HOLD("atph"),
        AT_THE_LUGGAGE_BELT_CONVEYOR("alcb"),
        AT_THE_STOREROOM("atsr");

        String description;

        PorterStates(String description) {
            this.description = description;
        }
    }
    /**
     * Porter id.
     */
    private final int pid;
    /**
     * Instance of Porter the Arrival Lounge interface.
     */
    private final ALPorter alPorter;
    /**
     * Instance of the Porter Baggage Collection Point interface.
     */
    private final BCPPorter bcpPorter;
    /**
     * Instance of the Porter Temporary Storage Area interface.
     */
    private final TSAPorter tsaPorter;
    /**
     * 
     * @param pid Porter id.
     * @param al Porter Arrival Lounge interface.
     * @param bcp Porter Baggage Collection Point interface.
     * @param tsa Porter Temporary Storage Area.
     */
    public PorterThread(int pid, ALPorter al, BCPPorter bcp, TSAPorter tsa) {
        this.pid = pid;
        this.alPorter = al;
        this.bcpPorter = bcp;
        this.tsaPorter = tsa;
    }
    /**
     * Implements the life cycle of the Porter.
     */
    @Override
    public void run() {
        while(!this.alPorter.takeARest(this.pid)) {
            String bagSituation = this.alPorter.tryToCollectABag(this.pid);
            while(bagSituation != null) {
                if(bagSituation.equals(PassengerThread.PassengerAndBagSituations.FDT.toString()))
                    this.bcpPorter.carryItToAppropriateStore(this.pid);
                else
                    this.tsaPorter.carryItToAppropriateStore(this.pid);
                bagSituation = this.alPorter.tryToCollectABag(this.pid);
            }
            this.alPorter.noMoreBagsToCollect(this.pid);
        }
    }
}
