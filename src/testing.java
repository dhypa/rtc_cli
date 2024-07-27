import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class testing {

    public static void main(String[] args) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true){
            try {
                String line = reader.readLine();
                System.out.println(line);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
