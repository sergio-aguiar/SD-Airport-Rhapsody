package SharedRegions;

import Interfaces.DTEPassenger;

public class DepartureTerminalEntrance implements DTEPassenger {

    private final Repository repository;

    public DepartureTerminalEntrance(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void prepareNextLeg() {

    }
}
