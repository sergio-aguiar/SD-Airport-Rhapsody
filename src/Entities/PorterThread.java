package Entities;

import Interfaces.ALPorter;
import Interfaces.BCPPorter;
import Interfaces.TSAPorter;

public class PorterThread extends Thread {

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

    private final int pid;
    private String[] bagData;

    private final ALPorter alPorter;
    private final BCPPorter bcpPorter;
    private final TSAPorter tsaPorter;

    public PorterThread(int pid, ALPorter al, BCPPorter bcp, TSAPorter tsa) {
        this.pid = pid;
        this.bagData = new String[2];
        this.alPorter = al;
        this.bcpPorter = bcp;
        this.tsaPorter = tsa;
    }

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
