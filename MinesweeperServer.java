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
        try(ServerSocket serverSocket = new ServerSocket(PORT))
        {
            System.out.println("New server socket started on port " + PORT);
            while (true)
            {
                handleClientConnection(server, serverSocket);
            }
        }
    }

    /**
     * Handle the connection between the server and the client.
     * @param server The MinesweeperServer object.
     * @param serverSocket The server socket.
     * @throws IOException If an I/O error occurs.
     */
    private static void handleClientConnection(MinesweeperServer server, ServerSocket serverSocket)
    {
        try
        {
            // Check if the client is connected
            Socket clientSocket = serverSocket.accept();
            if(clientSocket.isConnected())
            {
                System.out.println("Client " + clientSocket.getPort() + " connected.");
            }
            else
            {
                System.out.println("Client failed to connect.");
                clientSocket.close();
                return;
            }
            // Process the client's requests (e.g. try, flag, cheat, quit)
            processClientRequests(server, clientSocket);
        } 
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Process the client's requests.
     * @param server The MinesweeperServer object.
     * @param clientSocket The client socket.
     * @throws IOException If an I/O error occurs.
     */
    private static void processClientRequests(MinesweeperServer server, Socket clientSocket) throws IOException
    {
        try (OutputStream outputServer = clientSocket.getOutputStream();
             InputStream inputClient = clientSocket.getInputStream())
        {
            byte[] msg = new byte[1024];
            // Loop until the client sends a "QUIT" command
            while(true)
            {
                int len = inputClient.read(msg);
                String receivedMessage = new String(msg, 0, len).trim();
                if(isQuitCommand(receivedMessage))
                {
                    handleQuitCommand(clientSocket, outputServer);
                    break;
                } 
                else if(isCheatCommand(receivedMessage))
                {
                    handleCheatCommand(server, outputServer);
                } 
                else if(isFlagCommand(receivedMessage))
                {
                    handleFlagCommand(outputServer, receivedMessage);
                } 
                else if(isTryCommand(receivedMessage))
                {
                    handleTryCommand(outputServer, receivedMessage);
                } 
                else 
                {
                    handleWrongCommand(clientSocket, outputServer);
                }
            }
        }
    }

    /**
     * Handle the "QUIT" command from the client.
     * @param clientSocket The client socket.
     * @param outputServer The output stream to the client.
     * @throws IOException If an I/O error occurs.
     */
    private static void handleQuitCommand(Socket clientSocket, OutputStream outputServer)
        throws IOException
    {
        printDisconnectedMessage(clientSocket);
        outputServer.write("GOODBYE".getBytes());
        outputServer.flush();
        clientSocket.close();
    }
    
    /**
     * Handle the "CHEAT" command from the client.
     * @param server The MinesweeperServer object.
     * @param outputServer The output stream to the client.
     * @throws IOException If an I/O error occurs.
     */
    private static void handleCheatCommand(MinesweeperServer server, OutputStream outputServer)
        throws IOException
    {
        outputServer.write(server.convertBoardToString().getBytes());
        outputServer.flush();
    }
    
    /**
     * Handle the "FLAG" command from the client.
     * @param outputServer The output stream to the client.
     * @throws IOException If an I/O error occurs.
     */
    private static void handleFlagCommand(OutputStream outputServer, String input) throws IOException
    {
        // Write the updated grid to the client if the coordinates are valid
        if(areCorrectCoordinates(input))
        {
            outputServer.write("FLAG".getBytes());
            outputServer.flush();
        }
        else
        {
            outputServer.write("INVALID RANGE".getBytes());
            outputServer.flush();
        }
    }
    
    /**
     * Handle the "TRY" command from the client.
     * @param outputServer The output stream to the client.
     * @throws IOException If an I/O error occurs.
     */
    private static void handleTryCommand(OutputStream outputServer, String input) throws IOException
    {
        // Write the updated grid to the client if the coordinates are valid
        if(areCorrectCoordinates(input))
        {
            outputServer.write("TRY".getBytes());
            outputServer.flush();
        }
        else
        {
            outputServer.write("INVALID RANGE".getBytes());
            outputServer.flush();
        }
    }
    
    /**
     * Handle an invalid command from the client.
     * @param clientSocket The client socket.
     * @param outputServer The output stream to the client.
     * @throws IOException If an I/O error occurs.
     */
    private static void handleWrongCommand(Socket clientSocket, OutputStream outputServer) 
        throws IOException
    {
        outputServer.write("WRONG".getBytes());
        outputServer.flush();
        printWrongInputMessage(clientSocket);
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
     * Check if the input is a TRY command.
     * @param input The input from the client.
     * @param outputServer The output stream to the client.
     * @return True if the input is a valid command, false otherwise.
     */
    private static boolean isTryCommand(String input)
    {
        return input.startsWith(TRY_COMMAND);
    }

    /**
     * Check if the input is a FLAG command.
     * @param input The input from the client.
     * @param outputServer The output stream to the client.
     * @return True if the input is a valid command, false otherwise.
     */
    private static boolean isFlagCommand(String input)
    {        
        return input.startsWith(FLAG_COMMAND);
    }

    /**
     * Check if the coordinates from the client are valid.
     * @param input The input from the client.
     * @param outputServer The output stream to the client.
     * @return True if the input is a valid command, false otherwise.
     */
    private static boolean areCorrectCoordinates(String input)
    {
        // Divide the input into three parts: the command,
        // the x coordinate, and the y coordinate
        String[] parts = input.split(" ");
        if (parts.length == 3)
        {
            if(!isNumeric(parts[1]) || !isNumeric(parts[2]))
            {
                return false;
            }
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            if(x < 0 || x >= BOARD_SIZE || y < 0 || y >= BOARD_SIZE)
            {
                return false;
            }  
        }
        else
        {
            return false;
        }
        return true;
    }

    /**
     * Check if the input string is a number.
     * @param str The input string.
     * @return True if the input is a number, false otherwise.
     */
    private static boolean isNumeric(String str)
    {
        for(char c : str.toCharArray())
        {
            if(!Character.isDigit(c))
            {
                return false;
            }
        }
        return true;
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
