import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    private ArrayList<ConnectionHandler> connections;
    private ServerSocket server;
    private boolean running;
    private ExecutorService pool;

    public Server() {
        running = true;
        connections = new ArrayList<ConnectionHandler>();

    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(9999);
            pool = Executors.newCachedThreadPool();
            System.out.println("Listening on port " + server.getLocalPort());
            while (running) {
                Socket client = server.accept();
                var handler = new ConnectionHandler(client);
                connections.add(handler);
                pool.execute(handler);
            }

        } catch (IOException e) {
            shutdown();
        }

    }

    public void broadcast(String message) {
        connections.forEach(connectionHandler -> {
            if (connectionHandler != null) connectionHandler.sendMessage(message);
        });
    }

    public void shutdown() {
        running = false;
        if (!server.isClosed()) {
            try {
                server.close();
                for (ConnectionHandler ch : connections) {
                    ch.shutdown();
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Ignore
            }
        }
    }

    class ConnectionHandler implements Runnable {

        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;

        public ConnectionHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                out.println("What should I call you?");
                nickname = in.readLine();
                System.out.println(nickname + " connected");
                broadcast(nickname + " has joined the chat");
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/nick ")) {
                        String[] messageSplit = message.split(" ", 2);
                        if (messageSplit.length == 2) {
                            broadcast(nickname + " renamed themselves to " + messageSplit[1]);
                            System.out.println(nickname + " renamed themselves to " + messageSplit[1]);
                            nickname = messageSplit[1];
                            out.println("Name changed successfully");
                        } else {
                            out.println("No nickname was provided, try again.");
                        }
                    } else if (message.startsWith("/quit")) {
                        broadcast(nickname + " has better things to do with their life");
                        shutdown();
                    } else {
                        broadcast(nickname + " : " + message);
                    }
                }

            } catch (IOException e) {
                shutdown();
            }
        }

        public void sendMessage(String message) {
            out.println("message");
        }

        public void shutdown() {
            if (!client.isClosed()) {
                try {
                    in.close();
                    out.close();
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    // ignore
                }
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();


    }

}
