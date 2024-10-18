public class MinesweeperServer
{
    private static final short BOARD_SIZE = 7;
    private static final short NUM_MINES = BOARD_SIZE;
    private static final short PORT = 2377;
    private char[][] currentBoard;

    private void createInitialBoard()
    {
        char[][] board = new char[BOARD_SIZE][BOARD_SIZE];
        for(int i = 0; i < BOARD_SIZE; i++)
        {
            for(int j = 0; j < BOARD_SIZE; j++)
            {
                board[i][j] = '#';
            }
        }
        currentBoard = board;
    }

    private int getNumberOfAdjacentMines(int x, int y)
    {
        int numMines = 0;
        for(int i = x - 1; i <= x + 1; i++)
        {
            for(int j = y - 1; j <= y + 1; j++)
            {
                if(i >= 0 && i < BOARD_SIZE && j >= 0 && j < BOARD_SIZE)
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
        for(int i = 0; i < NUM_MINES; i++)
        {
            int x = (int)(Math.random() * BOARD_SIZE);
            int y = (int)(Math.random() * BOARD_SIZE);
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
        for(int i = 0; i < BOARD_SIZE; i++)
        {
            for(int j = 0; j < BOARD_SIZE; j++)
            {
                System.out.print(currentBoard[i][j] + " ");
            }
            System.out.println();
        }
    }

    private boolean isWin()
    {
        for(int i = 0; i < BOARD_SIZE; i++)
        {
            for(int j = 0; j < BOARD_SIZE; j++)
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
