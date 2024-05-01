import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Mainframe implements Runnable
{
    private final int PORT;
    private final int LIMIT;
    private final String FORMAT;

    private int count = 0;
    private ServerSocket serverSocket = null;
    private ExecutorService executor = null;
    private boolean running = false;

    public Mainframe(int port, int limit, String format)
    {
        this.PORT = port;
        this.LIMIT = limit;
        this.FORMAT = format;
    }

    public Mainframe(int port, int limit)
    {
        this(port, limit, "%s > %s");
    }

    public Mainframe(int port)
    {
        this(port, 10, "%s > %s");
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
                String name = String.format("Client %s", ++count);

                executor.execute(new ClientHandler(name, clientSocket, FORMAT));
                System.out.println(String.format("[!] %s has connected :3", name));
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
        private final String name;
        private final Socket clientSocket;
        private final String format;

        public ClientHandler(String name, Socket clientSocket, String format)
        {
            this.name = name;
            this.clientSocket = clientSocket;
            this.format = format;
        }

        @Override
        public void run()
        {
            BufferedReader in = null;
            PrintWriter out = null;

            try
            {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                String message;
                while ((message = in.readLine()) != null)
                {
                    System.out.println(String.format(format, name, message));
                }

                in.close();
                out.close();
                clientSocket.close();
            }
            catch (IOException e) { e.printStackTrace(); }
            finally
            {
                try
                {
                    if (in != null) in.close();
                    if (out != null) out.close();
                }
                catch (IOException e) {}
            }

            System.out.println(String.format("[!] %s has disconnected :<", name));
        }
    }

    public static void main(String[] args)
    {
        new Mainframe(10001).run();
    }
}
