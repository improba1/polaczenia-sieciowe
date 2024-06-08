import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Set<String> userNames = new HashSet<>();
    private static Set<UserThread> userThreads = new HashSet<>();

    public static void main(String[] args) {
        int port = 12345;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serwer czatu działa na porcie " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nowy użytkownik połączony");

                UserThread newUser = new UserThread(socket);
                userThreads.add(newUser);
                newUser.start();
            }

        } catch (IOException ex) {
            System.out.println("Błąd w serwerze: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    static void broadcast(String message, UserThread excludeUser) {
        for (UserThread aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }

    static void addUserName(String userName) {
        userNames.add(userName);
    }

    static void removeUser(String userName, UserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("Użytkownik " + userName + " opuścił czat");
        }
    }

    static Set<String> getUserNames() {
        return userNames;
    }

    static boolean hasUsers() {
        return !userNames.isEmpty();
    }
}

class UserThread extends Thread {
    private Socket socket;
    private ChatServer server;
    private PrintWriter writer;

    public UserThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            //printUsers();

            String userName = reader.readLine();
            server.addUserName(userName);

            String serverMessage = "Nowy użytkownik dołączył: " + userName;
            server.broadcast(serverMessage, this);

            String clientMessage;

            do {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                server.broadcast(serverMessage, this);

            } while (!clientMessage.equalsIgnoreCase("quit"));

            server.removeUser(userName, this);
            socket.close();

            serverMessage = userName + " opuścił czat.";
            server.broadcast(serverMessage, this);

        } catch (IOException ex) {
            System.out.println("Błąd w użytkowniku: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

//    void printUsers() {
//        if (server.hasUsers()) {
//            writer.println("Aktualnie zalogowani użytkownicy: " + server.getUserNames()+ "\n");
//        } else {
//            writer.println("Brak innych zalogowanych użytkowników");
//        }
//    }

    void sendMessage(String message) {
        writer.println(message);
    }
}
