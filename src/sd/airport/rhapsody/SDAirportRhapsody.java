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
    private static final int NUMBER_OF_PLANE_LANDINDS      = 5;
    private static final int NUMBER_OF_PASSENGERS          = 6;
    private static final int NUMBER_OF_PIECES_OF_LUGGAGE   = 2;
    private static final int NUMBER_OF_BUS_SEATS           = 3;
    private static final int NUMBER_OF_BUS_DRIVERS         = 1;
    private static final int NUMBER_OF_PORTERS             = 1;
    
    private static final int flight_number                 = 0; 
    private static final int busSeatNumber                 = 0;
    private static final int numberOfPassengerLuggageAtStart   = 0;
   
    private static PassengerThread[] passengers;
    private static BusDriverThread[] busDrivers;
    private static PorterThread[] porters;
    
    private static String[] passengerSituations;

    private static ArrivalLounge arrivalLoungeMonitor;
    private static ArrivalTerminalExit arrivalTerminalExitMonitor;
    private static ArrivalTerminalTransferQuay arrivalTerminalTransferQuayMonitor;
    private static BaggageCollectionPoint baggageCollectionPointMonitor;
    private static  BaggageReclaimOffice baggageReclaimOfficeMonitor;
    private static DepartureTerminalEntrance departureTerminalEntranceMonitor;
    private static DepartureTerminalTransferQuay departureTerminalTransferQuayMonitor;
    private static Repository repositoryMonitor;
    private static TemporaryStorageArea temporaryStorageAreaMonitor;
    
    
    public static void main(String[] args) {
        // TODO code application logic here
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
    
    