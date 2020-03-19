/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.airport.rhapsody;

import Entities.*;
import Interfaces.ALPassenger;
import Interfaces.ALPorter;
import Interfaces.ATEPassenger;
import Interfaces.ATTQBusDriver;
import Interfaces.ATTQPassenger;
import Interfaces.BCPPassenger;
import Interfaces.BROPassenger;
import Interfaces.DTEPassenger;
import Interfaces.DTTQBusDriver;
import Interfaces.DTTQPassenger;
import SharedRegions.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author marcomacedo
 */
public class SDAirportRhapsody {

    /**
     * @param args the command line arguments
     */
    private final int NUMBER_OF_PLANE_LANDINDS      = 5;
    private final int NUMBER_OF_PASSENGERS          = 6;
    private final int NUMBER_OF_PIECES_OF_LUGGAGE   = 2;
    private final int NUMBER_OF_BUS_SEATS           = 3;
    private final int NUMBER_OF_BUS_DRIVERS         = 1;
    private final int NUMBER_OF_PORTERS             = 1;
    
    private final int flight_number                 = 0; 
    private final int busSeatNumber                 = 0;
    private int numberOfPassengerLuggageAtStart   = 0;
   
    private PassengerThread[] passengers;
    private BusDriverThread[] busDrivers;
    private PorterThread[] porters;
    
    private String[] passengerSituations;

    private ArrivalLounge arrivalLoungeMonitor;
    private ArrivalTerminalExit arrivalTerminalExitMonitor;
    private ArrivalTerminalTransferQuay arrivalTerminalTransferQuayMonitor;
    private BaggageCollectionPoint baggageCollectionPointMonitor;
    private BaggageReclaimOffice baggageReclaimOfficeMonitor;
    private DepartureTerminalEntrance departureTerminalEntranceMonitor;
    private DepartureTerminalTransferQuay departureTerminalTransferQuayMonitor;
    private Repository repositoryMonitor;
    private TemporaryStorageArea temporaryStorageAreaMonitor;
    
    
    public static void main(String[] args) {
        // TODO code application logic here
        SDAirportRhapsody airportRhapsody = new SDAirportRhapsody();
    }
    
    /**
     * Main program
     */
    public SDAirportRhapsody() {
           
        
        // creating entity arrays
        try{
            passengers = new PassengerThread[NUMBER_OF_PASSENGERS];
            busDrivers = new BusDriverThread[NUMBER_OF_BUS_DRIVERS];
            porters = new PorterThread[NUMBER_OF_PORTERS];
            
            // creating Logger
            repositoryMonitor = new Repository(numberOfPassengerLuggageAtStart,NUMBER_OF_PIECES_OF_LUGGAGE, flight_number,busSeatNumber, NUMBER_OF_PASSENGERS, passengerSituations);
            // creating monitors

            arrivalLoungeMonitor = new ArrivalLounge(repositoryMonitor);
            arrivalTerminalExitMonitor = new ArrivalTerminalExit(repositoryMonitor);
            arrivalTerminalTransferQuayMonitor = new ArrivalTerminalTransferQuay(repositoryMonitor);

            baggageReclaimOfficeMonitor = new BaggageReclaimOffice(repositoryMonitor);
            baggageCollectionPointMonitor = new BaggageCollectionPoint(repositoryMonitor);

            departureTerminalEntranceMonitor = new DepartureTerminalEntrance(repositoryMonitor);
            departureTerminalTransferQuayMonitor = new DepartureTerminalTransferQuay(repositoryMonitor);

            temporaryStorageAreaMonitor = new TemporaryStorageArea(repositoryMonitor);

            // starting entities
                //PASSENGERS
            for(int i=0; i<NUMBER_OF_PASSENGERS; i++){
                    passengers[i] = new PassengerThread(i,numberOfPassengerLuggageAtStart,(ALPassenger)arrivalLoungeMonitor, (ATEPassenger)arrivalTerminalExitMonitor, (ATTQPassenger)arrivalTerminalTransferQuayMonitor, (BCPPassenger)baggageCollectionPointMonitor,(DTEPassenger)departureTerminalEntranceMonitor,(DTTQPassenger)departureTerminalTransferQuayMonitor, (BROPassenger)baggageReclaimOfficeMonitor);
                }
                 //BUS DRIVER
            for(int i=0; i<NUMBER_OF_BUS_DRIVERS; i++){
                    busDrivers[i] = new BusDriverThread(i, (ATTQBusDriver)arrivalTerminalTransferQuayMonitor, (DTTQBusDriver)departureTerminalTransferQuayMonitor);
                }
                 //PORTER
            for(int i=0; i<NUMBER_OF_PORTERS; i++){
                    porters[i] = new PorterThread(i, (ALPorter)arrivalLoungeMonitor, baggageCollectionPointMonitor, temporaryStorageAreaMonitor);
                }

            for(int i=0; i<NUMBER_OF_PASSENGERS; i++){passengers[i].start();}
            for(int i=0; i<NUMBER_OF_BUS_DRIVERS; i++){busDrivers[i].start();}
            for(int i=0; i<NUMBER_OF_PORTERS; i++){porters[i].start();}

              // Waiting for entities to die
            for(PassengerThread p : passengers)   {p.join();}
            for(BusDriverThread b : busDrivers) {b.join();}
            for(PorterThread p : porters) {p.join();}

             

         } catch (InterruptedException | IOException ex) {
            Logger.getLogger(SDAirportRhapsody.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
}
