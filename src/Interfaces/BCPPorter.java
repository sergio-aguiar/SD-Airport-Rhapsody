package Interfaces;

import Extras.Bag;

public interface BCPPorter {
    public void carryItToAppropriateStore(int pid, int bagID);
    public void noMoreBagsToCollect(int pid);
}
