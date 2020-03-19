package SharedRegions;

import Interfaces.TSAPorter;

public class TemporaryStorageArea implements TSAPorter {

    private final Repository repository;

    public TemporaryStorageArea(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void carryItToAppropriateStore() {

    }

    @Override
    public void noMoreBagsToCollect() {

    }

    @Override
    public void tryToCollectABag() {

    }
}
