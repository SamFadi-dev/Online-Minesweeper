import java.io.*;
import java.net.*;

public class MinesweeperServer
{
    public static final short PORT = 2377;
    private static final String QUIT_COMMAND = "QUIT";
    private static final String TRY_COMMAND = "TRY";
    private static final String FLAG_COMMAND = "FLAG";
    private static final String CHEAT_COMMAND = "CHEAT";

    public static void main(String[] args) throws IOException
    {
        Grid grid = new Grid((short) 7);
        grid.createInitialBoard();
        try(ServerSocket serverSocket = new ServerSocket(PORT))
        {
            System.out.println("New server socket started on port " + PORT);
            while (true)
            {
                handleClientConnection(grid, serverSocket);
            }
        }
    }

    /**
     * Handle the connection between the server and the client.
     * @param server The MinesweeperServer object.
     * @param serverSocket The server socket.
     * @throws IOException If an I/O error occurs.
     */
    private static void handleClientConnection(Grid grid, ServerSocket serverSocket)
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
            processClientRequests(grid, clientSocket);
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
    private static void processClientRequests(Grid grid, Socket clientSocket) throws IOException
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
                    handleCheatCommand(grid, outputServer);
                } 
                else if(isFlagCommand(receivedMessage))
                {
                    handleFlagCommand(outputServer, receivedMessage, grid);
                } 
                else if(isTryCommand(receivedMessage))
                {
                    handleTryCommand(outputServer, receivedMessage, grid);
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
    private static void handleCheatCommand(Grid grid, OutputStream outputServer)
        throws IOException
    {
        outputServer.write(grid.convertGridToString().getBytes());
        outputServer.flush();
    }
    
    /**
     * Handle the "FLAG" command from the client.
     * @param outputServer The output stream to the client.
     * @throws IOException If an I/O error occurs.
     */
    private static void handleFlagCommand(OutputStream outputServer, String input, Grid grid) throws IOException
    {
        // Write the updated grid to the client if the coordinates are valid
        if(areCorrectCoordinates(grid, input))
        {
            outputServer.write(grid.convertGridToString().getBytes());
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
    private static void handleTryCommand(OutputStream outputServer, String input, Grid grid) throws IOException
    {
        // Write the updated grid to the client if the coordinates are valid
        if(areCorrectCoordinates(grid, input))
        {
            outputServer.write(grid.convertGridToString().getBytes());
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
     * @implNote Debugging purposes only.
     */
    private static void printWrongInputMessage(Socket clientSocket)
    {
        System.out.println("Client " + clientSocket.getPort() + " sent an invalid command.");
    }

    /**
     * Print a message to the console indicating that the client disconnected.
     * @param clientSocket The client socket that disconnected.
     * @implNote Debugging purposes only.
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
    private static boolean areCorrectCoordinates(Grid grid, String input)
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
            if(x < 0 || x >= grid.getBoardSize() || y < 0 || y >= grid.getBoardSize())
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
}
