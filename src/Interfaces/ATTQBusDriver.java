package Interfaces;

public interface ATTQBusDriver {
    public boolean hasDaysWorkEnded();
    public void announcingBusBoarding();
    public void goToDepartureTerminal(int bid);
    public void parkTheBus(int bid);
}
