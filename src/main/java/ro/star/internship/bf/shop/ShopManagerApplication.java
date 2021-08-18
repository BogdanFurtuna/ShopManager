package ro.star.internship.bf.shop;

import ro.star.internship.bf.shop.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ro.star.internship.bf.shop.model.Category;
import ro.star.internship.bf.shop.model.Product;

import java.util.ArrayList;
import java.util.LinkedHashSet;

@SpringBootApplication
public class ShopManagerApplication {

    private static Logger logger = LogManager.getLogger(ShopManagerApplication.class);
    private static final LinkedHashSet<Category> categoryList = new LinkedHashSet<>();
    private static final ArrayList<Product> productList = new ArrayList<>();


    public static void main(String[] args) {
        Category c1 = new Category("FOOD");
        Category c2 = new Category("CLOTHES");
        categoryList.add(c1);
        categoryList.add(c2);

        Product p1 = new Product("MILK", 10, 5, 100);
        Product p2 = new Product("BREAD", 8, 7, 100);
        productList.add(p1);
        productList.add(p2);
        c1.setProductList(productList);

        Product p3 = new Product("PANTS", 6,6,100);
        ArrayList<Product> prodList = new ArrayList<>();
        prodList.add(p3);
        c2.setProductList(prodList);

        SpringApplication.run(ShopManagerApplication.class, args);
    }

    public static LinkedHashSet<Category> getCategoryList() {
        return categoryList;
    }

    public static ArrayList<Product> getProductList() {
        return productList;
    }
}
