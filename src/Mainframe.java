import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import utility.Constants;
import utility.Hasher;

public class Mainframe implements Runnable
{
    private final int PORT;
    private final int LIMIT;

    private ServerSocket serverSocket = null;
    private ExecutorService executor = null;
    private boolean running = false;

    public Mainframe(int port, int limit)
    {
        this.PORT = port;
        this.LIMIT = limit;
    }

    public Mainframe(int port)
    {
        this(port, 10);
    }

    @Override
    public void run()
    {
        try
        {
            serverSocket = new ServerSocket(PORT);
            executor = Executors.newFixedThreadPool(LIMIT);
            running = true;

            System.out.println(String.format("Server listening on port %s...", PORT));

            while (running)
            {
                Socket clientSocket = serverSocket.accept();
                executor.execute(new ClientHandler(clientSocket));
            }
        }
        catch (IOException e) { e.printStackTrace(); }
        finally
        {
            if (serverSocket != null)
            {
                try
                {
                    serverSocket.close();
                }
                catch (IOException e) { e.printStackTrace(); }
            }
        }
    }

    private static class ClientHandler implements Runnable
    {
        private final Socket clientSocket;

        private String username = "Unknown";
        private boolean authenticated = false;

        private BufferedReader in = null;
        private PrintWriter out = null;
        
        public ClientHandler(Socket clientSocket)
        {
            this.clientSocket = clientSocket;
        }

        private void authenticate()
        {
            try
            {
                String login = in.readLine();

                if (login == null)
                {
                    clientSocket.close();
                    return;
                }

                login = login.strip();
                String hash = in.readLine().strip();
                String double_hash = new String(Hasher.SHA3_256.digest(hash));

                if (!Constants.USERS.containsKey(login)) throw new IOException("No such user.");

                if (Constants.USERS.get(login).equals(double_hash))
                {
                    out.println(0);

                    username = login;
                    authenticated = true;
                    return;
                }
            }
            catch (IOException e) {}
            
            System.out.println("Failed to authenticate.");
            out.println(-1);
        }

        @Override
        public void run()
        {
            try
            {
                System.out.println("A client is attempting to connect...");

                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                System.out.println("Connected. Attempting to authenticate...");

                while (!authenticated)
                {
                    if (clientSocket.isClosed()) throw new IOException("Connection lost.");
                    authenticate();
                }

                System.out.println(String.format("Authenticated. %s connected.", username));

                String command;
                while ((command = in.readLine()) != null)
                {
                    System.out.println(String.format("< %s", command));
                    System.out.println(String.format("> %s", command));
                    out.println(command);
                }

                in.close();
                out.close();
                clientSocket.close();
            }
            catch (IOException e)
            {
                System.out.println("Connection lost.");
            }
            finally
            {
                try
                {
                    if (in != null) in.close();
                    if (out != null) out.close();
                }
                catch (IOException e) {}
            }

            System.out.println("Connection closed.");
        }
    }

    public static void main(String[] args)
    {
        new Mainframe(10001).run();
    }
}
