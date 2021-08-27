package ro.star.internship.bf.shop.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ro.star.internship.bf.shop.model.*;
import ro.star.internship.bf.shop.repositories.CategoryRepository;
import ro.star.internship.bf.shop.repositories.ClientRepository;
import ro.star.internship.bf.shop.repositories.ProductRepository;

import java.util.ArrayList;

@RestController
public class CommandController {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ClientRepository clientRepository;

    private static Logger logger = LogManager.getLogger(CommandController.class);
    private boolean productFound = false;
    private boolean categoryFound = false;
    private boolean errorFound = false;

    @GetMapping("/print_categories")
    public Iterable<Category> printCategories() {
        logger.info("PRINT CATEGORIES");
        return categoryRepository.findAll();
    }

    @GetMapping("/print_products_all")
    public Iterable<Product> printProducts(){
        logger.info("PRINT PRODUCTS ALL");
        return productRepository.findAll();
    }

    @GetMapping("/print_products_category")
    public ArrayList<Product> printProductsCategory(@RequestParam String name){

        logger.info("PRINT PRODUCTS CATEGORY " + name);
        categoryFound = false;
        ArrayList<Product> products = new ArrayList<>();
        Integer categoryID = 0;
        for(Category category : categoryRepository.findAll()){
            if(category.getCategoryname().equals(name)){
                categoryFound = true;
                categoryID = category.getId();
                break;
            }
        }
        if(!categoryFound){
            logger.error("Category " + name + " doesn't exist!");
            errorFound = true;
        }
        if(!errorFound) {
            for (Product product : productRepository.findAll()) {
                if (product.getCategoryID().equals(categoryID)) {
                    products.add(product);
                }
            }
        }
        if(products.size() == 0){
            logger.info("Category " + name + " does not have any products!");
        }
        return products;
    }

    @GetMapping("/print_products")
    public Product printProduct(@RequestParam String name){

        logger.info("PRINT PRODUCTS " + name);
        Product p = new Product();
        productFound = false;
        for(Product product : productRepository.findAll()){
            if(product.getName().equals(name)){
                p = product;
                productFound = true;
                break;
            }
        }
        if(!productFound){
            logger.error("Product " + name + " does not exist!");
        }
        return p;
    }

    @PostMapping("/add_new_category")
    public void addNewCategory(@RequestParam String name){

        logger.info("ADD NEW CATEGORY " + name);
        categoryFound = false;
        for(Category category : categoryRepository.findAll()){
            if(category.getCategoryname().equals(name)){
                categoryFound = true;
                break;
            }
        }
        if(categoryFound){
            logger.error("Category " + name + " already exists!");
        }else{
            logger.info("Category " + name + " has been added to the database!");
            Category category = new Category();
            category.setCategoryname(name);
            categoryRepository.save(category);
        }
    }

    @PostMapping("/add_new_product")
    public void addNewProduct(@RequestParam String name,
                              @RequestParam String categoryName,
                              @RequestParam String quantity,
                              @RequestParam String price){

        logger.info("ADD NEW PRODUCT " + name);
        productFound = false; categoryFound = false; errorFound = false;
        Integer categoryID = 0;
        long productPrice = 0, productQuantity = 0;

        for(Product p : productRepository.findAll()){
            if(p.getName().equals(name)){
                productFound = true;
                break;
            }
        }
        if(productFound){
           logger.error("Product " + name + " already exists!");
           errorFound = true;
        }

        for(Category category : categoryRepository.findAll()){
            if(category.getCategoryname().equals(categoryName)){
                categoryFound = true;
                categoryID = category.getId();
                break;
            }
        }
        if(!categoryFound){
            logger.error("Category " + categoryName + " doesn't exist!");
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
            logger.info("Product " + name + " has been added to the database!");
            Product product = new Product();
            product.setCategoryID(categoryID);
            product.setQuantity(productQuantity);
            product.setName(name);
            product.setPrice(productPrice);
            product.setMaxQuantity(200);
            productRepository.save(product);
        }
    }

    @PatchMapping("/replenish")
    public void replenishProduct(@RequestParam String name,
                                 @RequestParam String quantity){

        logger.info("REPLENISH " + name + " " + quantity);
        long productQuantity = 0;
        errorFound = false; productFound = false;

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

        for(Product p : productRepository.findAll()){
            if(p.getName().equals(name)){
                productFound = true;
                break;
            }
        }
        if(!productFound){
            logger.error("Product " + name + " does not exist!");
            errorFound = true;
        }

        if(!errorFound){
            for(Product product : productRepository.findAll()){
                if(product.getName().equals(name)){
                    if(product.getQuantity() == product.getMaxQuantity()){
                        logger.error("Cannot replenish product " + name + " because the stock is full");
                    }else if(product.getQuantity() + productQuantity > product.getMaxQuantity()){
                        logger.error("Cannot replenish product " + name + " because it will overcome the maximum stock");
                    }else{
                        logger.info(name + " replenished with " + quantity + " units.");
                        product.setQuantity(product.getQuantity() + productQuantity);
                        productRepository.save(product);
                    }
                }
            }
        }
    }

    @PatchMapping("/buy")
    public void buyProductForClient(@RequestParam(value = "name", defaultValue = "null") String name,
                                  @RequestParam(value = "quantity", defaultValue = "null") String quantity,
                                  @RequestParam(value = "userName", defaultValue = "null") String userName){

        logger.info("BUY PRODUCT " + name + " FOR " + userName);
        long productQuantity;
        productFound = false; errorFound = false;
        boolean clientFound = false;

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

        for(Product p : productRepository.findAll()){
            if(p.getName().equals(name)){
                productFound = true;
                break;
            }
        }
        if(!productFound){
            logger.error("Product " + name + " does not exist!");
            errorFound = true;
        }

        for(Client client : clientRepository.findAll()){
            if(client.getUserName().equals(userName)){
                clientFound = true;
                break;
            }
        }
        if(!clientFound){
            logger.error("Client " + userName + " does not exist!");
            errorFound = true;
        }

        if(!errorFound){
            for(Product product : productRepository.findAll()){
                if(product.getName().equals(name)){
                    if(product.getQuantity() < Integer.parseInt(quantity)){
                        logger.error("User " + userName + " cannot buy " + quantity + " " + name + " because there is only " + product.getQuantity() + " " + name + " left.");
                    }else{
                        long balance;
                        for(Client client : clientRepository.findAll()){
                            if(client.getUserName().equals(userName)){
                                balance = client.getBalance();
                                long amountToPay = product.getPrice() * Integer.parseInt(quantity);
                                if(balance < amountToPay){
                                    logger.error("User " + userName + " cannot buy " + quantity + " " + name + " because his balance is " + balance + " and the total cost is " + amountToPay);
                                }else{
                                    logger.info("User " + userName + " has bought " + quantity + " " + name + ".");
                                    long newQuantity = product.getQuantity() - Integer.parseInt(quantity);
                                    product.setQuantity(newQuantity);
                                    productRepository.save(product);
                                    client.setBalance(balance - amountToPay);
                                    clientRepository.save(client);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @DeleteMapping("/remove_product")
    public void removeProduct(@RequestParam String name){

        logger.info("REMOVE PRODUCT " + name);
        productFound = false;

        for(Product p : productRepository.findAll()){
            if(p.getName().equals(name)){
                productFound = true;
                break;
            }
        }
        if(!productFound){
            logger.error("Product " + name + " does not exist!");
        }else{
            for(Product product : productRepository.findAll()){
                if(product.getName().equals(name)){
                    if(product.getQuantity() != 0){
                        logger.error("Cannot remove " + name + " because quantity is not zero. Quantity is " + product.getQuantity());
                    }else{
                        productRepository.delete(product);
                    }
                    break;
                }
            }
        }


    }

}