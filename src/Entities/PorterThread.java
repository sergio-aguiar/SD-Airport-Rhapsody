package Entities;

import SharedRegions.*;

import javax.sound.sampled.Port;

public class PorterThread extends Thread {

    private enum PorterStates {
        WAITING_FOR_A_PLANE_TO_LAND("wptl"),
        AT_THE_PLANES_HOLD("atph"),
        AT_THE_LUGGAGE_BELT_CONVEYOR("alcb"),
        AT_THE_STOREROOM("atsr");

        String description;

        PorterStates(String description) {
            this.description = description;
        }
    }

    private PorterStates state;
    private int luggageOnConveyorBelt;
    private int luggageOnStoreRoom;

    private final ArrivalLounge arrivalLounge;
    private final BaggageCollectionPoint baggageCollectionPoint;
    private final TemporaryStorageArea temporaryStorageArea;

    public PorterThread(ArrivalLounge al, BaggageCollectionPoint bcp, TemporaryStorageArea tsa) {
        this.state = PorterStates.WAITING_FOR_A_PLANE_TO_LAND;

        this.luggageOnConveyorBelt = 0;
        this.luggageOnStoreRoom = 0;

        this.arrivalLounge = al;
        this.baggageCollectionPoint = bcp;
        this.temporaryStorageArea = tsa;
    }
}
