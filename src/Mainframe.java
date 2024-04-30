import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Mainframe implements Runnable
{
    private final int PORT;
    private boolean running;
    private ServerSocket serverSocket = null;

    /**
     * Generates and prepares the server for running. Only begins work once the {@link #run()} method is called.
     * @param port The localhost port number to use
     */
    public Mainframe(int port)
    {
        this.PORT = port;
        this.running = false;
    }

    /**
     * Opens the server socket for connections and handles realtime communication.
     */
    public void run()
    {
        Socket clientSocket = null;
        BufferedReader in = null;
        PrintWriter out =  null;

        try
        {
            serverSocket = new ServerSocket(PORT);
            running = true;
            System.out.println(String.format("Server listening on port %s.", PORT));
            
            while (running)
            {
                clientSocket = serverSocket.accept();
                System.out.println("A client has connected.");
                
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                {
                    System.out.println(String.format("> %s", inputLine));
                }
                
                clientSocket.close();
                running = false;
            }
        }
        catch (IOException e) { e.printStackTrace(); }
        finally
        {
            try
            {
                if (out != null) out.close();
                if (in != null) in.close();
                if (serverSocket != null) serverSocket.close();
            }
            catch (IOException e) { e.printStackTrace(); }
            running = false;
        }
    }

    public static void main(String[] args)
    {

        int port = 10001;
        
        if (args.length != 0)
        {
            try { port = Integer.parseInt(args[0]); }
            catch (NumberFormatException e) {}
        }

        new Mainframe(port).run();
    }
}
