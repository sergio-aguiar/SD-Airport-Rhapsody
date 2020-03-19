package Entities;

import Extras.Bag;
import Interfaces.ALPorter;
import Interfaces.BCPPorter;
import Interfaces.TSAPorter;
import SharedRegions.*;

import javax.sound.sampled.Port;

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
        super.run();
    }
}
