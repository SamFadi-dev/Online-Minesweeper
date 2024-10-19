public class Grid
{
    private short gridSize = 7;
    private short numberMines = gridSize;
    private char[][] currentBoard;

    public Grid(short gridSize)
    {
        this.gridSize = gridSize;
        this.numberMines = gridSize;
        createInitialBoard();
    }

    public short getBoardSize()
    {
        return gridSize;
    }

    public void createInitialBoard()
    {
        char[][] board = new char[gridSize][gridSize];
        for(int i = 0; i < gridSize; i++)
        {
            for(int j = 0; j < gridSize; j++)
            {
                board[i][j] = '#';
            }
        }
        currentBoard = board;
    }

    public String convertGridToString()
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < gridSize; i++)
        {
            for(int j = 0; j < gridSize; j++)
            {
                sb.append(currentBoard[i][j]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private int getNumberOfAdjacentMines(int x, int y)
    {
        int numMines = 0;
        for(int i = x - 1; i <= x + 1; i++)
        {
            for(int j = y - 1; j <= y + 1; j++)
            {
                if(i >= 0 && i < gridSize && j >= 0 && j < gridSize)
                {
                    if(currentBoard[i][j] == 'B')
                    {
                        numMines++;
                    }
                }
            }
        }
        return numMines;
    }

    private void placeMines()
    {
        for(int i = 0; i < numberMines; i++)
        {
            int x = (int)(Math.random() * gridSize);
            int y = (int)(Math.random() * gridSize);
            // If there is already a mine at this location, try again
            if(currentBoard[x][y] == 'B')
            {
                i--;
            }
            else
            {
                currentBoard[x][y] = 'B';
            }
        }
    }

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

    private boolean isWin()
    {
        for(int i = 0; i < gridSize; i++)
        {
            for(int j = 0; j < gridSize; j++)
            {
                if(currentBoard[i][j] == '#')
                {
                    return false;
                }
            }
        }
        return true;
    }

}
