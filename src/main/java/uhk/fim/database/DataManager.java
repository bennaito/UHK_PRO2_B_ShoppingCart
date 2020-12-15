package uhk.fim.database;

import uhk.fim.model.ShoppingCartItem;

import java.util.List;

public interface DataManager {
    public List<ShoppingCartItem> getAllShoppingCartItems();
    public void addShoppingCartItem(ShoppingCartItem item);
}
