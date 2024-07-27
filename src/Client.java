import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.Buffer;

public class Client implements Runnable {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean running;

    public Client(){

    }
    @Override
    public void run() {
        try {
            running = true;
            client = new Socket("localhost", 9999);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            InputHandler inHandler = new InputHandler();

            Thread t = new Thread(inHandler);
            t.start();

            String inMessage;
            while ((inMessage = in.readLine()) != null){
                System.out.println(inMessage);
            }


        } catch (Exception e) {
            shutdown();
        }
    }

    public void shutdown(){
        running = false;
        try {
            in.close();
            out.close();
            if (!client.isClosed()){
                client.close();
            }

        } catch (IOException e) {
            // ignore
            e.printStackTrace();
        }
    }

    class InputHandler implements Runnable {

        @Override
        public void run() {
            try {

                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while (running) {
                    String message = inReader.readLine();

                    if (message.startsWith("/quit")){
                        out.println(message);
                        inReader.close();
                        shutdown();
                    } else {
                        if (!message.isEmpty()) out.println(message);
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

}
