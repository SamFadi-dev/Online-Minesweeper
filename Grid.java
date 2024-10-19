public class Grid
{
    private short gridSize = 7;
    private short numberMines = gridSize;
    private short numberTurnsPlayed = 0;
    private Coordinate[][] currentGrid;

    /**
     * Constructor for the Grid class.
     * @param gridSize The size of the grid.
     * @implNote The number of mines is equal to the size of the grid.
     */
    public Grid(short gridSize)
    {
        if(gridSize < 1)
        {
            System.out.println("Invalid grid size.");
            return;
        }
        this.gridSize = gridSize;
        this.numberMines = gridSize;
        numberTurnsPlayed = 0;
        createInitialBoard();
    }

    /**
     * Get the size of the board.
     * @return The size of the board.
     */
    public short getBoardSize()
    {
        return gridSize;
    }

    /**
     * Create the initial board with all cells hidden.
     * @implNote The board is represented as a 2D array of characters.
     */
    public void createInitialBoard()
    {
        Coordinate[][] board = new Coordinate[gridSize][gridSize];
        for(int i = 0; i < gridSize; i++)
        {
            for(int j = 0; j < gridSize; j++)
            {
                board[i][j] = new Coordinate
                    (i, j, Coordinate.UNREVEALED, Coordinate.Status.UNREVEALED);
            }
        }
        currentGrid = board;
    }

    public void computeFinalGrid()
    {
        for(int i = 0; i < gridSize; i++)
        {
            for(int j = 0; j < gridSize; j++)
            {
                if(currentGrid[i][j].getValue() != Coordinate.BOMB)
                {
                    currentGrid[i][j].setValue
                        ((char)(getNumberOfAdjacentMines(j, i) + '0'));
                }
            }
        }
        printBoard();
    }

    public void flagCell(int x, int y)
    {
        if(x < 0 || x >= gridSize || y < 0 || y >= gridSize)
        {
            System.out.println("Invalid coordinates.");
            return;
        }
        // If the cell is already flagged, unflag it
        if(currentGrid[x][y].getStatus() == Coordinate.Status.FLAGGED)
        {
            currentGrid[x][y].setStatus(Coordinate.Status.UNREVEALED);
        }
        // If the cell is unrevealed, flag it
        else if(currentGrid[x][y].getStatus() == Coordinate.Status.UNREVEALED)
        {
            currentGrid[x][y].setStatus(Coordinate.Status.FLAGGED);
        }
        // If the cell is revealed, do nothing
    }

    public void revealCell(int x, int y)
    {
        if(x < 0 || x >= gridSize || y < 0 || y >= gridSize)
        {
            System.out.println("Invalid coordinates.");
            return;
        }
        if(numberTurnsPlayed == 0)
        {
            placeMines();
            computeFinalGrid();
        }
        else if(currentGrid[x][y].getStatus() == Coordinate.Status.REVEALED)
        {
            return;
        }
        else if(currentGrid[x][y].getValue() == Coordinate.BOMB)
        {
            System.out.println("Game over.");
            currentGrid[x][y].setStatus(Coordinate.Status.REVEALED);
            printBoard();
            return;
        }
        currentGrid[x][y].setStatus(Coordinate.Status.REVEALED);
        numberTurnsPlayed++;
        if(currentGrid[x][y].getValue() == '0')
        {
            propagateReveal(x, y);
        }
    }

    private void propagateReveal(int x, int y)
    {
        if(x < 0 || x >= gridSize || y < 0 || y >= gridSize)
        {
            System.out.println("Invalid coordinates.");
            return;
        }
        if(currentGrid[x][y].getValue() == Coordinate.BOMB)
        {
            return;
        }
        if (currentGrid[x][y].getStatus() == Coordinate.Status.REVEALED)
        {
            return;
        }
        currentGrid[x][y].setStatus(Coordinate.Status.REVEALED);

        // If the cell is empty, reveal all adjacent cells
        if(currentGrid[x][y].getValue() == '0')
        {
            for(int i = x - 1; i <= x + 1; i++)
            {
                for(int j = y - 1; j <= y + 1; j++)
                {
                    if(i >= 0 && i < gridSize && j >= 0 && j < gridSize)
                    {
                        if(currentGrid[i][j].getStatus() 
                            == Coordinate.Status.UNREVEALED)
                        {
                            propagateReveal(i, j);
                        }
                    }
                }
            }
        }
    }

    /**
     * Convert the grid to a string.
     * @param forceReveal If true, reveal all cells. (CHEAT)
     * @return The grid as a string.
     */
    public String convertGridToProtocol(boolean forceReveal)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < gridSize; i++)
        {
            for(int j = 0; j < gridSize; j++)
            {
                // If we want to force reveal the grid, show all cells (CHEAT)
                if(forceReveal)
                {
                    sb.append(currentGrid[i][j].getValue());
                }
                // If the cell is unrevealed, show the unrevealed character
                else if(currentGrid[i][j].getStatus() == Coordinate.Status.UNREVEALED)
                {
                    sb.append(Coordinate.UNREVEALED);
                }
                // If the cell is revealed, show the value
                else if(currentGrid[i][j].getStatus() == Coordinate.Status.REVEALED)
                {
                    sb.append(currentGrid[i][j].getValue());
                }
                // If the cell is flagged, show the flag character
                else
                {
                    sb.append(Coordinate.FLAG);
                }
            }
            sb.append("\\r\\n");
            sb.append("\n");
        }
        sb.append("\\r\\n");
        return sb.toString();
    }

    public String revealAllCells()
    {
        if(numberTurnsPlayed == 0)
        {
            return new String("GAME NOT STARTED\\r\\n");
        }
        return convertGridToProtocol(true);
    }

    /**
     * Get the number of mines in the neighborhood of a cell.
     * @param x The x coordinate of the cell.
     * @param y The y coordinate of the cell.
     * @return The number of mines in the neighborhood of the cell.
     */
    private int getNumberOfAdjacentMines(int x, int y)
    {
        if(x < 0 || x >= gridSize || y < 0 || y >= gridSize)
        {
            System.out.println("Invalid coordinates.");
            return -1;
        }
        int numMines = 0;
        for(int i = x - 1; i <= x + 1; i++)
        {
            for(int j = y - 1; j <= y + 1; j++)
            {
                if(i >= 0 && i < gridSize && j >= 0 && j < gridSize)
                {
                    if(currentGrid[i][j].getValue() == Coordinate.BOMB)
                    {
                        numMines++;
                    }
                }
            }
        }
        return numMines;
    }

    /**
     * Place mines on the board.
     * @implNote The mines are placed randomly on the board.
     */
    private void placeMines()
    {
        for(int i = 0; i < numberMines; i++)
        {
            int x = (int)(Math.random() * gridSize);
            int y = (int)(Math.random() * gridSize);
            // If there is already a mine at this location, try again
            if(currentGrid[x][y].getValue() == Coordinate.BOMB)
            {
                i--;
            }
            else
            {
                currentGrid[x][y].setValue(Coordinate.BOMB);
            }
        }
    }

    /**
     * Print the board to the console.
     */
    public void printBoard()
    {
        for(int i = 0; i < gridSize; i++)
        {
            for(int j = 0; j < gridSize; j++)
            {
                System.out.print(currentGrid[i][j].getValue() + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Is the current board a win?
     * @return True if the current board is a win, false otherwise.
     */
    public boolean isWin()
    {
        for(int i = 0; i < gridSize; i++)
        {
            for(int j = 0; j < gridSize; j++)
            {
                if(currentGrid[i][j].getStatus() == Coordinate.Status.UNREVEALED)
                {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isLose()
    {
        for(int i = 0; i < gridSize; i++)
        {
            for(int j = 0; j < gridSize; j++)
            {
                if(currentGrid[i][j].getValue() == Coordinate.BOMB)
                {
                    if(currentGrid[i][j].getStatus() == Coordinate.Status.REVEALED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
