import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import authorization.Authorizer;
import authorization.User;

public class Mainframe implements Runnable
{
    private final int PORT;
    private boolean running;
    private ServerSocket serverSocket = null;
    private Authorizer authorizer = null;

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
        User client = null;

        BufferedReader in = null;
        PrintWriter out =  null;

        try
        {
            serverSocket = new ServerSocket(PORT);
            authorizer = Authorizer.getInstance();
            running = true;
            System.out.println(String.format("Server listening on port %s.", PORT));
            
            while (running)
            {
                clientSocket = serverSocket.accept();
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                System.out.println("A client is attempting to connect...");

                while (client == null)
                {
                    byte[] hash = in.readLine().getBytes();
                    System.out.println(hash);
                    client = authorizer.authorize(hash);
                    if (client == null) System.out.println("Failed to authenticate client.");
                }
                
                out.println(client.permissions);
                System.out.println(String.format("%s successfully connected.", client.toString()));
                
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                {
                    System.out.println(String.format("> %s", inputLine));
                }
                
                clientSocket.close();
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
