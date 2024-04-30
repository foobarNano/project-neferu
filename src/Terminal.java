import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Terminal implements Runnable
{
    private final int PORT;

    /**
     * Generates and prepares the client for running. Only connects to the server once the {@link #run()} method is called.
     * @param port The localhost port number the server is open on
     */
    public Terminal(int port)
    {
        this.PORT = port;
    }

    public void run()
    {
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        Scanner scanner = null;
        
        try
        {
            socket = new Socket("localhost", PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);
            
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
