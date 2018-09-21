/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implementation.analytics;

/**
 *
 * @author HP
 */
public class BuzenSolver {
    
    public static double[] solveBuzenAlgorithm(int n, int k, double[] x) {
        double[] g = new double[n + 1];
        g[0] = 1;
        
        for (int j = 1; j <= k; j++) {
            for (int i = 1; i <= n; i++) g[i] += x[j - 1] * g[i - 1];
        }
        
        return g;
    }
    
}