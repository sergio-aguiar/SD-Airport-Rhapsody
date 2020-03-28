package SharedRegions;

import Entities.BusDriverThread;
import Entities.PassengerThread;
import Interfaces.ATTQBusDriver;
import Interfaces.ATTQPassenger;

import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/** Arrival Terminal Transger Quay shared region
 * Used by Passenger and Bus Driver.
 * @author sergiaguiar
 * @author marcomacedo
 */
public class ArrivalTerminalTransferQuay implements ATTQPassenger, ATTQBusDriver {

    private final ReentrantLock reentrantLock;
    private final Condition busQueueCondition;
    private final Condition busDriverCondition;
    
     /**
     * Number of total passengers.
     */
    private final int totalPassengers;
    /**
     * Bus seat number.
     */
    private final int busSeatNumber;
    
    /**
     * Number of queued Passengers.
     */
    private int queuedPassengers;
    /**
     * Number of passengers in the bus.
     */
    private int passengersInBus;
    /**
     * Number of passengers signaled to board the bus.
     */
    private int passengersSignaled;

    /**
     * Array with the bus seat numbers.
     */
    private final String[] busSeats;
        
    /**
     * Array with the passengers waiting the bus.
     */
    private String[] busWaitingQueue;
    
    /**
     * Instace of the repository.
     */
    private final Repository repository;
    
    /**
     * Arrival Terminal Transfer Quay constructor.
     * @param repository repository.
     * @param totalPassengers Number of total passengers.
     * @param busSeatNumber Bus seat number.
     */
    public ArrivalTerminalTransferQuay(Repository repository, int totalPassengers, int busSeatNumber){
        this.reentrantLock = new ReentrantLock(true);
        this.busQueueCondition = this.reentrantLock.newCondition();
        this.busDriverCondition = this.reentrantLock.newCondition();
        this.totalPassengers = totalPassengers;
        this.busSeatNumber = busSeatNumber;
        this.queuedPassengers = 0;
        this.passengersInBus = 0;
        this.passengersSignaled = 0;
        this.busSeats = new String[busSeatNumber];
        this.busWaitingQueue = new String[totalPassengers];
        Arrays.fill(this.busSeats, "-");
        Arrays.fill(this.busWaitingQueue, "-");
        this.repository = repository;
    }
    
    /**
     * Add passengers to the waiting queue.
     * @param pid passenger id.
     * @param positionInQueue passenger position in the queue.
     */
    private void addToWaitingQueue(int pid, int positionInQueue) {
        this.busWaitingQueue[positionInQueue] = String.valueOf(pid);
    }
    
    /**
     * Removee passenger from the waiting queue.
     * @param pid passenger id.
     * @return number of passengers in the queue.
     */
    private int removeFromWaitingQueue(int pid) {
        String[] tmpQueue = new String[this.totalPassengers];
        int found = -1;
        for(int i = 0; i < this.totalPassengers; i++) {
            if(this.busWaitingQueue[i].equals(String.valueOf(pid))) found = i;
            else {
                if(found != -1) tmpQueue[i - 1] = this.busWaitingQueue[i];
                else tmpQueue[i] = this.busWaitingQueue[i];
            }
        }
        tmpQueue[this.totalPassengers - 1] = "-";
        this.busWaitingQueue = Arrays.copyOf(tmpQueue, this.totalPassengers);
        return found;
    }
    
    /**
     * Adding new seats to bus.
     * @param pid passenger id.
     * @param seatInBus seat in the bus.
     */
    private void addToBusSeats(int pid, int seatInBus) {
        this.busSeats[seatInBus] = String.valueOf(pid);
    }
    
    /**
     * Number of passengers in the bus.
     * @return Number of passengers in the bus.
     */
    private int numberOfPassengersInBus() {
        int passengerCount = 0;
        for(String seat : this.busSeats) if(!seat.equals("-")) passengerCount++;
        return passengerCount;
    }
    
    /**
     * The passenger get into queue.
     * @param pid passenger id.
     * @return decrease number of passengers the the queue.
     */
    private int getIntoQueue(int pid) {
        this.addToWaitingQueue(pid, queuedPassengers);
        this.queuedPassengers++;
        return this.queuedPassengers - 1;
    }
    
    /**
     * Passenger get out of the queue.
     * @param pid passenger id.
     * @return  position in the queue.
     */
    private int getOutOfQueue(int pid) {
        int queuePosition = this.removeFromWaitingQueue(pid);
        this.queuedPassengers--;
        return queuePosition;
    }
    
    /**
     * Passenger get into the bus.
     * @param pid passenger id.
     * @return decrease number of passengers int the bus.
     */
    private int getIntoBus(int pid) {
        this.addToBusSeats(pid, this.passengersInBus);
        this.passengersInBus++;
        return this.passengersInBus - 1;
    }
    
    /**
     * Bus driver method: the Bus driver announces the bus boarding. 
     *
     */
    @Override
    public void announcingBusBoarding() {
        this.reentrantLock.lock();
        try {
            do this.busDriverCondition.awaitNanos(100); while(this.queuedPassengers == 0);
            for(int i = 0; i < this.busSeatNumber && i < this.queuedPassengers; i++) {
                passengersSignaled++;
                this.busQueueCondition.signal();
            }
            if(this.passengersSignaled > 0) this.busDriverCondition.await();
        } catch (Exception e) {
            System.out.println("ATTQ: announcingBusBoarding: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    /**
     * Passenger method: the Passenger enters the bus.
     * @param pid Passenger id.
     * @return bus seat.
     */
    @Override
    public int enterTheBus(int pid) {
        int busSeat = -1;
        int queuePosition = -1;
        this.reentrantLock.lock();
        try {
            queuePosition = this.getOutOfQueue(pid);
            this.repository.passengerGettingOutOfTheWaitingQueue(queuePosition);
            busSeat = this.getIntoBus(pid);
            this.passengersSignaled--;
            this.repository.passengerEnteringTheBus(pid, this.passengersInBus - 1);
            if(this.passengersSignaled == 0)
                this.busDriverCondition.signal();
        } catch (Exception e) {
            System.out.println("ATTQ: enterTheBus: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
        return busSeat;
    }
    
    /**
     * Bus driver method: has days ended.
     * @return true if has days work ended or false otherwise
     */
    @Override
    public boolean hasDaysWorkEnded() {
        return false;
    }
    
    /**
     * Bus Driver method: The bus driver parks the bus.
     * @param bid Bus driver id.
     */
    @Override
    public void parkTheBus(int bid) {
        this.reentrantLock.lock();
        try {
            this.passengersInBus = 0;
            this.repository.busDriverParkingTheBus();
        } catch (Exception e) {
            System.out.print("ATTQ: parkTheBus: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    
    /**
     * Passenger method: the passenger takes the bus.
     * @param pid passenger id.
     */
    @Override
    public void takeABus(int pid) {
        int queuePosition = -1;
        this.reentrantLock.lock();
        try {
            queuePosition = this.getIntoQueue(pid);
            this.repository.passengerGettingIntoTheWaitingQueue(pid, queuePosition);
            if(this.queuedPassengers == this.busSeatNumber) this.busDriverCondition.signal();
            this.repository.passengerTakingABus(pid);
            this.busQueueCondition.await();
        } catch (Exception e) {
            System.out.print("ATTQ: takeABus: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
    }
    
    /**
     * Bus driver method: the bus driver goes to departure terminal.
     * @param bid Bus driver id.
     * @return number of bus passengers.
     */
    @Override
    public int goToDepartureTerminal(int bid) {
        int busPassengers = 0;
        this.reentrantLock.lock();
        try {
            this.repository.busDriverGoingToDepartureTerminal();
            busPassengers = this.numberOfPassengersInBus();
        } catch (Exception e) {
            System.out.println("ATTQ: goToDepartureTerminal: " + e.toString());
        } finally {
            this.reentrantLock.unlock();
        }
        return busPassengers;
    }
}
