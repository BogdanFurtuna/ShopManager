package ro.star.internship.bf.shop.model;

public class Product {
    private final String name;
    private long quantity;
    private final long price;
    private final long maxQuantity;
    public Product(String name, long quantity, long price, long maxQuantity){
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.maxQuantity = maxQuantity;
    }

    public String getName() {
        return name;
    }

    public long getMaxQuantity() {
        return maxQuantity;
    }

    public long getPrice() {
        return price;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }
}

