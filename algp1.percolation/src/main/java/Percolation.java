/**
 * Program for running a percolation experiment.
 * 
 * @author Ovidiu Parvu
 */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/**
 * Class used to represent a percolation experiment.
 */
public class Percolation {

    private static final String ERR_INVALID_INDEX_BEGIN     
        = "The index \"";
    private static final String ERR_INVALID_INDEX_MIDDLE    
        = "\" is invalid because it is not between 1 and ";
    private static final String ERR_INVALID_INDEX_END       
        = ".";
    private static final String ERR_INVALID_GRID_SIZE_BEGIN 
        = "The provided grid size \"";
    private static final String ERR_INVALID_GRID_SIZE_END 
        = "\" is invalid because it is non-positive.";
    
    
    private int     gridSize;               // The number of grid rows/cols
    private int     nrOfSites;              // The number of grid sites
    private byte[]  openSites;              // Array that records which sites 
                                            // are open
    private boolean doesPercolate;          // Flag indicating if the system 
                                            // percolates
    
    private WeightedQuickUnionUF topUnionFind;      // The union-find data type 
                                                    // used to record sites 
                                                    // connected to a virtual 
                                                    // top site
    private WeightedQuickUnionUF bottomUnionFind;   // The union-find data type 
                                                    // used to record sites 
                                                    // connected to a virtual 
                                                    // bottom site
    
    /**
     * Construct a grid of size N x N for a percolation experiment.
     * 
     * @param N The number of rows/columns in the grid.
     */
    public Percolation(int N) {
        validateGridSize(N);
        initialize(N);
    }
    
    /**
     * Open the site (i, j) in the grid.
     * The indices i and j have values between 1 and N.
     *  
     * @param i The row index.
     * @param j The column index.
     */
    public void open(int i, int j) {
        validateIndex(i);
        validateIndex(j);
        
        openValidSite(i - 1, j - 1);
    }
    
    /**
     * Check if the site (i, j) in the grid is open.
     * 
     * @param i The row index.
     * @param j The column index.
     */
    public boolean isOpen(int i, int j) {
        validateIndex(i);
        validateIndex(j);
        
        int siteIndex = getSiteIndex(i - 1, j - 1);
        
        return isValidSiteOpen(siteIndex);
    }
    
    /**
     * Check if the site (i, j) in the grid is full.
     * 
     * @param i The row index.
     * @param j The column index.
     */
    public boolean isFull(int i, int j) {
        validateIndex(i);
        validateIndex(j);
        
        int siteIndex = getSiteIndex(i - 1, j - 1) + 1;
        
        return isValidSiteFull(siteIndex);
    }
    
    /**
     * Check if the system percolates.
     * 
     * @return True if the system percolates, false otherwise.
     */
    public boolean percolates() {
        return doesPercolate;
    }
    
    /**
     * Check if the given grid size is positive.
     * 
     * @param gridSize  The grid size.
     */
    private void validateGridSize(int gridSize) {
        if (gridSize <= 0) {
            throw new IllegalArgumentException(
                ERR_INVALID_GRID_SIZE_BEGIN +
                gridSize +
                ERR_INVALID_GRID_SIZE_END
            );
        }
    }
    
    /**
     * Initialization function.
     * 
     * @param gridSize The grid size.
     */
    private void initialize(int gridSize) {
        this.gridSize           = gridSize;
        this.nrOfSites          = (gridSize * gridSize);
        this.openSites          = new byte[(nrOfSites / 8) + 1];
        this.doesPercolate      = false;
        this.topUnionFind       = new WeightedQuickUnionUF(nrOfSites + 2);
        this.bottomUnionFind    = new WeightedQuickUnionUF(nrOfSites + 2);
        
        connectVirtualSitesToNeighbouringSites();
    }
    
    /**
     * Connect the virtual sites to the neighbouring sites.
     */
    private void connectVirtualSitesToNeighbouringSites() {
        int lastSiteIndex = nrOfSites + 1;
        
        for (int i = 0; i < gridSize; ++i) {
            topUnionFind.union(0, i + 1);
            bottomUnionFind.union(lastSiteIndex, nrOfSites - i);
        }
    }

    /**
     * Check if the given index is between 1 and N.
     * 
     * @param index The index.
     */
    private void validateIndex(int index) {
        if ((index < 1) || (index > gridSize)) {
            throw new IndexOutOfBoundsException(
                 ERR_INVALID_INDEX_BEGIN + 
                 index + 
                 ERR_INVALID_INDEX_MIDDLE + 
                 gridSize +
                 ERR_INVALID_INDEX_END
            );
        }
    }
    
    /**
     * Open the valid site (i, j) of the grid.
     * The indices i and j have values between 0 and N - 1.
     * 
     * @param i The row index.
     * @param j The column index.
     */
    private void openValidSite(int i, int j) {
        int validSiteIndex = getSiteIndex(i, j);
        
        if (!isValidSiteOpen(validSiteIndex)) {
            setSiteOpenState(validSiteIndex, true);
            
            tryConnect(validSiteIndex, i - 1, j);
            tryConnect(validSiteIndex, i + 1, j);
            tryConnect(validSiteIndex, i, j - 1);
            tryConnect(validSiteIndex, i, j + 1);
            
            updatePercolationStatus(validSiteIndex);
        }
    }

    /**
     * Connect the site corresponding to index with the site (i, j) if site 
     * (i, j) exists and is open. 
     * 
     * @param index The site index.
     * @param i     The row index for the other site.
     * @param j     The column index for the other site.
     */
    private void tryConnect(int index, int i, int j) {
        if ((i < 0) || (i >= gridSize)) return;
        if ((j < 0) || (j >= gridSize)) return;
        
        int siteIndex = getSiteIndex(i, j);
        
        // Try to connect given site with site (i, j)
        if (isValidSiteOpen(siteIndex)) {
            topUnionFind.union(index + 1, siteIndex + 1);
            bottomUnionFind.union(index + 1, siteIndex + 1);
        }
    }

    /**
     * Check if the site corresponding to index is open.
     *
     * @param index The site index.
     * @return True if the site corresponding to index is open, false otherwise.
     */
    private boolean isValidSiteOpen(int index) {
        return (getSiteOpenState(index) == true);
    }
    
    /**
     * Check if the site corresponding to index is full.
     * 
     * @param index The site index.
     * @return True if the site corresponding to index is full, false otherwise.
     */
    private boolean isValidSiteFull(int index) {
        return topUnionFind.connected(0, index);
    }
    
    /**
     * Get the open state of the site corresponding to the given index.
     * 
     * @param index The site index.
     * @return The open state corresponding to the given index.
     */
    private boolean getSiteOpenState(int index) {
        return ((openSites[index / 8] & (1 << (index % 8))) != 0);
    }
    
    /**
     * Set the open state of the site corresponding to the given index.
     * 
     * @param index The site index.
     * @param value The new site state value.
     */
    private void setSiteOpenState(int index, boolean value) {
        if (value == true) {
            openSites[index / 8] |= (1 << (index % 8));
        } else {
            openSites[index / 8] &= (~(1 << (index % 8)));
        }
    }

    /**
     * Get the index of the site (i, j).
     * The indices i and j have values between 0 and N - 1.
     * 
     * @param i The row index.
     * @param j The column index.
     * @return  The index of the site (i, j) in a 1D array considering the
     *          virtual top site.
     */
    private int getSiteIndex(int i, int j) {
        return (i * gridSize) + j;
    }
    
    /**
     * Set the percolation status to true if the given site is full and is
     * connected to the bottom.
     * 
     * @param index The site index.
     */
    private void updatePercolationStatus(int index) {
        if (doesPercolate == false) {
            doesPercolate = (
                isValidSiteFull(index + 1) &&
                isValidSiteConnectedToBottom(index + 1)
            );
        }
    }

    /**
     * Check if the site corresponding to the given index is connected to the 
     * bottom grid row.
     * 
     * @param index The site index.
     * @return True if the site corresponding to the given index is connected to
     *         the bottom grid row, false otherwise.
     */
    private boolean isValidSiteConnectedToBottom(int index) {
        return bottomUnionFind.connected(index, nrOfSites + 1);
    }
    
}
