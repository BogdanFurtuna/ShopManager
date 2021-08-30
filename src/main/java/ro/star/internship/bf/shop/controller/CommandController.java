package ro.star.internship.bf.shop.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final ResponseEntity BAD_REQUEST = new ResponseEntity(HttpStatus.BAD_REQUEST);
    private final ResponseEntity OK = new ResponseEntity(HttpStatus.OK);

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
    public ResponseEntity printProductsCategory(@RequestParam String categoryName){

        logger.info("PRINT PRODUCTS CATEGORY " + categoryName);
        Category category = categoryRepository.findCategoryByCategoryname(categoryName);
        ArrayList<Product> products = new ArrayList<>();

        if(category == null){
            logger.error("Category " + categoryName + " doesn't exist!");
            return BAD_REQUEST;
        }else{
            for (Product product : productRepository.findAll()) {
                if (product.getCategoryID().equals(category.getId())) {
                    products.add(product);
                }
            }
            if(products.size() == 0){
                logger.info("Category " + categoryName + " does not have any products!");
            }
            return new ResponseEntity<>(products, HttpStatus.OK);
        }
    }

    @GetMapping("/print_products")
    public ResponseEntity printProduct(@RequestParam String name){

        logger.info("PRINT PRODUCTS " + name);
        if(productRepository.findProductByName(name) != null){
            return new ResponseEntity<>(productRepository.findProductByName(name), HttpStatus.OK);
        }else{
            logger.error("Product " + name + " does not exist!");
            return BAD_REQUEST;
        }
    }

    @PostMapping("/add_new_category")
    public ResponseEntity addNewCategory(@RequestParam String categoryName){

        logger.info("ADD NEW CATEGORY " + categoryName);
        Category category = categoryRepository.findCategoryByCategoryname(categoryName);
        if(category != null){
            logger.error("Category " + categoryName + " already exists!");
            return BAD_REQUEST;
        }else{
            logger.info("Category " + categoryName + " has been added to the database!");
            category = new Category();
            category.setCategoryName(categoryName);
            categoryRepository.save(category);
        }
        return OK;
    }

    @PostMapping("/add_new_product")
    public ResponseEntity addNewProduct(@RequestParam String name,
                                        @RequestParam String categoryName,
                                        @RequestParam String quantity,
                                        @RequestParam String price){

        logger.info("ADD NEW PRODUCT " + name);
        Product product = productRepository.findProductByName(name);
        if(product != null){
            logger.error("Product " + name + " already exists!");
            return BAD_REQUEST;
        }

        Category category = categoryRepository.findCategoryByCategoryname(categoryName);
        Integer categoryID;
        if(category == null){
            logger.error("Category " + categoryName + " doesn't exist!");
            return BAD_REQUEST;
        }else{
            categoryID = category.getId();
        }

        long productQuantity;
        try{
            productQuantity = Integer.parseInt(quantity);
            if(productQuantity < 0){
                logger.error("Invalid number for Quantity!");
                return BAD_REQUEST;
            }
        }catch (NumberFormatException nfe){
            logger.error("Invalid number for Quantity!");
            return BAD_REQUEST;
        }

        long productPrice;
        try{
            productPrice = Integer.parseInt(price);
            if(productPrice < 0){
                logger.error("Invalid number for Price!");
                return BAD_REQUEST;
            }
        }catch (NumberFormatException nfe){
            logger.error("Invalid number for Price!");
            return BAD_REQUEST;
        }

        product = new Product();
        product.setCategoryID(categoryID);
        product.setQuantity(productQuantity);
        product.setName(name);
        product.setPrice(productPrice);
        product.setMaxQuantity(200);
        productRepository.save(product);
        logger.info("Product " + name + " has been added to the database!");

        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/replenish")
    public ResponseEntity replenishProduct(@RequestParam String name,
                                           @RequestParam String quantity){

        logger.info("REPLENISH " + name + " " + quantity);
        long productQuantity;
        try{
            productQuantity = Integer.parseInt(quantity);
            if(productQuantity < 0){
                logger.error("Invalid number for Quantity!");
                return BAD_REQUEST;
            }
        }catch (NumberFormatException nfe){
            logger.error("Invalid number for Quantity!");
            return BAD_REQUEST;
        }

        Product product = productRepository.findProductByName(name);
        if(product == null){
            logger.error("Product " + name + " does not exist!");
            return BAD_REQUEST;
        }

        if(product.getQuantity() == product.getMaxQuantity()){
            logger.error("Cannot replenish product " + name + " because the stock is full");
            return BAD_REQUEST;
        }else if(product.getQuantity() + productQuantity > product.getMaxQuantity()){
            logger.error("Cannot replenish product " + name + " because it will overcome the maximum stock");
            return BAD_REQUEST;
        }else{
            logger.info(name + " replenished with " + quantity + " units.");
            product.setQuantity(product.getQuantity() + productQuantity);
            productRepository.save(product);
        }
        return OK;
    }

    @PatchMapping("/buy")
    public ResponseEntity buyProductForClient(@RequestParam String name,
                                  @RequestParam String quantity,
                                  @RequestParam String userName){

        logger.info("BUY PRODUCT " + name + " FOR " + userName);
        long productQuantity;
        try{
            productQuantity = Integer.parseInt(quantity);
            if(productQuantity < 0){
                logger.error("Invalid number for Quantity!");
                return BAD_REQUEST;
            }
        }catch (NumberFormatException nfe){
            logger.error("Invalid number for Quantity!");
            return BAD_REQUEST;
        }

        Product product = productRepository.findProductByName(name);
        if(product == null){
            logger.error("Product " + name + " does not exist!");
            return BAD_REQUEST;
        }

        Client client = clientRepository.findClientByUsername(userName);
        if(client == null){
            logger.error("Client " + userName + " does not exist!");
            return BAD_REQUEST;
        }

        if(product.getQuantity() < Integer.parseInt(quantity)){
            logger.error("User " + userName + " cannot buy " + quantity + " " + name + " because there is only " + product.getQuantity() + " " + name + " left.");
            return BAD_REQUEST;
        }else{
            long balance;
            balance = client.getBalance();
            long amountToPay = product.getPrice() * Integer.parseInt(quantity);
            if(balance < amountToPay){
                logger.error("User " + userName + " cannot buy " + quantity + " " + name + " because his balance is " + balance + " and the total cost is " + amountToPay);
                return BAD_REQUEST;
            }else{
                logger.info("User " + userName + " has bought " + quantity + " " + name + ".");
                long newQuantity = product.getQuantity() - Integer.parseInt(quantity);
                product.setQuantity(newQuantity);
                productRepository.save(product);
                client.setBalance(balance - amountToPay);
                clientRepository.save(client);
            }
        }
        return OK;
    }

    @DeleteMapping("/remove_product")
    public ResponseEntity removeProduct(@RequestParam String name){

        logger.info("REMOVE PRODUCT " + name);
        Product product = productRepository.findProductByName(name);
        if(product == null){
            logger.error("Product " + name + " does not exist!");
            return BAD_REQUEST;
        }else{
            if(product.getQuantity() != 0){
                logger.error("Cannot remove " + name + " because quantity is not zero. Quantity is " + product.getQuantity());
                return BAD_REQUEST;
            }else {
                productRepository.delete(product);
            }
        }
        return OK;
    }

}