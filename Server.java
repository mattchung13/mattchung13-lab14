import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.time.*;
import java.util.*;

public class Server {
    private int port;
    private ServerSocket serverSocket;
    private List<ClientHandler> connections = new ArrayList<>();
    private ArrayList<LocalDateTime> connectedTimes = new ArrayList<>();

    public Server(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(port);
    }

    public void serve(int clientCount) {
        for (int i = 0; i < clientCount; i++) {
            try {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String handshake = in.readLine();
                if (!"12345".equals(handshake)) {
                    out.println("couldn't handshake");
                    clientSocket.close();
                    continue;
                }

                LocalDateTime now = LocalDateTime.now();
                connectedTimes.add(now);

                ClientHandler handler = new ClientHandler(clientSocket, now);
                connections.add(handler);
                new Thread(handler).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        try {
            for (ClientHandler handler : connections) {
                handler.close();
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<LocalDateTime> getConnectedTimes() {
        ArrayList<LocalDateTime> sorted = new ArrayList<>(connectedTimes);
        Collections.sort(sorted);
        return sorted;
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private LocalDateTime connectTime;

        public ClientHandler(Socket socket, LocalDateTime time) throws IOException {
            this.socket = socket;
            this.connectTime = time;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        }

        public void run() {
            try {
                String line = in.readLine();
                long num;
                try {
                    num = Long.parseLong(line);
                } catch (NumberFormatException e) {
                    out.println("There was an exception on the server");
                    return;
                }

                if (num > Integer.MAX_VALUE) {
                    out.println("There was an exception on the server");
                    return;
                }

                int factors = countFactors((int) num);
                out.println("The number " + num + " has " + factors + " factors");
            } catch (IOException e) {
                out.println("There was an exception on the server");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private int countFactors(int n) {
            int count = 0;
            for (int i = 1; i <= n; i++) {
                if (n % i == 0) count++;
            }
            return count;
        }

        public void close() throws IOException {
            socket.close();
        }


    }
}