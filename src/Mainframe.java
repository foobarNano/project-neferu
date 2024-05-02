import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import server.Permission;
import server.ServerFunction;
import ship.ShipSector;
import ship.sensor.Sensor;
import ship.sensor.Thermometer;
import utility.Constants;
import utility.Hasher;

@SuppressWarnings("rawtypes")
public class Mainframe implements Runnable
{
    private final int PORT;
    private final int LIMIT;

    private ServerSocket serverSocket = null;
    private ExecutorService executor = null;
    private boolean running = false;

    private final HashMap<String, ServerFunction> functions;
    private final ArrayList<Sensor> sensors;

    public Mainframe(int port, int limit)
    {
        this.PORT = port;
        this.LIMIT = limit;
        functions = new HashMap<>();
        sensors = new ArrayList<>();

        // Declare server functions
        functions.put("yell", new ServerFunction(
            "Yells the phrase passed as argument.",
            Permission.None
            ){
                @Override
                public String apply(String[] args)
                {
                    if (args.length < 2) return "Proper usage: yell [STUFF TO YELL]";
                    
                    String joined = String.join(" ", args);
                    return String.format("%s!", joined.substring(5).toUpperCase());
                }
            }
        );
        functions.put("read", new ServerFunction(
            "Reads the current readings of a sensor.",
            Permission.None
            ){
                @Override
                public String apply(String[] args)
                {
                    if (args.length < 2) return "Proper usage: read {'list' | SECTOR | SECTOR SENSOR}";
                    
                    StringBuilder builder = new StringBuilder();

                    if (args[1].equals("list"))
                    {
                        for (ShipSector s : ShipSector.values())
                        {
                            builder.append(s.name()).append(":\t");

                            for (Sensor sr : sensors)
                            {
                                if (sr.SECTOR == s)
                                {
                                    builder.append(' ').append(sr.NAME);
                                }
                            }

                            builder.append('\n');
                        }
                        builder.setLength(builder.length()-1);
                    }
                    else
                    {
                        try
                        {
                            ShipSector sector = ShipSector.valueOf(args[1].toUpperCase());

                            if (args.length == 2)
                            {
                                for (Sensor sr : sensors)
                                {
                                    if (sr.SECTOR == sector) builder.append(sr.NAME).append(" - ").append(sr.read()).append('\n');
                                }
                                if (builder.length() != 0) builder.setLength(builder.length()-1);
                            }
                            else
                            {
                                Sensor sensor = null;

                                for (Sensor sr : sensors)
                                {
                                    if (sr.NAME.equals(args[2].toUpperCase()) && sr.SECTOR == sector)
                                    {
                                        sensor = sr;
                                        break;
                                    }
                                }

                                if (sensor == null) return String.format("No sensor '%s' in sector %s", args[2], sector.name());

                                builder.append(sensor.read());
                            }
                        }
                        catch (IllegalArgumentException e)
                        {
                            return "Bad sector name. See 'read list' for available sectors.";
                        }
                    }

                    if (builder.length() == 0) builder.append("No sensors found.");
                    return builder.toString();
                }
            }
        );

        // Declare sensors
        sensors.add(new Thermometer("AIR", ShipSector.GREENHOUSE));
        sensors.add(new Thermometer("SOIL", ShipSector.GREENHOUSE));
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
                executor.execute(new ClientHandler(this, clientSocket));
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

    private String handle(String command)
    {
        String[] args = command.split(" ");
        ServerFunction function = functions.get(args[0]);

        if (function == null) return String.format("Unknown command: '%s'", args[0]);
        return function.apply(args);
    }

    private static class ClientHandler implements Runnable
    {
        private final Mainframe parent;
        private final Socket clientSocket;

        private String username = "Unknown";
        private boolean authenticated = false;

        private BufferedReader in = null;
        private PrintWriter out = null;
        
        public ClientHandler(Mainframe parent, Socket clientSocket)
        {
            this.parent = parent;
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

                    String response = parent.handle(command);

                    System.out.println(String.format("> %s", response));
                    out.println(response);
                    out.println();
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

            System.out.println(String.format("%s disconnected.", username));
        }
    }

    public static void main(String[] args)
    {
        new Mainframe(10001).run();
    }
}
