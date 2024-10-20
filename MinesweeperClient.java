import java.io.*;
import java.net.*;

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
            }
            InputStream inputServer = clientSocket.getInputStream();
            OutputStream outputClient = clientSocket.getOutputStream();
            BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));
            byte msg[] = new byte[1024];

            while(true)
            {
                String userInput = keyboardReader.readLine();
                outputClient.write(userInput.getBytes());
                String receivedMessage = new String(msg, 0, inputServer.read(msg)).trim();
                if(receivedMessage.equals("GOODBYE")) break;
                System.out.println(receivedMessage);
                outputClient.flush();
            }
            System.out.println("Disconnected from server.");
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
