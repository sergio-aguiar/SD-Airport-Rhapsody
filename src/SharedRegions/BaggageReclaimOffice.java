package SharedRegions;

import Interfaces.BROPassenger;

public class BaggageReclaimOffice implements BROPassenger {

    private final Repository repository;

    public BaggageReclaimOffice(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void reportMissingBags() {

    }
}
