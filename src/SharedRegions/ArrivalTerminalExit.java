package SharedRegions;

import Interfaces.ATEPassenger;

public class ArrivalTerminalExit implements ATEPassenger {

    private final Repository repository;

    public ArrivalTerminalExit(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void goHome() {

    }
}
