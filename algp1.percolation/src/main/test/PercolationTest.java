/**
 * Program for testing various percolation experiments.
 * 
 * @author Ovidiu Parvu
 */

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import edu.princeton.cs.algs4.StdRandom;

/**
 * Class to test various percolation experiments.
 */
public class PercolationTest {

    private final static int GRID_SIZE = 5;
    
    private Percolation percolation;    // The percolation experiment
    
    @Before
    public void setUp() {
        percolation = new Percolation(GRID_SIZE);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidGridSize() {
        percolation = new Percolation(-GRID_SIZE);
    }
    
    @Test
    public void testValidGridSize() {
        // Do nothing
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidNegativeOpenRowIndex() {
        percolation.open(-GRID_SIZE - 1, 1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidTooLargeOpenRowIndex() {
        percolation.open(GRID_SIZE * 2, 1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidNegativeOpenColumnIndex() {
        percolation.open(1, -GRID_SIZE - 4);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidTooLargeOpenColumnIndex() {
        percolation.open(1, GRID_SIZE * 10);
    }
    
    @Test
    public void testOpen() {
        percolation.open(1, 1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidNegativeIsOpenRowIndex() {
        percolation.isOpen(-GRID_SIZE - 1, 1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidTooLargeIsOpenRowIndex() {
        percolation.isOpen(GRID_SIZE * 2, 1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidNegativeIsOpenColumnIndex() {
        percolation.isOpen(1, -GRID_SIZE - 4);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidTooLargeIsOpenColumnIndex() {
        percolation.isOpen(1, GRID_SIZE * 10);
    }
    
    @Test
    public void testIsOpen() {
        assertFalse(percolation.isOpen(1, 1));
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidNegativeIsFullRowIndex() {
        percolation.isFull(-GRID_SIZE - 1, 1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidTooLargeIsFullRowIndex() {
        percolation.isFull(GRID_SIZE * 2, 1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidNegativeIsFullColumnIndex() {
        percolation.isFull(1, -GRID_SIZE - 4);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidTooLargeIsFullColumnIndex() {
        percolation.isFull(1, GRID_SIZE * 10);
    }
    
    @Test
    public void testIsFull() {
        percolation.open(1, 1);
        
        assertTrue(percolation.isFull(1, 1));
    }
    
    @Test
    public void testDoesNotPercolate1() {
        assertFalse(percolation.percolates());
    }
    
    @Test
    public void testDoesNotPercolate2() {
        for (int i = 1; i < GRID_SIZE; ++i) {
            for (int j = 1; j <= GRID_SIZE; ++j) {
                percolation.open(i, j);
            }
        }
        
        assertFalse(percolation.percolates());
    }
    
    @Test
    public void testDoesNotPercolate3() {
        percolation = new Percolation(1);
        
        assertFalse(percolation.percolates());
    }
    
    @Test
    public void testDoesNotPercolateRandom() {
        int colIndex = 0;
        
        for (int i = 1; i < GRID_SIZE; ++i) {
            for (int j = 1; j <= GRID_SIZE; ++j) {
                colIndex = StdRandom.uniform(GRID_SIZE) + 1;
                
                percolation.open(i, colIndex);
            }
        }
        
        assertFalse(percolation.percolates());
    }
    
    @Test
    public void testPercolates1() {
        for (int i = 1; i <= GRID_SIZE; ++i) {
            percolation.open(i, 1);
        }
        
        assertTrue(percolation.percolates());
    }
    
    @Test
    public void testPercolates2() {
        for (int i = 1; i <= GRID_SIZE; ++i) {
            for (int j = 1; j <= GRID_SIZE; ++j) {
                percolation.open(i, j);
            }
        }
        
        assertTrue(percolation.percolates());
    }
    
    @Test
    public void testPercolates3() {
        for (int i = 1; i <= GRID_SIZE; ++i) {
            percolation.open(i, GRID_SIZE);
        }
        
        assertTrue(percolation.percolates());
    }
    
    @Test
    public void testPercolates4() {
        percolation = new Percolation(1);
        
        percolation.open(1, 1);
        
        assertTrue(percolation.percolates());
    }
    
    @Test
    public void testPercolatesRandom() {
        int colIndex = 0;
        
        for (int i = 1; i <= GRID_SIZE; ++i) {
            for (int j = 1; j <= GRID_SIZE; ++j) {
                colIndex = StdRandom.uniform(GRID_SIZE) + 1;
                
                percolation.open(i, colIndex);
            }
        }
        
        for (int i = 1; i <= GRID_SIZE; ++i) {
            percolation.open(i, 1);
        }
        
        assertTrue(percolation.percolates());
    }
    
    @Test
    public void testPercolationBacklash() {
        for (int i = 1; i <= GRID_SIZE; ++i) {
            percolation.open(i, GRID_SIZE);
        }
        
        percolation.open(GRID_SIZE, 1);
        
        assertTrue(percolation.percolates());
        assertFalse(percolation.isFull(GRID_SIZE, 1));
    }
    
    @Test
    public void testDoesNotPercolatePerformanceTest() {
        int colIndex    = 0;
        int newGridSize = GRID_SIZE * 200;
        
        percolation = new Percolation(newGridSize);
        
        for (int i = 1; i < newGridSize; ++i) {
            for (int j = 1; j <= newGridSize; ++j) {
                colIndex = StdRandom.uniform(newGridSize) + 1;
                
                percolation.open(i, colIndex);
            }
        }
        
        assertFalse(percolation.percolates());
    }
    
    @Test
    public void testPercolatesPerformanceTest() {
        int colIndex    = 0;
        int newGridSize = GRID_SIZE * 200;
        
        percolation = new Percolation(newGridSize);
        
        for (int i = 1; i <= newGridSize; ++i) {
            for (int j = 1; j <= newGridSize; ++j) {
                colIndex = StdRandom.uniform(newGridSize) + 1;
                
                percolation.open(i, colIndex);
            }
        }
        
        for (int i = 1; i <= newGridSize; ++i) {
            percolation.open(i, 1);
        }
        
        assertTrue(percolation.percolates());
    }
    
}
