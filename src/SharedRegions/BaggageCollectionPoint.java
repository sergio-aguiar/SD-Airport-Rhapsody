package SharedRegions;

import Entities.PassengerThread;
import Entities.PorterThread;
import Extras.Bag;
import Interfaces.BCPPassenger;
import Interfaces.BCPPorter;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BaggageCollectionPoint implements BCPPassenger, BCPPorter {

    private final ReentrantLock reentrantLock;
    private final Condition[] passengerLuggageConditions;

    private final Repository repository;

    public BaggageCollectionPoint(Repository repository) {
        this.reentrantLock = new ReentrantLock(true);
        this.repository = repository;
        this.passengerLuggageConditions = new Condition[this.repository.getNumberOfPassengers()];
        for(Condition c : this.passengerLuggageConditions) c = this.reentrantLock.newCondition();
    }

    @Override
    public boolean goCollectABag(int pid) {
        boolean success = false;
        this.reentrantLock.lock();
        try {
            this.passengerLuggageConditions[pid].await();
            if(this.repository.isPassengerBagInBaggageCollectionPoint(pid)) {
                this.repository.claimBagFromBaggageCollectionPoint(pid);
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
    public void carryItToAppropriateStore(int pid) {
        this.reentrantLock.lock();
        try {
            this.repository.setPorterState(pid, PorterThread.PorterStates.AT_THE_LUGGAGE_BELT_CONVEYOR);
            int passengerID = this.repository.getPorterHeldBagID();
            this.repository.carryBagToBaggageCollectionPoint();
            this.passengerLuggageConditions[passengerID].signal();
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
