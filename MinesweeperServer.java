import java.io.*;
import java.net.*;

public class MinesweeperServer
{
    public static final short PORT = 2377;
    private static final short BOARD_SIZE = 7;
    private static final short NUM_MINES = BOARD_SIZE;
    private char[][] currentBoard;
    private static final String QUIT_COMMAND = "QUIT";
    private static final String TRY_COMMAND = "TRY";
    private static final String FLAG_COMMAND = "FLAG";
    private static final String CHEAT_COMMAND = "CHEAT";

    public static void main(String[] args) throws IOException
    {
        MinesweeperServer server = new MinesweeperServer();
        server.createInitialBoard();
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);
        while(true)
        {
            Socket clientSocket = serverSocket.accept();
            // Check if the client connected successfully
            if(clientSocket.isConnected())
            {
                System.out.println("Client " + clientSocket.getPort() + " connected.");
            }
            else
            {
                System.out.println("Client failed to connect.");
                clientSocket.close();
                continue;
            }
            OutputStream outputServer = clientSocket.getOutputStream();
            InputStream inputClient = clientSocket.getInputStream();
            byte msg[] = new byte[1024];

            while (true) 
            {
                // Read the message from the client and act accordingly
                int len = inputClient.read(msg);
                String receivedMessage = new String(msg, 0, len).trim();
                if(!isClientInputValid(receivedMessage, outputServer))
                {
                    printWrongInputMessage(clientSocket);
                    continue;
                }
                if(isQuitCommand(receivedMessage))
                {
                    printDisconnectedMessage(clientSocket);
                    outputServer.write("GOODBYE".getBytes());
                    outputServer.flush();
                    // Close the client socket and break the loop
                    break;
                }
                else if(isCheatCommand(receivedMessage))
                {
                    outputServer.write(server.convertBoardToString().getBytes());
                    outputServer.flush();
                    continue;
                }
                else if(isFlagCommand(receivedMessage))
                {
                    outputServer.write("FLAG".getBytes());
                    outputServer.flush();
                    continue;
                }
                else if(isTryCommand(receivedMessage))
                {
                    outputServer.write("TRY".getBytes());
                    outputServer.flush();
                    continue;
                }
                // Wrong command => restart the loop
                else
                {
                    outputServer.write("WRONG".getBytes());
                    outputServer.flush();
                    printWrongInputMessage(clientSocket);
                    continue;
                }
            }
            clientSocket.close();
        }
    }

    /**
     * Print a message to the console indicating that the client sent an invalid command.
     * @param clientSocket The client socket that sent the invalid command.
     */
    private static void printWrongInputMessage(Socket clientSocket)
    {
        System.out.println("Client " + clientSocket.getPort() + " sent an invalid command.");
    }

    /**
     * Print a message to the console indicating that the client disconnected.
     * @param clientSocket The client socket that disconnected.
     */
    private static void printDisconnectedMessage(Socket clientSocket)
    {
        System.out.println("Client " + clientSocket.getPort() + " disconnected.");
    }

    /**
     * Check if the input from the client is a valid command.
     * @param input The input from the client.
     * @param outputServer The output stream to the client.
     * @return True if the input is a valid command, false otherwise.
     */
    private static boolean isTryCommand(String input)
    {
        return input.startsWith(TRY_COMMAND);
    }

    /**
     * Check if the input from the client is a valid command.
     * @param input The input from the client.
     * @param outputServer The output stream to the client.
     * @return True if the input is a valid command, false otherwise.
     */
    private static boolean isFlagCommand(String input)
    {
        return input.startsWith(FLAG_COMMAND);
    }

    /**
     * Check if the input from the client is a valid command.
     * @param input The input from the client.
     * @return True if the input is a valid command, false otherwise.
     */
    private static boolean isQuitCommand(String input)
    {
        return input.equals(QUIT_COMMAND);
    }

    /**
     * Check if the input from the client is a valid command.
     * @param input The input from the client.
     * @return True if the input is a valid command, false otherwise.
     */
    private static boolean isCheatCommand(String input)
    {
        return input.equals(CHEAT_COMMAND);
    }

    /**
     * Check if the input from the client is a valid command.
     * @param input The input from the client.
     * @param outputServer The output stream to the client.
     * @return True if the input is a valid command, false otherwise.
     */
    private static boolean isClientInputValid(String input, OutputStream outputServer) throws IOException
    {
        if(!isQuitCommand(input)
            && !isTryCommand(input)
            && !isFlagCommand(input)
            && !isCheatCommand(input))
        {
            outputServer.write("WRONG".getBytes());
            return false;
        }
        return true;
    }

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

    private String convertBoardToString()
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < BOARD_SIZE; i++)
        {
            for(int j = 0; j < BOARD_SIZE; j++)
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
