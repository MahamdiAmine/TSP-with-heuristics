package Scheduler;

import TSPFileparser.Parser;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import Utils.utils;
import heuristics.Insertion.Insertion;
import heuristics.LinKernighan.LinKernighan;
import heuristics.Neighbour.Neighbour;
import heuristics.Parsers.Coords;
import heuristics.Parsers.Length;
import heuristics.Parsers.Load;
import heuristics.Parsers.Validator;
import heuristics.Random.Random;
import heuristics.TwoOPT.TwoOpt;

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
        PrintWriter outForML = new PrintWriter(new File(statisticsPath + ".MLFormat"));
        List<Double> minMax;
        int dimension;
        double knownLowerBound;
        System.out.println("\n\n\t\t");
        Parser in;
        double[][] adjMatrixFromTspFile;
        Arrays.sort(listOfFiles);
        for (int i = 0; i < listOfFiles.length; i++) {
            String name = listOfFiles[i].getName();
            if (listOfFiles[i].isFile() && name.substring(name.length() - 3).equalsIgnoreCase("tsp")) {
                in = new Parser(new File(String.valueOf(listOfFiles[i])));
                adjMatrixFromTspFile = in.getAdjacencyMatrix();
                dimension = adjMatrixFromTspFile[0].length;
                minMax = utils.minMax(adjMatrixFromTspFile);
                knownLowerBound = utils.getKnownLowerBound(knownLowerBounds, listOfFiles[i].getName());
                if (debug) {
                    System.out.println(Arrays.deepToString(adjMatrixFromTspFile));
                }
                out.println("*********************************************************************************");
                out.println("File N° " + (i + 1) + "      " + listOfFiles[i].getName());
                out.println("       Dimension : " + adjMatrixFromTspFile[0].length);
                out.print("       Known Lower Bound : ");
                if (knownLowerBound == 0.0) {
                    out.println(" UNKNOWN");
                } else {
                    out.println(knownLowerBound);
                }
                out.println("       Min    : " + minMax.get(0) + " , " + minMax.get(1) + " , " + minMax.get(2));
                out.println("       Max    : " + minMax.get(dimension - 1) + " , " + minMax.get(dimension - 2) + " , " + minMax.get(dimension - 3));
                out.println("       Avg    : " + utils.avg(adjMatrixFromTspFile));
                out.println("       Var    : " + utils.var(adjMatrixFromTspFile));
                out.println("       Median : " + minMax.get(Math.round(minMax.size() / 2)));
                out.println("--------------");
                /*new format for Ml :*/
                outForML.print(dimension);
                outForML.print(" " + Utils.utils.avg(adjMatrixFromTspFile));
                outForML.print(" " + Utils.utils.var(adjMatrixFromTspFile));
                outForML.print(" " + minMax.get(0) + " " + minMax.get(dimension - 1) + " " + minMax.get(Math.round(minMax.size() / 2)));
                outForML.print(" " + (minMax.get(0) + minMax.get(1) + minMax.get(2)) / 3);
                outForML.print(" " + (minMax.get(dimension - 1) + minMax.get(dimension - 2) + minMax.get(dimension - 3)) / 3);
                /*the LinKernighan Heuristic*/
                {
                    LinKernighan lk = new LinKernighan(adjMatrixFromTspFile);
                    out.println("       [1] LinKernighan : ");
                    startingTime = System.currentTimeMillis();
                    lk.runAlgorithm();
                    elapsedTime = System.currentTimeMillis() - startingTime;
                    out.printf("        \t\ttime : %7dms         distance :%15f \n", elapsedTime, lk.getDistance());
                    // (30%/tempExecHeuristic1 + 70%/CostHeuristic1)/(1/temExexheuristic1 + 1/CostHeuristic1)
                    outForML.print(" " + (0.3 / elapsedTime + 0.7 * lk.getDistance()) / (1 / elapsedTime + 1 / lk.getDistance()));
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
                    outForML.print(" " + (0.3 / elapsedTime + 0.7 * length) / (1 / elapsedTime + 1 / length));
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
                    if (elapsedTime == 0) elapsedTime = 1;
                    randomHeuristic.getRandomTour();
                    out.printf("         \t\ttime : %7dms         distance :%15f \n", elapsedTime, randomHeuristic.getDistance());
                    outForML.print(" " + (0.3 / elapsedTime + 0.7 * randomHeuristic.getDistance()) / (1 / elapsedTime + 1 / randomHeuristic.getDistance()));
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
                    elapsedTime = System.currentTimeMillis() - startingTime;
                    if (elapsedTime == 0) elapsedTime = 1;
                    out.printf("        \t\ttime : %7dms         distance :%15f \n", elapsedTime, Length.routeLength1(neighbour.getTour()));
                    outForML.print(" " + (0.3 / elapsedTime + 0.7 * Length.routeLength1(neighbour.getTour())) / (1 / elapsedTime + 1 / Length.routeLength1(neighbour.getTour())));
                    if (debug) {
                        System.out.println(neighbour);
                    }
                }
                /* Insertion Heuristic*/
                {
                    out.println("       [5] Insertion Heuristic : ");
                    startingTime = System.currentTimeMillis();
                    Insertion insertion = new Insertion(adjMatrixFromTspFile);
                    elapsedTime = System.currentTimeMillis() - startingTime;
                    out.printf("        \t\ttime : %7dms         distance :%15f \n", elapsedTime, insertion.getLength_path());
                    outForML.println(" " + (0.3 / elapsedTime + 0.7 * insertion.getLength_path()) / (1 / elapsedTime + 1 / insertion.getLength_path()));
                    if (debug) {
                        System.out.println(insertion);
                    }
                }
                System.out.println("[" + (i + 1) + "]   " + name + ":  ✔ ");
            }
        }
        out.flush();
        outForML.flush();
        out.close();
        outForML.close();
    }

}
