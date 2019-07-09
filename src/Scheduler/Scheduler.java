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
        Parser in;
        long elapsedTime1, elapsedTime2, elapsedTime3, elapsedTime4, elapsedTime5;
        double d1, d2, d3, d4, d5;
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
                double LB = knownLowerBound;
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
                /*writing stats*/
                out.println("       Min    : " + minMax.get(0) + " , " + minMax.get(1) + " , " + minMax.get(2));
                out.println("       Max    : " + minMax.get(dimension - 1) + " , " + minMax.get(dimension - 2) + " , " + minMax.get(dimension - 3));
                out.println("       Avg    : " + utils.avg(adjMatrixFromTspFile));
                out.println("       Var    : " + utils.var(adjMatrixFromTspFile));
                out.println("       Median : " + minMax.get(Math.round(minMax.size() / 2)));
                out.println("--------------");
                /*new format for Ml :*/
                double avg = Utils.utils.avg(adjMatrixFromTspFile);
                double var = Utils.utils.var(adjMatrixFromTspFile);
                double min = minMax.get(0);
                double max = minMax.get(dimension - 1);
                double med = minMax.get(Math.round(minMax.size() / 2));
                double avgMin = (minMax.get(0) + minMax.get(1) + minMax.get(2)) / 3;
                double avgMax = (minMax.get(dimension - 1) + minMax.get(dimension - 2) + minMax.get(dimension - 3)) / 3;

                /*the LinKernighan Heuristic*/
                {
                    LinKernighan lk = new LinKernighan(adjMatrixFromTspFile);
                    out.println("       [1] LinKernighan : ");
                    startingTime = System.currentTimeMillis();
                    lk.runAlgorithm();
                    elapsedTime1 = System.currentTimeMillis() - startingTime;
                    if (elapsedTime1 == 0) elapsedTime1 = 1;
                    out.printf("        \t\ttime : %7dms         distance :%15f \n", elapsedTime1, lk.getDistance());
                    d1 = lk.getDistance();
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
                    elapsedTime2 = System.currentTimeMillis() - startingTime;
                    if (elapsedTime2 == 0) elapsedTime2 = 1;
                    d2 = length;
                    out.printf("        \t\ttime : %7dms         distance :%15f\n", elapsedTime2, length);
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
                    elapsedTime3 = System.currentTimeMillis() - startingTime;
                    if (elapsedTime3 == 0) elapsedTime3 = 1;
                    d3 = randomHeuristic.getDistance();
                    randomHeuristic.getRandomTour();
                    out.printf("         \t\ttime : %7dms         distance :%15f \n", elapsedTime3, randomHeuristic.getDistance());
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
                    elapsedTime4 = System.currentTimeMillis() - startingTime;
                    if (elapsedTime4 == 0) elapsedTime4 = 1;
                    d4 = Length.routeLength1(neighbour.getTour());
                    out.printf("        \t\ttime : %7dms         distance :%15f \n", elapsedTime4, Length.routeLength1(neighbour.getTour()));
                    if (debug) {
                        System.out.println(neighbour);
                    }
                }
                /* Insertion Heuristic*/
                {
                    out.println("       [5] Insertion Heuristic : ");
                    startingTime = System.currentTimeMillis();
                    Insertion insertion = new Insertion(adjMatrixFromTspFile);
                    elapsedTime5 = System.currentTimeMillis() - startingTime;
                    if (elapsedTime5 == 0) elapsedTime5 = 1;
                    d5 = insertion.getLength_path();
                    out.printf("        \t\ttime : %7dms         distance :%15f \n", elapsedTime5, insertion.getLength_path());
                    if (debug) {
                        System.out.println(insertion);
                    }
                }
                /*writing results*/
                {
                    double NormeParams = Math.sqrt(dimension * dimension + avg * avg + var * var + med * med +
                            min * min + max * max + avgMin * avgMin + avgMax * avgMax);
                    //distances
                    d1 = 1 / (d1 - LB);
                    d2 = 1 / (d2 - LB);
                    d3 = 1 / (d3 - LB);
                    d4 = 1 / (d4 - LB);
                    d5 = 1 / (d5 - LB);
                    double d_sum = d1 + d2 + d3 + d4 + d5;
                    double d[] = {d1 / d_sum, d2 / d_sum, d3 / d_sum, d4 / d_sum, d5 / d_sum};
                    //params
                    double size = dimension / NormeParams;
                    avg = avg / NormeParams;
                    var = var / NormeParams;
                    med = med / NormeParams;
                    min = min / NormeParams;
                    max = max / NormeParams;
                    avgMin = avgMin / NormeParams;
                    avgMax = avgMax / NormeParams;
                    outForML.print(size + " " + avg + " " + var + " " + min + " " + max + " " + med
                            + " " + avgMin + " " + avgMax);
                    //apply the format:
                    for (int counter = 0; counter < d.length; counter++) {
                        outForML.print(" " + d[counter]);
                    }
                    outForML.println();
                    System.out.println("[" + (i + 1) + "]   " + name + ":  ✔ ");
                }

            }
        }
        out.flush();
        outForML.flush();
        out.close();
        outForML.close();
    }

}
