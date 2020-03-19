package SharedRegions;

import Interfaces.ALPassenger;
import Interfaces.ALPorter;

public class ArrivalLounge implements ALPassenger, ALPorter {

    private final Repository repository;

    public ArrivalLounge(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void takeARest() {

    }

    @Override
    public void noMoreBagsToCollect() {

    }

    @Override
    public void tryToCollectABag() {

    }

    @Override
    public void whatShouldIDo() {

    }
}
