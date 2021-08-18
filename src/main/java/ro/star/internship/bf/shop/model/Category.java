package ro.star.internship.bf.shop.model;

import java.util.ArrayList;

public class Category {
    private final String name;
    private ArrayList<Product> productList = new ArrayList<>();
    public Category(String name){
        this.name = name;
    }

    public void addProduct(Product product){
        productList.add(product);
    }

    public String getName() {
        return name;
    }

    public ArrayList<Product> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<Product> productList) {
        this.productList = productList;
    }

}
