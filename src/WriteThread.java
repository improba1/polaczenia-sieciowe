import java.io.*;
import java.net.Socket;

public class WriteThread extends Thread {
    private PrintWriter writer;
    private Socket socket;
    private ChatClient client;

    public WriteThread(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Błąd we/wy: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.print("Podaj login: ");
            String userName = consoleReader.readLine();
            client.setUserName(userName);
            writer.println(userName);

            String text;

            do {
                text = consoleReader.readLine();
                writer.println(text);

            } while (!text.equalsIgnoreCase("quit"));

            socket.close();

        } catch (IOException ex) {
            System.out.println("Błąd we/wy: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
