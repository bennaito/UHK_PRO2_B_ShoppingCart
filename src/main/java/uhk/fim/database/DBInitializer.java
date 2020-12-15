package uhk.fim.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBInitializer {

    private String driver;
    private String url;

    public DBInitializer(String driver, String url) {
        this.driver = driver;
        this.url = url;
    }

    public void init() throws ClassNotFoundException, SQLException {
        // Načíst driver
        Class.forName(driver);
        // Vytvořit connection
        Connection con = DriverManager.getConnection(url);
        // Vytvořit statement
        Statement st = con.createStatement();

        st.executeUpdate("DROP TABLE ShoppingCartItems");

        // Vytvořit tabulku - SQL dotaz
        st.executeUpdate("CREATE TABLE ShoppingCartItems (id INT NOT NULL GENERATED ALWAYS AS IDENTITY "
                + "CONSTRAINT ShoppingCartItems_PK PRIMARY KEY, name VARCHAR(50), pricePerPiece decimal, pieces INT)");

        // Ukázkové záznamy
        st.executeUpdate("INSERT INTO ShoppingCartItems (name, pricePerPiece, pieces) VALUES ('Televize', 17000.0, 1)");
        st.executeUpdate("INSERT INTO ShoppingCartItems (name, pricePerPiece, pieces) VALUES ('Lampa', 300.0, 3)");

        con.close();
    }
}
