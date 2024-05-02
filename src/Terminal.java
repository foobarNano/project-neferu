import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import utility.Hasher;

public class Terminal implements Runnable
{
    private final int PORT;

    private String username = null;
    private boolean authenticated = false;

    private Socket socket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private Scanner scanner = null;

    public Terminal(int port)
    {
        this.PORT = port;
    }

    private int authenticate()
    {
        System.out.println("To cancel, simply leave login blank.");

        System.out.print("Login: ");
        username = scanner.nextLine().strip();
        
        if (username.isBlank()) return -2;
        
        out.println(username);

        System.out.print("Password: ");
        String hash = new String(Hasher.SHA3_256.digest(scanner.nextLine().strip()));
        
        out.println(hash);

        try
        {
            return Integer.parseInt(in.readLine());
        }
        catch (IOException e)
        {
            return -1;
        }
    }

    @Override
    public void run()
    {
        try
        {
            System.out.println("Attempting to establish connection...");

            socket = new Socket("localhost", PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            scanner = new Scanner(System.in);

            System.out.println("Connected. Attempting to authenticate...");

            while (!authenticated)
            {
                switch (authenticate())
                {
                    case 0:
                        System.out.println(String.format("Authenticated. Welcome, %s!", username));
                        authenticated = true;
                        break;

                    case -2:
                        System.out.println("Quitting...");
                        return;

                    default:
                        System.out.println("Unable to authenticate, please try again.");
                        break;
                }
            }
            
            String command;
            System.out.print("> ");
            while (!(command = scanner.nextLine()).isBlank())
            {
                out.println(command);

                String response = in.readLine();
                System.out.println(String.format("< %s", response));

                System.out.print("> ");
            }
        }
        catch (IOException e)
        {
            System.out.println("Unable to connect to the server.");
        }
        finally
        {
            try
            {
                if (out != null) out.close();
                if (in != null) in.close();
                if (socket != null) socket.close();
                if (scanner != null) scanner.close();
            }
            catch (IOException e) {}
        }
    }

    public static void main(String[] args)
    {
        new Terminal(10001).run();
    }
}
