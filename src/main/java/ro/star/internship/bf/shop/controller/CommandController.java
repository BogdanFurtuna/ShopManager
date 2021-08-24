package ro.star.internship.bf.shop.controller;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import ro.star.internship.bf.shop.bootstrap.BootStrapData;
import ro.star.internship.bf.shop.model.*;

@RestController
public class CommandController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    private static Logger logger = LogManager.getLogger(CommandController.class);

    @GetMapping("/PRINT_CATEGORIES")
    public ArrayList<String> printCategories(){
        logger.info("PRINT CATEGORIES");
        ArrayList<String> output = new ArrayList<>();
        LinkedHashSet<Category> categories = BootStrapData.getCategoryList();
        for(Category category : categories){
            output.add(category.getName());
        }
        return output;
    }

    @GetMapping("/PRINT_PRODUCTS_ALL")
    public ArrayList<Product> printProductsAll(){
        logger.info("PRINT PRODUCTS ALL");
        return BootStrapData.getProductList();
    }

    @GetMapping("/PRINT_PRODUCTS_CATEGORY")
    public ArrayList<Product> printProductsCategory(@RequestParam(value = "name", defaultValue = "null") String name){
        logger.info("PRINT PRODUCTS CATEGORY " + name);
        boolean found = false;
        LinkedHashSet<Category> categories = BootStrapData.getCategoryList();
        for(Category category : categories){
            if(category.getName().equals(name)){
                found = true;
                if(category.getProductList().size() == 0){
                    logger.info("The category " + name + " doesn't have any products!");
                }else{
                    return category.getProductList();
                }
                break;
            }
        }
        if(!found){
            logger.error("The category " + name + " does not exist!");
        }
        return null;
    }

    @GetMapping("/PRINT_PRODUCTS")
    public Product printProducts(@RequestParam(value = "name", defaultValue = "null") String name){
        logger.info("PRINT PRODUCT " + name);
        boolean found = false;
        for(Product product : BootStrapData.getProductList()){
            if(product.getName().equals(name)){
                found = true;
                return product;
            }
        }
        if(!found){
            logger.error("The product " + name + " does not exist!");
        }
        return null;
    }

    @PostMapping("/ADD_NEW_CATEGORY")
    public void addNewCategory(@RequestParam(value = "name", defaultValue = "null") String name) {
        logger.info("ADD NEW CATEGORY " + name);
        boolean found = false;
        for (Category c : BootStrapData.getCategoryList()) {
            if (c.getName().equals(name)) {
                found = true;
                break;
            }
        }
        if (found) {
            logger.error("The category " + name + " already exists!");
        } else {
            logger.info("Category " + name + " has been added to the system!");
            Category category = new Category(name);
            BootStrapData.addCategory(category);
        }
    }
    @PostMapping("/ADD_NEW_PRODUCT")
    public void addNewProduct(@RequestParam(value = "name", defaultValue = "null") String name,
                              @RequestParam(value = "categoryName", defaultValue = "null") String categoryName,
                              @RequestParam(value = "quantity", defaultValue = "null") String quantity,
                              @RequestParam(value = "price", defaultValue = "null") String price){

        logger.info("ADD NEW PRODUCT " + name);
        boolean productFound = false, errorFound = false, categoryFound = false;
        long productPrice = 0, productQuantity = 0;
        for(Product p : BootStrapData.getProductList()){
            if(p.getName().equals(name)){
                productFound = true;
                break;
            }
        }
        if(productFound){
            logger.error("Product " + name + " already exists!");
            errorFound = true;
        }

        for (Category c : BootStrapData.getCategoryList()) {
            if (c.getName().equals(categoryName)) {
                categoryFound = true;
                break;
            }
        }
        if (!categoryFound) {
            logger.error("The category " + categoryName + " doesn't exist!");
            errorFound = true;
        }

        try{
            productQuantity = Integer.parseInt(quantity);
            if(productQuantity < 0){
                logger.error("Invalid number for Quantity!");
                errorFound = true;
            }
        }catch (NumberFormatException nfe){
            logger.error("Invalid number for Quantity!");
            errorFound = true;
        }

        try{
            productPrice = Integer.parseInt(price);
            if(productPrice < 0){
                logger.error("Invalid number for Price!");
                errorFound = true;
            }
        }catch (NumberFormatException nfe){
            logger.error("Invalid number for Price!");
            errorFound = true;
        }

        if(!errorFound){
            logger.info("Product " + name + " has been added to the system!");
            Product product = new Product(name, productQuantity, productPrice, 100);
            BootStrapData.addProducts(product);
            LinkedHashSet<Category> categories = BootStrapData.getCategoryList();
            for(Category c : categories){
                if(c.getName().equals(categoryName)){
                    c.addProduct(product);
                }
            }
        }
    }

    @PatchMapping("/REPLENISH")
    public void replenishProduct(@RequestParam(value = "name", defaultValue = "null") String name,
                                 @RequestParam(value = "quantity", defaultValue = "null") String quantity){

        logger.info("REPLENISH " + name + " " + quantity);
        long productQuantity = 0;
        boolean errorFound = false, found = false;

        try{
            productQuantity = Integer.parseInt(quantity);
            if(productQuantity < 0){
                logger.error("Invalid number for Quantity!");
                errorFound = true;
            }
        }catch (NumberFormatException nfe){
            logger.error("Invalid number for Quantity!");
            errorFound = true;
        }


        for(Product product : BootStrapData.getProductList()){
            if(product.getName().equals(name)){
                found = true;
                break;
            }
        }
        if(!found){
            logger.error("The product " + name + " does not exist!");
            errorFound = true;
        }

        if(!errorFound){
            for(Product product : BootStrapData.getProductList()){
                if(product.getName().equals(name)){
                    if (product.getQuantity() == product.getMaxQuantity()) {
                        logger.error("Cannot replenish product " + name + " because the stock is full");
                    } else {
                        logger.info(name + " replenished with " + quantity + " units.");
                        product.setQuantity(product.getQuantity() + productQuantity);
                    }
                }
            }
        }
    }

    @PatchMapping("/BUY_FOR")
    public void buyProductForUser(@RequestParam(value = "name", defaultValue = "null") String name,
                                  @RequestParam(value = "quantity", defaultValue = "null") String quantity,
                                  @RequestParam(value = "userName", defaultValue = "null") String userName){

        logger.info("BUY PRODUCT " + name + " FOR " + userName);
        long productQuantity;
        boolean errorFound = false, productFound = false, clientFound = false;

        try{
            productQuantity = Integer.parseInt(quantity);
            if(productQuantity < 0){
                logger.error("Invalid number for Quantity!");
                errorFound = true;
            }
        }catch (NumberFormatException nfe){
            logger.error("Invalid number for Quantity!");
            errorFound = true;
        }


        for(Product product : BootStrapData.getProductList()){
            if(product.getName().equals(name)){
                productFound = true;
                break;
            }
        }
        if(!productFound){
            logger.error("The product " + name + " does not exist!");
            errorFound = true;
        }

        for(Client client : BootStrapData.getClientList()){
            if(client.getUserName().equals(userName)){
                clientFound = true;
                break;
            }
        }
        if(!clientFound){
            logger.error("The client " + userName + " does not exist!");
            errorFound = true;
        }

        if(!errorFound){
            for(Product product : BootStrapData.getProductList()){
                if(product.getName().equals(name)){
                    if(product.getQuantity() < Integer.parseInt(quantity)){
                        logger.error("User " + userName + " cannot buy " + quantity + " " + name + " because there is only " + product.getQuantity() + " " + name + " left.");
                    }else{
                        long balance;
                        for(Client client : BootStrapData.getClientList()){
                            if(client.getUserName().equals(userName)){
                                balance = client.getBalance();
                                long amountToPay = product.getPrice() * Integer.parseInt(quantity);
                                if(balance < amountToPay){
                                    logger.error("User " + userName + " cannot buy " + quantity + " " + name + " because his balance is " + balance + " and the total cost is " + amountToPay);
                                }else{
                                    logger.info("User " + userName + " has bought " + quantity + " " + name + ".");
                                    long newQuantity = product.getQuantity() - Integer.parseInt(quantity);
                                    product.setQuantity(newQuantity);
                                    client.setBalance(balance - amountToPay);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @DeleteMapping("REMOVE_PRODUCT")
    public void removeProduct(@RequestParam(value = "name", defaultValue = "null") String name){

        logger.info("REMOVE PRODUCT " + name);
        boolean productFound = false;

        for(Product product : BootStrapData.getProductList()){
            if(product.getName().equals(name)){
                productFound = true;
                break;
            }
        }
        if(!productFound){
            logger.error("The product " + name + " does not exist!");
        }else{
            Product prod = null;
            for(Product product : BootStrapData.getProductList()){
                if(product.getName().equals(name)){
                    if(product.getQuantity() != 0){
                        logger.error("Cannot remove " + name + " because quantity is not zero. Quantity is " + product.getQuantity());
                    }else{
                        for(Category category : BootStrapData.getCategoryList()){
                            ArrayList<Product> newProductList = new ArrayList<>();
                            for(Product p : category.getProductList()){
                                if(!p.getName().equals(name)){
                                    newProductList.add(p);
                                }
                            }
                            category.setProductList(newProductList);
                        }
                    }
                    prod = product;
                    break;
                }
            }
            BootStrapData.getProductList().remove(prod);
        }

    }

}