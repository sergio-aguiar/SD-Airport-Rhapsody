package SharedRegions;

import Interfaces.DTEPassenger;

public class DepartureTerminalEntrance implements DTEPassenger {

    private Repository repositoryMonitor;
    @Override
    public void prepareNextLeg() {

    }
     /**
     * 
     * @param repositoryMonitor 
     */
    public void setRepositoryMonitor(Repository repositoryMonitor) {
        this.repositoryMonitor = repositoryMonitor;
    }
}
