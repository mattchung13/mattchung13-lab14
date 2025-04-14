import java.io.*;
import java.net.*;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Client(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void handshake() {
        out.println("12345");
    }

    public String request(String input) throws IOException {
        out.println(input);
        return in.readLine();
    }

    public void disconnect() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public Socket getSocket() {
        return socket;
    }
}
