package Entities;

import Exceptions.PorterDoneException;
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

    private final ALPorter alPorter;
    private final BCPPorter bcpPorter;
    private final TSAPorter tsaPorter;

    public PorterThread(int pid, ALPorter al, BCPPorter bcp, TSAPorter tsa) {
        this.pid = pid;
        this.alPorter = al;
        this.bcpPorter = bcp;
        this.tsaPorter = tsa;
    }

    @Override
    public void run() {
        while(true) {
            try {
                this.alPorter.takeARest(this.pid);
            //} catch (PorterDoneException e) {
            } catch (Exception e) {
                break;
            }
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
