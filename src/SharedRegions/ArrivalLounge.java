package SharedRegions;

import Entities.PassengerThread;
import Entities.PorterThread;
import Exceptions.PorterDoneException;
import Interfaces.ALPassenger;
import Interfaces.ALPorter;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ArrivalLounge implements ALPassenger, ALPorter {

    private final ReentrantLock reentrantLock;
    private final Condition passengerCondition;
    private final Condition porterCondition;

    private int passengersThatReached;

    private final Repository repository;

    public ArrivalLounge(Repository repository) {
        this.reentrantLock = new ReentrantLock();
        this.passengerCondition = this.reentrantLock.newCondition();
        this.porterCondition = this.reentrantLock.newCondition();
        this.passengersThatReached = 0;
        this.repository = repository;
    }

    @Override
    public void takeARest(int pid) {
        this.reentrantLock.lock();
        try {
            if(this.repository.isPorterDone())
                throw new PorterDoneException("The porter's services are no longer needed.");
            this.porterCondition.await();
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
            this.repository.setPorterState(pid, PorterThread.PorterStates.WAITING_FOR_A_PLANE_TO_LAND);
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }

    @Override
    public String whatShouldIDo(int pid) {
        this.passengersThatReached++;
        if(this.passengersThatReached == this.repository.getNumberOfPassengers()) this.porterCondition.signal();
        return this.repository.getPassengerSituation(pid).toString();
    }

    @Override
    public String tryToCollectABag(int pid) {
        this.repository.setPorterState(pid, PorterThread.PorterStates.AT_THE_PLANES_HOLD);
        if(this.repository.getNumberOfLuggageLeftToCollect() != 0) return this.repository.getBag();
        return null;
    }

    @Override
    public boolean goCollectABag(int pid) {
        boolean success = false;
        this.reentrantLock.lock();
        try {
            if(this.repository.isPassengerBagInBaggageCollectionPoint(pid)) success = true;
            this.repository.setPassengerState(pid, PassengerThread.PassengerStates.AT_THE_LUGGAGE_COLLECTION_POINT);
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
        return success;
    }
}
