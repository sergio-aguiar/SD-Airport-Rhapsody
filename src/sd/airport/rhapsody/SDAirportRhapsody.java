/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.airport.rhapsody;

import Entities.*;
import Extras.Bag;
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
import java.util.Stack;
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
   
    
    private static final int K      = 2;
    private static final int N      = 3;
    private static final int M      = 2;
    private static final int T      = 3;
 
    private static final int NUMBER_OF_BUS_DRIVERS         = 1;
    private static final int NUMBER_OF_PORTERS             = 1;
    
    private static final int flightNumber                 = 0; 
    private static final int busSeatNumber                 = 0;
    private static final int numberOfPassengerLuggageAtStart   = 0;
   
    private static PassengerThread[] passengers;
    private static BusDriverThread[] busDrivers;
    private static PorterThread[] porters;
    
    private static final String[][] pStat = new String[N][K];
    /**
     * Array bidimensional of Bags with passengers and landings.
     */
    private static final int[][] nBags = new int[N][K];
    /**
     * Array bidimensional of Bags missing with bags and landings.
     */
    private static final int [][] nBagsMissing = new int[N][K];
    private static int nT;
    private static final int[] nTotal = new int[K];
    private static PassengerThread.PassengerAndBagSituations[] passengerSituations;
    private static Bag[][] bag;
   
    private static final PassengerThread.PassengerStates[] passengerStates = new PassengerThread.PassengerStates[N];
    
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
            piecesOfLuggage();
   
            //System.out.println(Arrays.toString(passengerSituations));  
            
         
            passengers = new PassengerThread[N];
            busDrivers = new BusDriverThread[NUMBER_OF_BUS_DRIVERS];
            porters = new PorterThread[NUMBER_OF_PORTERS];
        
            // creating Logger
            repositoryMonitor = new Repository(flightNumber, numberOfPassengerLuggageAtStart,busSeatNumber, N, passengerSituations, nTotal);

            // creating monitors
            arrivalLoungeMonitor = new ArrivalLounge(repositoryMonitor, N, nTotal, bag);
            arrivalTerminalExitMonitor = new ArrivalTerminalExit(repositoryMonitor, departureTerminalEntranceMonitor,N);
            arrivalTerminalTransferQuayMonitor = new ArrivalTerminalTransferQuay(repositoryMonitor, N, T);

            baggageReclaimOfficeMonitor = new BaggageReclaimOffice(repositoryMonitor);
            baggageCollectionPointMonitor = new BaggageCollectionPoint(repositoryMonitor, N);

            departureTerminalEntranceMonitor = new DepartureTerminalEntrance(repositoryMonitor,arrivalTerminalExitMonitor, N);
            departureTerminalTransferQuayMonitor = new DepartureTerminalTransferQuay(repositoryMonitor);

            temporaryStorageAreaMonitor = new TemporaryStorageArea(repositoryMonitor);

            // starting entities
                //PASSENGERS
            for(int i=0; i<N; i++){
                    passengers[i] = new PassengerThread(i,numberOfPassengerLuggageAtStart,PassengerThread.PassengerAndBagSituations.TRT,(ALPassenger)arrivalLoungeMonitor, (ATEPassenger)arrivalTerminalExitMonitor, (ATTQPassenger)arrivalTerminalTransferQuayMonitor, (BCPPassenger)baggageCollectionPointMonitor,(DTEPassenger)departureTerminalEntranceMonitor,(DTTQPassenger)departureTerminalTransferQuayMonitor, (BROPassenger)baggageReclaimOfficeMonitor);
                }
                 //BUS DRIVER
            for(int i=0; i<NUMBER_OF_BUS_DRIVERS; i++){
                    busDrivers[i] = new BusDriverThread(i, (ATTQBusDriver)arrivalTerminalTransferQuayMonitor, (DTTQBusDriver)departureTerminalTransferQuayMonitor);
                }
                 //PORTER
            for(int i=0; i<NUMBER_OF_PORTERS; i++){
                    porters[i] = new PorterThread(i, (ALPorter)arrivalLoungeMonitor, baggageCollectionPointMonitor, temporaryStorageAreaMonitor);
                }

            for(int i=0; i<N; i++){passengers[i].start();}
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
    
    private static void piecesOfLuggage(){
        for (int land = 0; land < K; land++){
            nT = 0;
            for(int nP = 0; nP < N; nP++){
                
                Arrays.fill(passengerStates, PassengerThread.PassengerStates.AT_THE_DISEMBARKING_ZONE);
                
                // Passenger State
                if((Math.random() < 0.4))
                    pStat[nP][land] = "FDT";     
                else
                    pStat[nP][land] = "TRT";
                
                // Number Bags
                if (Math.random() < 0.5)
                    nBags[nP][land] = 2;
                else if(Math.random() < 0.5)
                     nBags[nP][land] = 1;
                else 
                     nBags[nP][land] = 0;
                
                if("TRT".equals(pStat[nP][land]) || nBags[nP][land] == 0)
                    nBagsMissing[nP][land] = nBags[nP][land];      // no missing bags
                else if (Math.random() < 0.5)
                     nBagsMissing[nP][land] = nBags[nP][land] - 1; //passenfer lost 1 bag
             
                nT += nBags[nP][land];
            }
        nTotal[land] = nT;                                          // number of bags per plane
       System.out.println(Arrays.deepToString(pStat));

        }
    }


    
    
}   
    
    