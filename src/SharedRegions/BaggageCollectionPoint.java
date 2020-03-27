package SharedRegions;

import Entities.PorterThread;
import Interfaces.BCPPassenger;
import Interfaces.BCPPorter;

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
                this.repository.passengerCollectingABag(pid);
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
            this.repository.porterCarryBagToBaggageCollectionPoint();
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
            this.repository.porterAnnouncingNoMoreBagsToCollect();
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
}
