/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.airport.rhapsody;

import java.util.Arrays;

/**
 *
 * @author marcomacedo
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    
    private static final int K      = 1;
    private static final int N      = 6;
    private static final int M      = 2;
    private static final int T      = 3;
 

    
    private static final String[][] pStat = new String[N][K];
   
    /**
     * Array bidimensional of Bags with passengers and landings.
     */
    private static final int[][] nBags = new int[N][K];
    /**
     * Array bidimensional of Bags missing with bags and landings.
     */
  
    private static final int [][] nBagsMissing = new int[N][K];;
    private static int nT;
    private static final int[] nTotal = new int[K];
    private static String[] passengerSituations = new String[K];
    
    public static void main(String[] args) {
        // TODO code application logic here
        piecesOfLuggage();
        passengerSituations = bi_to_uni(pStat);
        System.out.println(Arrays.toString(passengerSituations));       
    }
    private static String [] bi_to_uni (String[][] S)
    {
            String[] ret = new String[S.length * S[0].length];
            int pos = 0;
             for(int id=0;id<S.length; id++) {
                  for(int flight=0;flight<S[id].length; flight++) {
                        ret[pos++] = S[id][flight];
                 }
            }
            return ret;
    }
    private static void piecesOfLuggage(){
        for (int land = 0; land < K; land++){
            nT = 0;
            for(int nP = 0; nP < N; nP++){
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
            //System.out.println(Arrays.deepToString(pStat));
        }
    }

    
}
