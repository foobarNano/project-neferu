import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import utility.Hasher;
import utility.Serializer;

public class Terminal implements Runnable
{
    private final int PORT;
    private Socket socket = null;

    private BufferedReader in = null;
    private PrintWriter out = null;
    private Scanner scanner = null;

    /**
     * Generates and prepares the client for running. Only connects to the server once the {@link #run()} method is called.
     * @param port The localhost port number the server is open on
     */
    public Terminal(int port)
    {
        this.PORT = port;
    }

    private boolean authenticate()
    {
        String word1 = "neferu";
        String word2 = null;
        String word3 = null;

        System.out.print("Password: ");
        word2 = scanner.nextLine();

        word3  = Serializer.read_object("auth.key");

        String phrase = String.format("%s%s%s", word1, word2, word3);
        byte[] hash = Hasher.SHA3_256.digest(phrase);

        System.out.println(hash);
        out.println(hash);

        try
        {
            return in.read() == 1;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        return false;
    }

    public void run()
    {
        try
        {
            System.out.println(String.format("Attempting to establish connection on port %s...", PORT));
            socket = new Socket("localhost", PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);

            while (!authenticate())
            {
                System.out.println("Authentication error, please try again.");
            }
            
            String message;
            do
            {
                System.out.print("< ");
                message = scanner.nextLine();
                out.println(message);
            }
            while (!message.strip().isBlank());
        }
        catch (IOException e) { e.printStackTrace(); }
        finally
        {
            try
            {
                if (socket != null) socket.close();
                if (out != null) out.close();
                if (in != null) in.close();
                if (scanner != null) scanner.close();
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    }

    public static void main(String[] args)
    {
        int port = 10001;

        new Terminal(port).run();
    }
}
