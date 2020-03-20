package Interfaces;

import Entities.PassengerThread;

public interface ALPassenger {
    public String whatShouldIDo(int pid);
    public void goCollectABag(int pid);
}
