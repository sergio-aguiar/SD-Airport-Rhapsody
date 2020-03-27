package SharedRegions;

import Entities.PorterThread;
import Interfaces.BCPPassenger;
import Interfaces.BCPPorter;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**Baggage Colection Point: Used by passenger for and Porter.
 * @author sergiaguiar
 * @author marcomacedo
 */
public class BaggageCollectionPoint implements BCPPassenger, BCPPorter {

    private final ReentrantLock reentrantLock;
    private final Condition[] passengerLuggageConditions;
    
    /**
     * Arraylist of bags int he colection point.
     */
    private final ArrayList<Integer> bcpBags;
    /**
     * Instance of the repository.
     */
    private final Repository repository;
    
    /**
     * Baggage Collection Point constructor.
     * @param repository repository.
     * @param totalPassengers number of total passengers.
     */
    public BaggageCollectionPoint(Repository repository, int totalPassengers) {
        this.reentrantLock = new ReentrantLock(true);
        this.passengerLuggageConditions = new Condition[totalPassengers];
        for(Condition c : this.passengerLuggageConditions) c = this.reentrantLock.newCondition();
        this.bcpBags = new ArrayList<>();
        this.repository = repository;
    }
    
    /**
     * Passenger bag is int the collection point?
     * @param pid passenger id.
     * @return true if bag in the collection point, false otherwise.
     */
    private boolean isPassengerBagInCollectionPoint(int pid) {
        for(Integer bag : this.bcpBags) if(bag == pid) return true;
        return false;
    }
    /**
     * The passenger claims a bag from the collection point.
     * @param pid passenger id.
     */
    private void claimBagFromBaggageCollectionPoint(int pid) {
        for(Integer bag : this.bcpBags)
            if(bag == pid) {
                this.bcpBags.remove(bag);
                break;
            }
    }
    
    /**
     * Passenger method: the passenger go collect a bag from the collection point.
     * @param pid passenger id.
     * @return true if succes, false otherwise.
     */
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
    
    /**
     * Porter method: The porter carries the Bag to the appropriate store.
     * @param pid Porter id.
     * @param bagID Bag id.
     */
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
    
    /**
     * Porter method: The porter has no more bags to collect.
     * @param pid Porter id.
     */
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
