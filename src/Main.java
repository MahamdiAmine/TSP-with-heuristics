import TSPFileparser.Parser;
import heuristics.LinKernighan;
import utils.utils;

import java.io.File;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws Exception {

        String path = "./src/data/berlin52.tsp";
        double adjMatrixFromFile[][];
        Parser in = new Parser(new File(path));
        double adjMatrixFromTspFile[][] = in.getAdjacencyMatrix();
        System.out.println("Starting...");

        System.out.println(Arrays.deepToString(adjMatrixFromTspFile));

        String data_path = "./src/data/data4.txt";
        adjMatrixFromFile = utils.readFromFile(data_path);

        LinKernighan lk = new LinKernighan(adjMatrixFromTspFile);

        // Time keeping
        long start;
        start = System.currentTimeMillis();

        // Show the results even if shutdown
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

