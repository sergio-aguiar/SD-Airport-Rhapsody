package SharedRegions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import Entities.*;
import SharedRegions.*;
        
public class Repository {
    
    private File logFile;
    
    private PassengerThread.PassengerStates[] psStates;
    private BusDriverThread.BusDriverStates[] bSates;
    private PorterThread.PorterStates[] ptStates;
    
    private PassengerThread.PassengerAndBagSituations[] bagSituation;
    
    private int numberOfLuggageAtThePlane;
    private int numberOfLuggageOnConveyor;
    private int numberOfLuggageAtTheStoreRoom;
   
    private int numberOfLuggagePassengerAtStart;
    private int numberOfLuggagePassengerPresentlyCollected;
    
    private int flightNumber;
    
    
    private BufferedWriter  writer;

    public Repository() throws IOException{
        
        this.numberOfLuggageAtThePlane = 0;
        this.numberOfLuggageAtTheStoreRoom = 0;
        this.numberOfLuggageOnConveyor = 0;
        this.numberOfLuggagePassengerAtStart = 0;
        this.numberOfLuggagePassengerPresentlyCollected = 0;
        this.flightNumber = 0;
        
        this.psStates = new PassengerThread.PassengerStates[6];
        this.bSates = new BusDriverThread.BusDriverStates[1];
        this.ptStates = new PorterThread.PorterStates[1];
        
        for(int i=0; i<psStates.length; i++) {psStates[i] = PassengerThread.PassengerStates.AT_THE_DISEMBARKING_ZONE;    }
        for(int i=0; i<bSates.length; i++)  {bSates[i]  = BusDriverThread.BusDriverStates.PARKING_AT_THE_ARRIVAL_TERMINAL;      }
        for(int i=0; i<ptStates.length; i++)  {ptStates[i]  = PorterThread.PorterStates.WAITING_FOR_A_PLANE_TO_LAND;  }
        
        // open data stream to log file
         logFile = new File("logFile_" + System.nanoTime() + ".txt");
         writer = new BufferedWriter(new FileWriter(logFile));

         // write static elements
         writer.write("              AIRPORT RHAPSODY - Description of the internal state of the problem\n");
          writer.write(" \n");
         writer.write("PLANE    PORTER                  DRIVER\n");
         writer.write("FN BN  Stat CB SR   Stat  Q1 Q2 Q3 Q4 Q5 Q6  S1 S2 S3\n");
         writer.write("                                                         PASSENGERS\n");
         writer.write("St1 Si1 NR1 NA1 St2 Si2 NR2 NA2 St3 Si3 NR3 NA3 St4 Si4 NR4 NA4 St5 Si5 NR5 NA5 St6 Si6 NR6 NA6 \n");

         
         log();
    }

    public boolean close(){
        
        try {
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    public void log(){
        
        String line = "";
        //Plane numer + number of luggages at the planes
        line += flightNumber + " " + numberOfLuggageAtThePlane + "   "; 
        // Porter states
        line += ptStates[0] + " ";
        
        try {
            writer.write((line + "\n"));
        } catch (IOException ex) {
            Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

