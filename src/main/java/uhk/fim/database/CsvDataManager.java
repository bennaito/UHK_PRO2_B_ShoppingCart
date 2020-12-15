package uhk.fim.database;

import uhk.fim.model.ShoppingCartItem;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvDataManager implements DataManager {
    private String url;

    public CsvDataManager(String url) {
        this.url = url;
    }

    @Override
    public List<ShoppingCartItem> getAllShoppingCartItems() {
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(url));
            List<ShoppingCartItem> shoppingCartItems = new ArrayList<>();

            String line;
            while ((line = bfr.readLine()) != null) {
                String[] values = line.split(";");
                ShoppingCartItem shoppingCartItem = new ShoppingCartItem(values[0], Double.parseDouble(values[1]), Integer.parseInt(values[2]));
                shoppingCartItems.add(shoppingCartItem);
            }

            bfr.close();
            return shoppingCartItems;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addShoppingCartItem(ShoppingCartItem item) {
        try {
            BufferedWriter bfw = new BufferedWriter(new FileWriter(url, true));
            bfw.newLine();
            bfw.write(item.getName() + ";" + item.getPricePerPiece() + ";" + item.getPieces());
            bfw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
