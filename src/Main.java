import Scheduler.Scheduler;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 3) {
//            String dataPath = "./TSP_Instances/medium";
            String dataPath = "./data_KLB";
//            String dataPath = args[0];
            String knownLowerBoundsPath = args[1];
//            String statsPath = args[2];
            String statsPath = "statistics/small.txt";
            Scheduler scheduler = new Scheduler(statsPath, dataPath, knownLowerBoundsPath, false);
            scheduler.Execute();
        } else System.out.println("usage :\n java -jar scheduler.jar dataPath knownLowerBoundsPath statsPath");
    }

}