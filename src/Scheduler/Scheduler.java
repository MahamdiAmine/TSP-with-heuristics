package Scheduler;

import TSPFileparser.Parser;
import heuristics.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Utils.utils;

public class Scheduler {

    private final String statisticsPath;
    private final String dataPath;
    private final String knownLowerBounds;
    private final Boolean debug;
    private long startingTime, elapsedTime;

    public Scheduler(String statsPath, String dataPath, String knownLowerBounds, boolean debug) {
        this.statisticsPath = statsPath;
        this.dataPath = dataPath;
        this.knownLowerBounds = knownLowerBounds;
        this.debug = debug;
    }

    public void Execute() throws Exception {
        File folder = new File(dataPath);
        File[] listOfFiles = folder.listFiles();
        PrintWriter out = new PrintWriter(new File(statisticsPath));
        List<Double> minMax;
        int dimension;
        double knownLowerBound;

        for (int i = 0; i < listOfFiles.length; i++) {
            String name = listOfFiles[i].getName();
            if (listOfFiles[i].isFile() && name.substring(name.length() - 3).equalsIgnoreCase("tsp")) {
                Parser in = new Parser(new File(String.valueOf(listOfFiles[i])));
                double[][] adjMatrixFromTspFile = in.getAdjacencyMatrix();
                dimension = adjMatrixFromTspFile[0].length;
                minMax = utils.minMax(adjMatrixFromTspFile);
                knownLowerBound = utils.getKnownLowerBound(knownLowerBounds, listOfFiles[i].getName());
                if (debug) {
                    System.out.println(Arrays.deepToString(adjMatrixFromTspFile));
                }
                out.println("*********************************************************************************");
                out.println("File N° " + (i + 1) + "      " + listOfFiles[i].getName());
                out.println("       Dimension : " + adjMatrixFromTspFile[0].length);
                out.println("       EDGE_WEIGHT_TYPE : EUC_2D");
                out.print("       Known Lower Bound : ");
                if (knownLowerBound == 0.0) {
                    out.println(" UNKNOWN");
                } else {
                    out.println(knownLowerBound);
                }
                out.println("       Min    : " + minMax.get(1 + dimension) + " , " + minMax.get(dimension + 2) + " , " + minMax.get(dimension + 3));
                out.println("       Max    : " + minMax.get(dimension * dimension - 1) + " , " + minMax.get(dimension * dimension - 2) + " , " + minMax.get(dimension * dimension - 3));
                out.println("       Avg    : " + utils.avg(adjMatrixFromTspFile));
                out.println("       Var    : " + utils.var(adjMatrixFromTspFile));
                out.println("       Median : " + minMax.get(Math.round(minMax.size() / 2)));
                out.println("--------------");
                /*the LinKernighan Heuristic*/
                {
                    LinKernighan lk = new LinKernighan(adjMatrixFromTspFile);
                    out.println("       [1] LinKernighan : ");
                    startingTime = System.currentTimeMillis();
                    lk.runAlgorithm();
                    elapsedTime = System.currentTimeMillis() - startingTime;
                    out.printf("        \t\ttime : %7dms         distance :%15f \n", elapsedTime, lk.getDistance());
                    if (debug) {
                        System.out.println(lk);
                    }
                }
                /* 2-OPT Heuristic*/
                {
                    ArrayList<Coords> cities = new ArrayList<>(Load.loadTSPLib1(String.valueOf(listOfFiles[i]))); //alter file name here.
                    ArrayList<Coords> nearestN;
                    ArrayList<Coords> result;
                    out.println("       [2] 2-OPT : ");
                    startingTime = System.currentTimeMillis();
                    double length;
                    nearestN = Neighbour.nearest1(cities);
                    result = TwoOpt.alternate1(nearestN, debug);
                    length = Length.routeLength1(nearestN);
                    Validator.validate1(nearestN, debug);
                    elapsedTime = System.currentTimeMillis() - startingTime;
                    out.printf("        \t\ttime : %7dms         distance :%15f\n", elapsedTime, length);
                    if (debug) {
                        int[] twoOPT_tour = new int[result.size()];
                        Arrays.fill(twoOPT_tour, 0);
                        for (int counter = 0; counter < result.size(); counter++) {
                            twoOPT_tour[counter] = result.get(counter).getId();
                        }
                        System.out.println(Arrays.toString(twoOPT_tour));
                    }
                }
                /* Random Heuristic*/
                {
                    out.println("       [3] Random Heuristic : ");
                    startingTime = System.currentTimeMillis();
                    Random randomHeuristic = new Random(adjMatrixFromTspFile);
                    elapsedTime = System.currentTimeMillis() - startingTime;
                    randomHeuristic.getRandomTour();
                    out.printf("         \t\ttime : %7dms         distance :%15f \n", elapsedTime, randomHeuristic.getDistance());
                    if (debug) {
                        System.out.println(randomHeuristic);
                    }

                }
                /* Nearest Neighbour Heuristic*/
                {
                    out.println("       [4] Nearest Neighbour Heuristic : ");
                    ArrayList<Coords> cities = new ArrayList<>(Load.loadTSPLib1(String.valueOf(listOfFiles[i]))); //alter file name here.
                    startingTime = System.currentTimeMillis();
                    Neighbour neighbour = new Neighbour(cities);
                    long nearestNeighbourHeuristicTime = System.currentTimeMillis() - startingTime;
                    out.printf("        \t\ttime : %7dms         distance :%15f \n", elapsedTime, Length.routeLength1(neighbour.getTour()));
                    if (debug) {
                        System.out.println(neighbour);
                    }
                }
            }
        }
        out.flush();
        out.close();

    }

}