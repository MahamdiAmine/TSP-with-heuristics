package Utils;


import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class utils {
    public static double[][] readFromFile(String data_path) throws Exception {
        Scanner sc;
        sc = new Scanner(new BufferedReader(new FileReader(data_path)));
        Path path = Paths.get(data_path);
        int index = (int) Files.lines(path).count();
        double matrix[][] = new double[index][index];
        while (sc.hasNextLine()) {
            for (int i = 0; i < matrix.length; i++) {
                String[] line = sc.nextLine().trim().split(" ");
                for (int j = 0; j < line.length; j++) {
                    matrix[i][j] = Double.parseDouble(line[j]);
                }
            }
        }
        return matrix;
    }

    public static double var(double[][] adjMatrix) {
        List<Double> list = minMax(adjMatrix);
        double value, sum = 0, avg = avg(adjMatrix);
        for (int i = 0; i < list.size(); i++) {
            value = list.get(i);
            if (value != 0.0) {
                sum += Math.pow(value - avg, 2);
            }
        }
        return Math.sqrt(sum / list.size());
    }

    public static double avg(double[][] adjMatrix) {
        List<Double> list = minMax(adjMatrix);
        double sum = list.stream().mapToDouble(Double::doubleValue).sum();
        return sum / list.size();
    }

    public static List<Double> minMax(double[][] adjMatrix) {
        List<Double> list = new ArrayList<>();
        int dimension = adjMatrix[0].length;
        for (int i = 0; i < dimension; i++) {
            for (int j = i + 1; j < dimension; j++) {
                list.add(adjMatrix[i][j]);
            }
        }
        Collections.sort(list);
        return list;
    }

    public static double getKnownLowerBound(String statsPath, String fileName) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(statsPath));
        String line;
        String fn;
        double bound = 0;
        while ((line = in.readLine()) != null) {
            StringTokenizer strTok = new StringTokenizer(line, " ");
            fn = String.valueOf(strTok.nextToken());
            if (fileName.equals(fn)) {
                bound = Double.valueOf(strTok.nextToken());
                break;
            }
        }
        return bound;
    }

}
