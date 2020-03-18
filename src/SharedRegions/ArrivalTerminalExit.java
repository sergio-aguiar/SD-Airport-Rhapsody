package SharedRegions;

import Interfaces.ATEPassenger;

public class ArrivalTerminalExit implements ATEPassenger {
    
    private Repository repositoryMonitor;
    @Override
    public void goHome() {

    }
    
    public void setRepositoryMonitor(Repository repositoryMonitor) {
        this.repositoryMonitor = repositoryMonitor;
    }
}
