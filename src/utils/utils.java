package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

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
                    matrix[i][j] = Integer.parseInt(line[j]);
                }
            }
        }
        return matrix;
    }
}
