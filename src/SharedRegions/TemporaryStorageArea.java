package SharedRegions;

import Interfaces.TSAPorter;

public class TemporaryStorageArea implements TSAPorter {
    
    private Repository repositoryMonitor;
    @Override
    public void carryItToAppropriateStore() {

    }

    @Override
    public void noMoreBagsToCollect() {

    }

    @Override
    public void tryToCollectABag() {

    }
    
    /**
     * @param repositoryMonitor 
     */
    public void setRepositoryMonitor(Repository repositoryMonitor) {
        this.repositoryMonitor = repositoryMonitor;
    }
}
