package uhk.fim.server;

import com.google.gson.Gson;
import uhk.fim.model.ShoppingCart;
import uhk.fim.model.ShoppingCartItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {

    private final ServerSocket serverSocket;

    public ServerThread(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        processRequestJson(socket);
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processRequest(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            String requestHeader = in.readLine();

            String responseContent = "";
            if(requestHeader.contains("about")) {
                responseContent = "<html><head><title>About Page</title></head>";
                responseContent += "<body><h1>About us</h1></body>";
                responseContent += "</html>";
            } else {
                responseContent = "<html><head><title>Welcome Page</title></head>";
                responseContent += "<body><h1>Welcome!</h1><a href=\"http://localhost:8080/about\">About us</a></body>";
                responseContent += "</html>";
            }

            PrintWriter out = new PrintWriter(socket.getOutputStream());

            out.println("HTTP/1.1 200 OK");
            out.println("Content-type: text/html");
            out.println("Content-length: " + responseContent.length());
            out.println();
            out.println(responseContent);

            out.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processRequestJson(Socket socket) {
        try {


            ShoppingCart cart = new ShoppingCart();
            cart.addItem(new ShoppingCartItem("Telefon", 6999, 1));
            cart.addItem(new ShoppingCartItem("Notebook", 15000, 2));

            Gson gson = new Gson();
            String responseContent = gson.toJson(cart);



            PrintWriter out = new PrintWriter(socket.getOutputStream());

            out.println("HTTP/1.1 200 OK");
            out.println("Content-type: application/json");
            out.println("Content-length: " + responseContent.length());
            out.println();
            out.println(responseContent);

            out.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
