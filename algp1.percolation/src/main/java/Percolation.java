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
    
    
    private int                     gridSize;   // The number of grid rows/cols
    private int                     nrOfSites;  // The number of grid sites
    private byte[]                  openSites;  // Array that records which 
                                                // sites are open
    private WeightedQuickUnionUF    unionFind;  // The union-find data type used
                                                // to record connected sites

    private boolean                 doesPercolate;  // Flag indicating if the
                                                    // system percolates.
    
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
        
        return isValidSiteOpen(i - 1, j - 1);
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
        
        return unionFind.connected(0, getSiteIndex(i - 1, j - 1) + 1);
    }
    
    /**
     * Check if the system percolates.
     * 
     * @return True if the system percolates, false otherwise.
     */
    public boolean percolates() {
        if (gridSize > 1) {
            return unionFind.connected(0, nrOfSites + 1);
        } else {
            return isOpen(1, 1);
        }
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
        this.gridSize       = gridSize;
        this.nrOfSites      = (gridSize * gridSize);
        this.openSites      = new byte[(nrOfSites / 8) + 1];
        this.doesPercolate  = false;
        
        // The union-find data type additionally contains a virtual top site
        this.unionFind  = new WeightedQuickUnionUF(nrOfSites + 1);
        
        connectVirtualSitesToNeighbouringSites();
    }
    
    /**
     * Connect the virtual top site to the neighbouring sites.
     */
    private void connectVirtualSitesToNeighbouringSites() {
        for (int i = 0; i < gridSize; ++i) {
            unionFind.union(0, i + 1);
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
        if (!isValidSiteOpen(i, j)) {
            int validSiteIndex = getSiteIndex(i, j);

            setSiteState(validSiteIndex, true);
            
            tryConnect(validSiteIndex, i - 1, j);
            tryConnect(validSiteIndex, i + 1, j);
            tryConnect(validSiteIndex, i, j - 1);
            tryConnect(validSiteIndex, i, j + 1);
        }
    }

    /**
     * Connect the site corresponding to index with the site (i, j) if site 
     * (i, j) is open. If the given site index corresponds to a site on the
     * bottom row of the system then connect the site with the virtual bottom
     * site.
     * 
     * The indices i and j have values between 0 and N - 1.
     * 
     * @param index The site index.
     * @param i     The row index for the other site.
     * @param j     The column index for the other site.
     */
    private void tryConnect(int index, int i, int j) {
        // Try to connect given site with site (i, j)
        if (isSiteOpen(i, j)) {
            unionFind.union(index + 1, getSiteIndex(i, j) + 1);
        }
    }

    /**
     * Check if the site (i, j) is open.
     * 
     * @param i The row index.
     * @param j The column index.
     * @return  True if i and j are between 0 and N - 1 and the (i, j)-th site
     *          is open, false otherwise.
     */
    private boolean isSiteOpen(int i, int j) {
        if ((i < 0) || (i >= gridSize)) return false;
        if ((j < 0) || (j >= gridSize)) return false;
        
        return isValidSiteOpen(i, j);
    }
    
    /**
     * Check if the site (i, j) is open.
     *
     * The indices i and j have values between 0 and N - 1.
     * 
     * @param i The row index.
     * @param j The column index.
     * @return  True if the (i, j)-th site is open, false otherwise.
     */
    private boolean isValidSiteOpen(int i, int j) {
        int siteIndex = getSiteIndex(i , j);
        
        return ((openSites[siteIndex / 8] & (1 << (siteIndex % 8))) != 0);
    }
    
    /**
     * Set the state of the site corresponding to the given index.
     * 
     * @param index The site index.
     * @param value The new site state value.
     */
    private void setSiteState(int index, boolean value) {
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
    
}
