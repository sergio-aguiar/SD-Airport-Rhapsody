package SharedRegions;

import Interfaces.ALPassenger;
import Interfaces.ALPorter;

public class ArrivalLounge implements ALPassenger, ALPorter {
   
    private Repository repositoryMonitor;
    
    @Override
    public void takeARest() {

    }

    @Override
    public void noMoreBagsToCollect() {

    }

    @Override
    public void tryToCollectABag() {

    }

    @Override
    public void whatShouldIDo() {

    }
    
    /**
     * @param repositoryMonitor 
     */
    public void setRepositoryMonitor(Repository repositoryMonitor) {
        this.repositoryMonitor = repositoryMonitor;
    }
}
