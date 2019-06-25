import TSPFileparser.Parser;
import heuristics.LinKernighan;
import utils.utils;

import java.io.File;
import java.util.Arrays;

public class Main {


    public static void main(String[] args) throws Exception {

        String path = "./src/data/berlin52.tsp";
        double adj_matrix[][];
        Parser in = new Parser(new File(path));
        double adj_matrix2[][] = in.getAdjacencyMatrix();
        System.out.println("Starting...");

        // Create the instance of the problem
//        System.out.println(Arrays.deepToString(adj_matrix2));

        String data_path = "./src/data/data4.txt";
        adj_matrix = utils.readFromFile(data_path);

        LinKernighan lk = new LinKernighan(adj_matrix2);

        // Time keeping
        long start;
        start = System.currentTimeMillis();

        // Shpw the results even if shutdown
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.printf("The solution took: %dms\n", System.currentTimeMillis() - start);
                System.out.println("The solution is: ");
                System.out.println(lk);
                System.out.println(Arrays.toString(lk.getTour()));
            }
        });

        lk.runAlgorithm();
    }
}

