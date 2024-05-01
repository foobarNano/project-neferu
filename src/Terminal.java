import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Terminal implements Runnable
{
    private final int PORT;

    private Socket socket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private Scanner scanner = null;

    public Terminal(int port)
    {
        this.PORT = port;
    }

    @Override
    public void run()
    {
        try
        {
            socket = new Socket("localhost", PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            scanner = new Scanner(System.in);
            
            String message;
            System.out.print("> ");
            while (!(message = scanner.nextLine()).isBlank())
            {
                out.println(message);
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
