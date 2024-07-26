import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable {

    private ArrayList<ConnectionHandler> connections;

    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(9999);
            Socket client = server.accept();
            connections.add(new ConnectionHandler(client));

        } catch (IOException e) {
            // TODO: handle
        }

    }

    public void broadcast(String message) {
        connections.forEach(connectionHandler -> {
            if (connectionHandler != null) connectionHandler.sendMessage(message);
        });
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

                    } else if (message.startsWith("/quit")) {
                        // TODO: handle quit
                    } else {
                        broadcast(nickname + " : " + message);
                    }
                }

            } catch (IOException e) {
                // TODO: handle
            }
        }

        public void sendMessage(String message) {
            out.println("message");
        }
    }
}
