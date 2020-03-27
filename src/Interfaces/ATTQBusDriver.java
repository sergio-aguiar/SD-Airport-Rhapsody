package Interfaces;

public interface ATTQBusDriver {
    public boolean hasDaysWorkEnded();
    public void announcingBusBoarding();
    public int goToDepartureTerminal(int bid);
    public void parkTheBus(int bid);
}
