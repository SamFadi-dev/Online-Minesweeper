import java.io.*;
import java.net.*;

public class MinesweeperClient
{
    public static void main(String[] args) throws IOException
    {
        Socket clientSocket = new Socket("localhost", MinesweeperServer.PORT);
        if(clientSocket.isConnected())
        {
            System.out.println("Connected to server.");
        }
        else
        {
            System.out.println("Failed to connect to server.");
            clientSocket.close();
            return;
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
}
