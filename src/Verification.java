import TSPFileparser.Parser;
import Utils.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Verification {
    private static void readOutputs() throws IOException {
        String dataPath = "./data_KLB";
        String statsPath = "./statistics/official.txt.MLFormat";
        File folder = new File(dataPath);
        File[] listOfFiles = folder.listFiles();
        Arrays.sort(listOfFiles);
        for (int i = 0; i < listOfFiles.length; i++) {
            String fileName = listOfFiles[i].getName();
            System.out.printf("[%d]     %s:\n", i, fileName);
        }
        Scanner scanner = new Scanner(System.in);
        int idx;
        do {
            System.out.print("Select the dataset to test: ");
            idx = scanner.nextInt();
        } while (idx >= listOfFiles.length || idx < 0);
        String line = Files.readAllLines(Paths.get(statsPath)).get(idx);
        String[] tokens = line.split(" ");
        ArrayList<Struct> list = new ArrayList();
        list.add(new Struct("LinKernighan", Double.parseDouble(tokens[12])));
        list.add(new Struct("2-OPT", Double.parseDouble(tokens[11])));
        list.add(new Struct("Random", Double.parseDouble(tokens[10])));
        list.add(new Struct("Nearest Neighbour", Double.parseDouble(tokens[9])));
        list.add(new Struct("Insertion", Double.parseDouble(tokens[8])));
        Collections.sort(list, new Comparator<Struct>() {
            @Override
            public int compare(Struct c1, Struct c2) {
                return Double.compare(c1.getValue(), c2.getValue());
            }
        });
        for (Struct struct1 : list) {
            System.out.println(struct1.getName() + "              : " + struct1.getValue());
        }
    }

    private static ArrayList<Struct> getStats(String path) {
        Parser in = new Parser(new File(path));
        double[][] adjMatrixFromTspFile = in.getAdjacencyMatrix();
        int dimension = adjMatrixFromTspFile[0].length;
        List<Double> minMax = utils.minMax(adjMatrixFromTspFile);
        double avg = Utils.utils.avg(adjMatrixFromTspFile);
        double var = Utils.utils.var(adjMatrixFromTspFile);
        double min = minMax.get(0);
        double med = minMax.get(Math.round(minMax.size() / 2));
        double max = minMax.get(dimension - 1);
        double avgMin = (minMax.get(0) + minMax.get(1) + minMax.get(2)) / 3;
        double avgMax = (minMax.get(dimension - 1) + minMax.get(dimension - 2) + minMax.get(dimension - 3)) / 3;
        ArrayList<Struct> list = new ArrayList<>();
        list.add(new Struct("avg", avg));
        list.add(new Struct("var", var));
        list.add(new Struct("min", min));
        list.add(new Struct("max", max));
        list.add(new Struct("med", med));
        list.add(new Struct("avgMin", avgMin));
        list.add(new Struct("avgMax", avgMax));
        Collections.sort(list, new Comparator<Struct>() {
            @Override
            public int compare(Struct c1, Struct c2) {
                return Double.compare(c1.getValue(), c2.getValue());
            }
        });
        return list;
    }

    public static void main(String[] args) throws IOException {
//        readOutputs();
        ArrayList<Struct> l = getStats("./data_KLB/d198.tsp");
        for (Struct struct : l) {
            System.out.println(struct.getName() + "       " + struct.getValue());
        }
    }
}