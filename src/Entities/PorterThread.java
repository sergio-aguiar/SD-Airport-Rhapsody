package Entities;

import Interfaces.ALPorter;
import Interfaces.BCPPorter;
import Interfaces.TSAPorter;

/**
 * Porter Thread: execute's the Porter's life-cycle.
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
     * Current Bag's Data.
     */
    private String[] bagData;
    /**
     * Porter's ID.
     */
    private final int pid;
    /**
     * Instance of the Porter's the Arrival Lounge interface.
     */
    private final ALPorter alPorter;
    /**
     * Instance of the Porter's Baggage Collection Point interface.
     */
    private final BCPPorter bcpPorter;
    /**
     * Instance of the Porter's Temporary Storage Area interface.
     */
    private final TSAPorter tsaPorter;
    /**
     * 
     * @param pid Porter's ID.'
     * @param al Porter's Arrival Lounge interface.
     * @param bcp Porter's Baggage Collection Point interface.
     * @param tsa Porter's Temporary Storage Area interface.
     */
     public PorterThread(int pid, ALPorter al, BCPPorter bcp, TSAPorter tsa) {
        this.pid = pid;
        this.bagData = new String[2];
        this.alPorter = al;
        this.bcpPorter = bcp;
        this.tsaPorter = tsa;
    }
    /**
     * Executes the Porter's life-cycle.
     */
   @Override
    public void run() {
       System.out.println("PORTER STARTING!");
        while(!this.alPorter.takeARest(this.pid)) {
            System.out.println("PORTER TRYING TO COLLECT A BAG!");
            String tmpBag = this.alPorter.tryToCollectABag(this.pid);
            while(!tmpBag.equals("")) {
                this.bagData = tmpBag.split(",");
                System.out.println("PORTER DECIDING WHERE TO CARRY THE BAG TO!");
                if(this.bagData[1].equals(PassengerThread.PassengerAndBagSituations.FDT.toString())) {
                    System.out.println("PORTER CARRYING BAG TO BCP!");
                    this.bcpPorter.carryItToAppropriateStore(this.pid, Integer.parseInt(this.bagData[0]));
                }
                else {
                    System.out.println("PORTER CARRYING BAG TO TSA!");
                    this.tsaPorter.carryItToAppropriateStore(this.pid, Integer.parseInt(this.bagData[0]));
                }
                System.out.println("PORTER TRYING TO COLLECT A BAG!");
                tmpBag = this.alPorter.tryToCollectABag(this.pid);
            }
            System.out.println("PORTER ANNOUNCING NO MORE BAGS!");
            this.bcpPorter.noMoreBagsToCollect(this.pid);
        }
    }
}
