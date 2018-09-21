package implementation.analytics;

import implementation.components.Server;
import implementation.analytics.util.Probability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GordonNewellSolver {

    public static double[] solveGordonNewellEquation(Map<String, Server> servers, Map<String, Probability> probabilities) {
        int n = servers.size(); // matrix size
        double[][] paramMatrixTemp = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                paramMatrixTemp[i][j] = 0;
            }
        }

        List<Server> serverList = new ArrayList<>();
        for (Server server : servers.values()) {
            serverList.add(server);
        }

        for (int i = 0; i < n; i++) {
            Server from = serverList.get(i);

            for (int j = 1; j < n; j++) {
                Server to = serverList.get(j);
                String key = to.getId() + "-" + from.getId();
                Probability probability = probabilities.getOrDefault(key, null);
                if (probability == null) {
                    probability = new Probability();
                    probability.probability = 0;
                }

                if (i == j) {
                    paramMatrixTemp[i][j - 1] = (-1) * (1 - probability.probability) * 1.0 / to.getAverageExecutionTime();
                } else {
                    paramMatrixTemp[i][j - 1] = probability.probability * 1.0 / to.getAverageExecutionTime();
                }
            }

            Server to = serverList.get(0);
            String key = to.getId() + "-" + from.getId();
            Probability probability = probabilities.getOrDefault(key, null);
            if (probability == null) {
                probability = new Probability();
                probability.probability = 0;
            }

            if (i == 0) paramMatrixTemp[i][n - 1] = (1 - probability.probability) * 1.0 / to.getAverageExecutionTime();
            else paramMatrixTemp[i][n - 1] = (-1) * probability.probability * 1.0 / to.getAverageExecutionTime();
        }

        double[][] paramMatrix = new double[n - 1][n - 1];
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1; j++) paramMatrix[i][j] = paramMatrixTemp[i][j];
        }

        double[][] resultMatrix = new double[n - 1][1];
        for (int i = 0; i < n - 1; i++) {
            resultMatrix[i][0] = paramMatrixTemp[i][n - 1];
        }

        double[][] invertedMatrix = invert(paramMatrix);
        double[][] result = new double[n - 1][1];
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < 1; j++) {
                for (int k = 0; k < n - 1; k++) {
                    result[i][j] = result[i][j] + invertedMatrix[i][k] * resultMatrix[k][j];
                }
            }
        }

        double[] solution = new double[n];
        solution[0] = 1;
        for (int i = 1; i < n; i++) solution[i] = result[i - 1][0];

        return solution;
    }

    public static double[][] invert(double a[][]) {
        int n = a.length;
        double x[][] = new double[n][n];
        double b[][] = new double[n][n];
        int index[] = new int[n];
        for (int i = 0; i < n; ++i)
            b[i][i] = 1;

        // Transform the matrix into an upper triangle
        gaussian(a, index);

        // Update the matrix b[i][j] with the ratios stored
        for (int i = 0; i < n - 1; ++i)
            for (int j = i + 1; j < n; ++j)
                for (int k = 0; k < n; ++k)
                    b[index[j]][k]
                            -= a[index[j]][i] * b[index[i]][k];

        // Perform backward substitutions
        for (int i = 0; i < n; ++i) {
            x[n - 1][i] = b[index[n - 1]][i] / a[index[n - 1]][n - 1];
            for (int j = n - 2; j >= 0; --j) {
                x[j][i] = b[index[j]][i];
                for (int k = j + 1; k < n; ++k) {
                    x[j][i] -= a[index[j]][k] * x[k][i];
                }
                x[j][i] /= a[index[j]][j];
            }
        }
        return x;
    }

    public static void gaussian(double a[][], int index[]) {
        int n = index.length;
        double c[] = new double[n];

        // Initialize the index
        for (int i = 0; i < n; ++i)
            index[i] = i;

        // Find the rescaling factors, one from each row
        for (int i = 0; i < n; ++i) {
            double c1 = 0;
            for (int j = 0; j < n; ++j) {
                double c0 = Math.abs(a[i][j]);
                if (c0 > c1) c1 = c0;
            }
            c[i] = c1;
        }

        // Search the pivoting element from each column
        int k = 0;
        for (int j = 0; j < n - 1; ++j) {
            double pi1 = 0;
            for (int i = j; i < n; ++i) {
                double pi0 = Math.abs(a[index[i]][j]);
                pi0 /= c[index[i]];
                if (pi0 > pi1) {
                    pi1 = pi0;
                    k = i;
                }
            }

            // Interchange rows according to the pivoting order
            int itmp = index[j];
            index[j] = index[k];
            index[k] = itmp;
            for (int i = j + 1; i < n; ++i) {
                double pj = a[index[i]][j] / a[index[j]][j];

                // Record pivoting ratios below the diagonal
                a[index[i]][j] = pj;

                // Modify other elements accordingly
                for (int l = j + 1; l < n; ++l)
                    a[index[i]][l] -= pj * a[index[j]][l];
            }
        }
    }

}
