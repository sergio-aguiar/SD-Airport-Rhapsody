package SharedRegions;

import Interfaces.BCPPassenger;
import Interfaces.BCPPorter;

public class BaggageCollectionPoint implements BCPPassenger, BCPPorter {

    private final Repository repository;

    public BaggageCollectionPoint(Repository repository) {
        this.repository = repository;
    }

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
}
