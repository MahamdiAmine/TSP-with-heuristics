import Scheduler.Scheduler;

public class Main {

    public static void main(String[] args) throws Exception {
        String statsPath = "./statistics/stats.txt";
        String dataPath = "./dataWithBounds/";
        String knownLowerBoundsPath = "./dataWithBounds/knownLowerBounds";
        Scheduler scheduler = new Scheduler(statsPath, dataPath, knownLowerBoundsPath, false);
        scheduler.Execute();
    }
}