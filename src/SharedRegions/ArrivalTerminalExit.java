package SharedRegions;

import Entities.PassengerThread;
import Interfaces.ATEPassenger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/** Arrival terminal exit shared region: where the passenger waits for fellow passengers travelling in the same plane to be ready to leave airport or to check next leg.
 * Used by passengers.
 * @author sergiaguiar
 * @author marcomacedo
 */
public class ArrivalTerminalExit implements ATEPassenger {

    private final ReentrantLock reentrantLock;
    private final Condition passengerCondition;
    
    /**
     * Number of total passengers.
     */
    private final int totalPassengers;
    /**
     * Number of wainting passenger in the arrival terminal.
     */
    private int waitingPassengers;
    
    /**
     * Instance of the Departure Terminal Entrance.
     */
    private DepartureTerminalEntrance dte;
    /**
     * Instave of the repository.
     */
    private final Repository repository;
    
    /**
     * Arrival terminal exit constructor.
     * @param repository repository.
     * @param totalPassengers number of total passengers.
     */
    public ArrivalTerminalExit(Repository repository, int totalPassengers) {
        this.reentrantLock = new ReentrantLock(true);
        this.passengerCondition = this.reentrantLock.newCondition();
        this.totalPassengers = totalPassengers;
        this.repository = repository;
    }

    public void setDte(DepartureTerminalEntrance dte) {
        this.dte = dte;
    }

    /**
     * Get the waiting passengers.
     * @return the number of waiting passengers.
     */
    public int getWaitingPassengers() {
        return this.waitingPassengers;
    }
    /**
     * Signal waiting passengers.
     */
    public void signalWaitingPassengers() {
        this.passengerCondition.signalAll();
    }



    /**
     * Passenger method: go home.
     * @param pid passenger id.
     */
    @Override
    public void goHome(int pid) {
        this.reentrantLock.lock();
        try {
            this.repository.passengerGoingHome(pid);
            this.waitingPassengers++;
            if(this.waitingPassengers + this.dte.getWaitingPassengers() == this.totalPassengers) {
                this.dte.signalWaitingPassengers();
                this.passengerCondition.signalAll();
                this.waitingPassengers = 0;
            }
            else this.passengerCondition.await();
        } catch (Exception e) {
            System.out.println("ATE: goHome: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
}
