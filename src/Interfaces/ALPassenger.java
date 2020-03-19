package Interfaces;

import Entities.PassengerThread;

public interface ALPassenger {
    public String whatShouldIDo(int pid);
    public boolean goCollectABag(int pid);
}
