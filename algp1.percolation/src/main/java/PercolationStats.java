/**
 * Program for estimating the percolation threshold considering an N x N grid.
 * 
 * @author Ovidiu Parvu
 */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

/**
 * Class for estimating the percolation threshold considering an N x N grid.
 */
public class PercolationStats {
    
    private static final String ERR_INVALID_GRID_SIZE_BEGIN 
        = "The provided grid size \"";
    private static final String ERR_INVALID_GRID_SIZE_END 
        = "\" is invalid because it is non-positive.";
    private static final String ERR_INVALID_NR_MONTE_CARLO_SIMULATIONS_BEGIN 
        = "The provided number of Monte Carlo simulations \"";
    private static final String ERR_INVALID_NR_MONTE_CARLO_SIMULATIONS_END 
        = "\" is invalid because it is non-positive.";
    private static final String ERR_INVALID_NR_CMD_LINE_ARGS 
        = "Usage: PercolationStats <grid-size> <nr-monte-carlo-simulation>.";
    
    private static final String OUT_SEPARATOR    
        = ", ";
    private static final String OUT_CONF_INTERVAL 
        = "95% confidence interval = ";
    private static final String OUT_STDDEV        
        = "stddev                  = ";
    private static final String OUT_MEAN 
        = "mean                    = ";

    private int      gridSize;                  // The grid size
    private int      nrOfMonteCarloSimulations; // The number of Monte Carlo
                                                // simulations to perform
    private int      nrOfSites;                 // The number of sites in the 
                                                // grid
    private double[] percolationThresholds;     // The percolation thresholds
                                                // estimated during the Monte 
                                                // Carlo simulations
    
    private double mean;            // The mean percolation threshold
    private double stdDev;          // The standard deviation of all percolation 
                                    // thresholds estimated
    private double confidenceLow;   // The percolation threshold 95% confidence 
                                    // interval lower bound
    private double confidenceHigh;  // The percolation threshold 95% confidence 
                                    // interval upper bound
    
    /**
     * Create an instance for approximating the percolation threshold 
     * considering an N x N grid by running T Monte Carlo simulations.
     * 
     * @param N The grid size.
     * @param T The number of Monte Carlo simulations.
     */
    public PercolationStats(int N, int T) {
        validateGridSize(N);
        validateNrOfMonteCarloSimulations(T);
        
        initialize(N, T);
        runExperiments();
        updateStatisticalAnalysisResults();
    }

    public double mean() {
        return mean;
    }
    
    public double stddev() {
        return stdDev;
    }
    
    public double confidenceLo() {
        return confidenceLow;
    }
    
    public double confidenceHi() {
        return confidenceHigh;
    }

    // Test client
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println(ERR_INVALID_NR_CMD_LINE_ARGS);
            System.exit(1);
        }
        
        // Read input values
        int gridSize        = Integer.parseInt(args[0]);
        int nrSimulations   = Integer.parseInt(args[1]);
        
        PercolationStats stats = new PercolationStats(gridSize, nrSimulations);
        
        outputPercolationStats(stats);
    }

    /**
     * Check if the grid size is positive.
     * 
     * @param gridSize The grid size.
     * @throws IllegalArgumentException if the grid size is non-positive.
     */
    private void validateGridSize(
        int gridSize
    ) throws IllegalArgumentException {
        if (gridSize <= 0) {
            throw new IllegalArgumentException(
                ERR_INVALID_GRID_SIZE_BEGIN +
                gridSize +
                ERR_INVALID_GRID_SIZE_END
            );
        }
    }
    
    /**
     * Check if the number of Monte Carlo simulations is positive.
     * 
     * @param nrOfMonteCarloSimulations The number of Monte Carlo simulations.
     * @throws IllegalArgumentException if the number of Monte Carlo simulations
     *         is non-positive.
     */
    private void validateNrOfMonteCarloSimulations(
        int nrOfMonteCarloSimulations
    ) throws IllegalArgumentException {
        if (nrOfMonteCarloSimulations <= 0) {
            throw new IllegalArgumentException(
                ERR_INVALID_NR_MONTE_CARLO_SIMULATIONS_BEGIN +
                nrOfMonteCarloSimulations +
                ERR_INVALID_NR_MONTE_CARLO_SIMULATIONS_END
            );
        }
    }
    
    /**
     * Initialization function.
     * 
     * @param N The grid size.
     * @param T The number of Monte Carlo simulations to perform.
     */
    private void initialize(int N, int T) {
        this.gridSize                   = N;
        this.nrOfMonteCarloSimulations  = T;
        this.nrOfSites                  = (gridSize * gridSize);
        this.percolationThresholds      = new double[nrOfMonteCarloSimulations];
        
        this.mean           = 0;
        this.stdDev         = 0;
        this.confidenceLow  = 0;
        this.confidenceHigh = 0;
    }
    
    /**
     * Run the Monte Carlo simulations.
     */
    private void runExperiments() {
        for (int i = 0; i < nrOfMonteCarloSimulations; ++i) {
            percolationThresholds[i] = runMonteCarloPercolationSimulation();
        }
    }

    /**
     * Run a Monte Carlo simulation of the percolation experiment in order to
     * estimate the percolation threshold.
     * 
     * @return The estimated percolation threshold.
     */
    private double runMonteCarloPercolationSimulation() {
        Percolation percolationExperiment   = new Percolation(gridSize);
        int         nrOfOpenedSites         = 0;
        
        int randomRow = 0;
        int randomCol = 0;
        
        // Open random sites while the system does not percolate
        while (!percolationExperiment.percolates()) {
            randomRow = 1 + StdRandom.uniform(gridSize);
            randomCol = 1 + StdRandom.uniform(gridSize);
            
            if (!percolationExperiment.isOpen(randomRow, randomCol)) {
                percolationExperiment.open(randomRow, randomCol);
                
                ++nrOfOpenedSites;
            }
        }
        
        return ((double) nrOfOpenedSites / nrOfSites);
    }

    /**
     * Update the statistical analysis results considering the results obtained
     * from the Monte Carlo simulations.
     */
    private void updateStatisticalAnalysisResults() {
        updateMean();
        updateStdDev();
        updateConfidenceInterval();
    }

    /**
     * Update the mean value according to the results obtained from the Monte
     * Carlo simulations.
     */
    private void updateMean() {
        mean = StdStats.mean(percolationThresholds);
    }
    
    /**
     * Update the standard deviation value according to the results obtained 
     * from the Monte Carlo simulations.
     */
    private void updateStdDev() {
        stdDev = StdStats.stddev(percolationThresholds);
    }

    /**
     * Update the confidence interval bounds according to the results obtained 
     * from the Monte Carlo simulations.
     */
    private void updateConfidenceInterval() {
        double lhs = mean;
        double rhs = 1.96 * stdDev / Math.sqrt(nrOfMonteCarloSimulations);
        
        confidenceLow   = lhs - rhs;
        confidenceHigh  = lhs + rhs;
    }

    /**
     * Output the percolation statistics.
     * 
     * @param stats The percolation statistics.
     */
    private static void outputPercolationStats(PercolationStats stats) {
        StdOut.println(OUT_MEAN + stats.mean());
        StdOut.println(OUT_STDDEV + stats.stddev());
        StdOut.println(
            OUT_CONF_INTERVAL + stats.confidenceLo() + 
            OUT_SEPARATOR + stats.confidenceHi()
        );
    }
    
}
