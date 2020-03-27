package SharedRegions;

import Entities.PassengerThread;
import Interfaces.DTEPassenger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/** Departure Terminal Entrance shared region: used by Passenger
 * @author sergiaguiar
 * @author marcomacedo
 */
public class DepartureTerminalEntrance implements DTEPassenger {

    private final ReentrantLock reentrantLock;
    private final Condition passengerCondition;
    /**
     * Number of total passengers.
     */
    private final int totalPassengers;
    /**
     * Number of waiting passengers.
     */
    private int waitingPassengers;
    /**
     * Instance of Arrival Terminal Exit.
     */
    private final ArrivalTerminalExit ate;
    /**
     * Instance of Repository.
     */
    private final Repository repository;
    
    /**
     * Departure Terminal Entrance constructor.
     * @param repository repository
     * @param ate arrival terminal exit.
     * @param totalPassengers number of total passengers.
     */
    public DepartureTerminalEntrance(Repository repository, ArrivalTerminalExit ate, int totalPassengers) {
        this.reentrantLock = new ReentrantLock(true);
        this.passengerCondition = this.reentrantLock.newCondition();
        this.totalPassengers = totalPassengers;
        this.waitingPassengers = 0;
        this.ate = ate;
        this.repository = repository;
    }
    /**
     * Get the number of waiting passengers.
     * @return number of waiting passengers.
     */
    public int getWaitingPassengers() {
        return this.waitingPassengers;
    }
    /**
     * Sginal the waiting passsengers.
     */
    public void signalWaitingPassengers() {
        this.passengerCondition.signalAll();
    }
    /**
     * Passenger method: The passenger prepares the nex leg of the journey.
     * @param pid passenger id.
     */
    @Override
    public void prepareNextLeg(int pid) {
        this.reentrantLock.lock();
        try {
            this.repository.passengerPreparingNextLeg(pid);
            this.waitingPassengers++;
            if(this.waitingPassengers + this.ate.getWaitingPassengers() == this.totalPassengers) {
                this.ate.signalWaitingPassengers();
                this.passengerCondition.signalAll();
                this.waitingPassengers = 0;
            }
            else this.passengerCondition.await();
        } catch (Exception e) {
            System.out.print(e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
}
