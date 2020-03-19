package SharedRegions;

import Entities.PassengerThread;
import Entities.PorterThread;
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
    public void tryToCollectABag(int pid) {
        this.reentrantLock.lock();
        try {
            this.repository.setPorterState(pid, PorterThread.PorterStates.AT_THE_PLANES_HOLD);
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }

    @Override
    public PassengerThread.PassengerAndBagSituations whatShouldIDo(int pid) {
        this.passengersThatReached++;
        if(this.passengersThatReached == this.repository.getNumberOfPassengers()) this.porterCondition.signal();
        return this.repository.getPassengerSituation(pid);
    }
}
