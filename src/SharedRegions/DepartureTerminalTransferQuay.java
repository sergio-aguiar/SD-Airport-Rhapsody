package SharedRegions;

import Interfaces.DTTQBusDriver;
import Interfaces.DTTQPassenger;

public class DepartureTerminalTransferQuay implements DTTQPassenger, DTTQBusDriver {
    
    private Repository repositoryMonitor;
    @Override
    public void goToDepartureTerminal() {

    }

    @Override
    public void leaveTheBus() {

    }

    @Override
    public void parkTheBusAndLetPassOff() {

    }
     /**
     * 
     * @param repositoryMonitor 
     */
    public void setRepositoryMonitor(Repository repositoryMonitor) {
        this.repositoryMonitor = repositoryMonitor;
    }
}
