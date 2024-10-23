import java.io.*;
import java.net.*;

public class MinesweeperClient
{
    public static void main(String[] args) throws IOException
    {
        try
        {
            @SuppressWarnings("resource")
            Socket clientSocket = new Socket("localhost", MinesweeperServer.SERVER_PORT);
            if(clientSocket.isConnected())
            {
                System.out.println("Connected to server.");
                System.out.println(printCommands());
            }
            InputStream inputServer = clientSocket.getInputStream();
            OutputStream outputClient = clientSocket.getOutputStream();
            BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));
            byte msg[] = new byte[MinesweeperServer.MSG_SIZE];

            while(true)
            {
                // Send the user input to the server.
                String userInput = keyboardReader.readLine();
                if(userInput.isEmpty())
                {
                    System.out.println("Please enter a command.");
                    continue;
                }
                userInput = userInput.concat("\r\n\r\n");
                outputClient.write(userInput.getBytes());

                // Receive the server's response.
                String receivedMessage = new String(msg, 0, inputServer.read(msg)).trim();

                System.out.println(receivedMessage + "\n");
            }
        }
        catch(UnknownHostException e)
        {
            System.out.println("Unknown host.");
        }
        catch(IOException e)
        {
            System.out.println("I/O error. Probably timeout and/or disconnected.");
        }
    }

    private static String printCommands()
    {
        return "\nList Of Commands: \n"
            + "- FLAG x y\n"
            + "- TRY x y\n"
            + "- CHEAT\n"
            + "- QUIT\n";
    }
}
