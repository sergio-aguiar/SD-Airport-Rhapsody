package SharedRegions;

import Entities.PassengerThread;
import Entities.PorterThread;
import Extras.Bag;
import Interfaces.BCPPassenger;
import Interfaces.BCPPorter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BaggageCollectionPoint implements BCPPassenger, BCPPorter {

    private final ReentrantLock reentrantLock;
    private final Condition[] passengerLuggageConditions;

    private final ArrayList<Integer> bcpBags;

    private final Repository repository;

    public BaggageCollectionPoint(Repository repository, int totalPassengers) {
        this.reentrantLock = new ReentrantLock(true);
        this.passengerLuggageConditions = new Condition[totalPassengers];
        for(Condition c : this.passengerLuggageConditions) c = this.reentrantLock.newCondition();
        this.bcpBags = new ArrayList<>();
        this.repository = repository;
    }

    private boolean isPassengerBagInCollectionPoint(int pid) {
        for(Integer bag : this.bcpBags) if(bag == pid) return true;
        return false;
    }

    private void claimBagFromBaggageCollectionPoint(int pid) {
        for(Integer bag : this.bcpBags)
            if(bag == pid) {
                this.bcpBags.remove(bag);
                break;
            }
    }

    @Override
    public boolean goCollectABag(int pid) {
        boolean success = false;
        this.reentrantLock.lock();
        try {
            this.passengerLuggageConditions[pid].await();
            if(this.isPassengerBagInCollectionPoint(pid)) {
                this.claimBagFromBaggageCollectionPoint(pid);
                success = true;
            }
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
        return success;
    }

    @Override
    public void carryItToAppropriateStore(int pid, int bagID) {
        this.reentrantLock.lock();
        try {
            this.repository.setPorterState(pid, PorterThread.PorterStates.AT_THE_LUGGAGE_BELT_CONVEYOR);
            this.bcpBags.add(bagID);
            this.passengerLuggageConditions[bagID].signal();
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }

    @Override
    public void noMoreBagsToCollect(int pid) {
        this.reentrantLock.lock();
        try {
            for(Condition c : this.passengerLuggageConditions) c.signal();
            this.repository.setPorterState(pid, PorterThread.PorterStates.WAITING_FOR_A_PLANE_TO_LAND);
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
}
