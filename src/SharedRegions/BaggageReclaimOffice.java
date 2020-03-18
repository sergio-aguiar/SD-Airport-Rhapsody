package SharedRegions;

import Interfaces.BROPassenger;

public class BaggageReclaimOffice implements BROPassenger {

    private Repository repositoryMonitor;
    @Override
    public void reportMissingBags() {

    }
     /**
     * @param repositoryMonitor 
     */
    public void setRepositoryMonitor(Repository repositoryMonitor) {
        this.repositoryMonitor = repositoryMonitor;
    }
}
