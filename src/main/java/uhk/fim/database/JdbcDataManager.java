package uhk.fim.database;

import uhk.fim.model.ShoppingCartItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcDataManager implements DataManager {
    private Connection con;
    private Statement st;

    public JdbcDataManager(String driver, String url) throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        con = DriverManager.getConnection(url);
    }

    @Override
    public List<ShoppingCartItem> getAllShoppingCartItems() {
        try {
            st = con.createStatement();
            ResultSet resultSet = st.executeQuery("SELECT * FROM ShoppingCartItems");

            List<ShoppingCartItem> shoppingCartItems = new ArrayList<>();
            while (resultSet.next()) {
                ShoppingCartItem shoppingCartItem = new ShoppingCartItem(
                    resultSet.getString("name"),
                    resultSet.getDouble("pricePerPiece"),
                    resultSet.getInt("pieces")
                );

                shoppingCartItems.add(shoppingCartItem);
            }

            st.close();

            return shoppingCartItems;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addShoppingCartItem(ShoppingCartItem item) {
        try {
            st = con.createStatement();
            st.executeUpdate("INSERT INTO ShoppingCartItems (name, pricePerPiece, pieces) VALUES ('" + item.getName()
                    + "', " + item.getPricePerPiece() + ", " + item.getPieces() + ")");

            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
