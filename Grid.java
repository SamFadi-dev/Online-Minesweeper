import java.util.*;

public class Grid
{
    private short gridSize = 7;
    private short numberMines = gridSize;
    private short numberTurnsPlayed = 0;
    private Coordinate[][] currentBoard;

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
        currentBoard = board;
    }

    /**
     * Convert the grid to a string.
     * @return The grid as a string.
     */
    public String convertGridToString()
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < gridSize; i++)
        {
            for(int j = 0; j < gridSize; j++)
            {
                sb.append(currentBoard[i][j].getCoordinateValue());
            }
            sb.append("\n");
        }
        return sb.toString();
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
                    if(currentBoard[i][j].getCoordinateValue() == 'B')
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
            if(currentBoard[x][y].getCoordinateValue() == 'B')
            {
                i--;
            }
            else
            {
                currentBoard[x][y].setCoordinateValue(Coordinate.BOMB);
            }
        }
    }

    /**
     * Print the board to the console.
     */
    private void printBoard()
    {
        for(int i = 0; i < gridSize; i++)
        {
            for(int j = 0; j < gridSize; j++)
            {
                System.out.print(currentBoard[i][j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * Is the current board a win?
     * @return True if the current board is a win, false otherwise.
     */
    private boolean isWin()
    {
        for(int i = 0; i < gridSize; i++)
        {
            for(int j = 0; j < gridSize; j++)
            {
                if(currentBoard[i][j].getCoordinateValue() == Coordinate.UNREVEALED)
                {
                    return false;
                }
            }
        }
        return true;
    }

}
