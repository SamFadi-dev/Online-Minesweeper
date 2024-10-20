import java.io.*;
import java.net.*;

/**
 * Minesweeper client.
 */
public class MinesweeperClient
{
    public static void main(String[] args) throws IOException
    {
        try
        {
            Socket clientSocket = new Socket("localhost", MinesweeperServer.PORT);
            if(clientSocket.isConnected())
            {
                System.out.println("Connected to server.");
                System.out.println("List Of Commands: \n"
                    + "1. FLAG x y\n"
                    + "2. TRY x y\n"
                    + "3. CHEAT\n"
                    + "4. QUIT\n");
            }
            InputStream inputServer = clientSocket.getInputStream();
            OutputStream outputClient = clientSocket.getOutputStream();
            BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));
            byte msg[] = new byte[1024];

            while(true)
            {
                // Send the user input to the server.
                String userInput = keyboardReader.readLine();
                userInput = userInput.concat("\r\n\r\n");
                outputClient.write(userInput.getBytes());
                outputClient.flush();

                // Receive the server's response.
                String receivedMessage = new String(msg, 0, inputServer.read(msg)).trim();

                // Exit the client if the server sends "GOODBYE" or GAME LOST/WON. 
                if(receivedMessage.contains("GOODBYE") || receivedMessage.contains("GAME LOST") 
                    || receivedMessage.contains("GAME WON"))
                {
                    System.out.println(receivedMessage);
                    break;
                }
                System.out.println(receivedMessage + "\n");
            }
            clientSocket.close();
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
}
