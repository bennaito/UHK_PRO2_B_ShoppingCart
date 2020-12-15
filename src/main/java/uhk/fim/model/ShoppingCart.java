package uhk.fim.model;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<ShoppingCartItem> items;

    public ShoppingCart() {
        this.items = new ArrayList<ShoppingCartItem>();
    }

    public List<ShoppingCartItem> getItems() {
        return items;
    }

    public void addItem(ShoppingCartItem newItem) {
        boolean isInList = false;
        for (ShoppingCartItem item : items) {
            if(item.getName().equals(newItem.getName()) && item.getPricePerPiece() == newItem.getPricePerPiece()) {
                isInList = true;
                item.setPieces(item.getPieces() + newItem.getPieces());
                break;
            }
        }

        if(!isInList)
            this.items.add(newItem);
    }

    public double getTotalPrice() {
        double sum = 0;
        for (ShoppingCartItem item : items) {
            sum += item.getTotalPrice();
        }
        return sum;
    }

    public void setItems(List<ShoppingCartItem> items) {
        this.items = items;
    }
}
