package Entities;

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
     * Bag Data.
     */
    private String[] bagData;
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
        this.bagData = new String[2];
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
            this.bagData = this.alPorter.tryToCollectABag(this.pid).split(",");
            while(this.bagData[0] != null && this.bagData[1] != null) {
                if(this.bagData[1].equals(PassengerThread.PassengerAndBagSituations.FDT.toString()))
                    this.bcpPorter.carryItToAppropriateStore(this.pid, Integer.parseInt(this.bagData[0]));
                else
                    this.tsaPorter.carryItToAppropriateStore(this.pid, Integer.parseInt(this.bagData[0]));
                this.bagData = this.alPorter.tryToCollectABag(this.pid).split(",");
            }
            this.bcpPorter.noMoreBagsToCollect(this.pid);
        }
    }
}
