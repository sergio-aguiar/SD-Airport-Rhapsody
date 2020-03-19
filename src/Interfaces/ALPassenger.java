package Interfaces;

import Entities.PassengerThread;

public interface ALPassenger {
    public PassengerThread.PassengerAndBagSituations whatShouldIDo(int pid);
}
