package uhk.fim.database;

import org.apache.derby.tools.ij;

import java.io.IOException;

public class RunDBConsole {
    public static void main(String[] args) {
        try {
            // connect 'jdbc:derby:SampleDB';
            // select * from ShoppingCartItems;
            ij.main(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
