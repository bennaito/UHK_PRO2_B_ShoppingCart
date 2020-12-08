package uhk.fim.server;

import java.io.IOException;

public class WebServer {
    private int port;

    public WebServer(int port) {
        this.port = port;
    }

    private void start() throws IOException {
        ServerThread thread = new ServerThread(port);
        thread.start();

        System.out.println("Server spuštěn. Naslouchá na adrese http://localhost:" + port);
    }

    public static void main(String[] args) {
        WebServer server = new WebServer(8080);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Při spuštění serveru došlo k chybě: " + e.getMessage());
        }
    }
}
