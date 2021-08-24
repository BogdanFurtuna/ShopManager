package ro.star.internship.bf.shop.bootstrap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.star.internship.bf.shop.model.Category;
import ro.star.internship.bf.shop.model.Client;
import ro.star.internship.bf.shop.model.Product;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class BootStrapData {

    private static Logger logger = LogManager.getLogger(BootStrapData.class);
    private static final LinkedHashSet<Category> categoryList = new LinkedHashSet<>();
    private static final ArrayList<Product> productList = new ArrayList<>();
    private static final ArrayList<Client> clientList = new ArrayList<>();

    public static void loadData() {
        Category c1 = new Category("FOOD");
        Category c2 = new Category("CLOTHES");
        categoryList.add(c1);
        categoryList.add(c2);

        Product p1 = new Product("MILK", 10, 5000, 100);
        Product p2 = new Product("BREAD", 8, 7, 100);
        productList.add(p1);
        productList.add(p2);
        c1.addProduct(p1);
        c1.addProduct(p2);

        Product p3 = new Product("PANTS", 5,6,100);
        productList.add(p3);
        c2.addProduct(p3);

        Client client = new Client("jim", 500);
        clientList.add(client);
    }

    public static LinkedHashSet<Category> getCategoryList() {
        return categoryList;
    }

    public static ArrayList<Product> getProductList() {
        return productList;
    }

    public static ArrayList<Client> getClientList() {
        return clientList;
    }

    public static void addCategory(Category category){
        categoryList.add(category);
    }

    public static void addProducts(Product product){
        productList.add(product);
    }
}
