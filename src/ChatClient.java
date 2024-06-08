

import java.io.*;
import java.net.*;

public class ChatClient {
    private String hostname;
    private int port;
    private String userName;

    public ChatClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);

            System.out.println("Połączono z serwerem");

            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();

        } catch (UnknownHostException ex) {
            System.out.println("Nieznany host: " + ex.getMessage());

        } catch (IOException ex) {
            System.out.println("Błąd we/wy: " + ex.getMessage());
        }

    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    String getUserName() {
        return this.userName;
    }

    public static void main(String[] args) {
        if (args.length < 2) return;

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        ChatClient client = new ChatClient(hostname, port);
        client.execute();
    }
}

class ReadThread extends Thread {
    private BufferedReader reader;
    private Socket socket;
    private ChatClient client;

    public ReadThread(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;

        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            System.out.println("Błąd we/wy: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {

        while (true) {
            try {
                String response = reader.readLine();
                System.out.println(response);

//                if (client.getUserName() != null) {
//                    System.out.print("[" + client.getUserName() + "]: ");
//                }

            } catch (IOException ex) {
                System.out.println("Błąd przy odczycie z serwera: " + ex.getMessage());
                ex.printStackTrace();
                break;
            }
        }
    }
}



