package SharedRegions;

import Interfaces.BCPPassenger;
import Interfaces.BCPPorter;

public class BaggageCollectionPoint implements BCPPassenger, BCPPorter {

    private Repository repositoryMonitor;
    @Override
    public void tryToCollectABag() {

    }

    @Override
    public void noMoreBagsToCollect() {

    }

    @Override
    public void carryItToAppropriateStore() {

    }

    @Override
    public void goCollectABag() {

    }
    
     /**
     * @param repositoryMonitor 
     */
    public void setRepositoryMonitor(Repository repositoryMonitor) {
        this.repositoryMonitor = repositoryMonitor;
    }
}
