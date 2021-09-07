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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Category " + categoryName + " doesn't exist!");
        }else{
            for (Product product : productRepository.findAll()) {
                if (product.getCategoryID().equals(category.getId())) {
                    products.add(product);
                }
            }
            if(products.size() == 0){
                logger.info("Category " + categoryName + " does not have any products!");
                return ResponseEntity.status(HttpStatus.OK).body("Category " + categoryName + " does not have any products!");
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product " + name + " does not exist!");
        }
    }

    @PostMapping("/add_new_category")
    public ResponseEntity addNewCategory(@RequestParam String categoryName){

        logger.info("ADD NEW CATEGORY " + categoryName);
        Category category = categoryRepository.findCategoryByCategoryname(categoryName);
        if(category != null){
            logger.error("Category " + categoryName + " already exists!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Category " + categoryName + " already exists!");
        }else{
            logger.info("Category " + categoryName + " has been added to the database!");
            category = new Category();
            category.setCategoryName(categoryName);
            categoryRepository.save(category);
        }
        return ResponseEntity.status(HttpStatus.OK).body("Category " + categoryName + " has been added to the database!");
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product " + name + " already exists!");
        }

        Category category = categoryRepository.findCategoryByCategoryname(categoryName);
        Integer categoryID;
        if(category == null){
            logger.error("Category " + categoryName + " doesn't exist!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Category " + categoryName + " doesn't exist!");
        }else{
            categoryID = category.getId();
        }

        long productQuantity;
        try{
            productQuantity = Integer.parseInt(quantity);
            if(productQuantity < 0){
                logger.error("Invalid number for Quantity!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid number for Quantity!");
            }
        }catch (NumberFormatException nfe){
            logger.error("Invalid number for Quantity!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid number for Quantity!");
        }

        long productPrice;
        try{
            productPrice = Integer.parseInt(price);
            if(productPrice < 0){
                logger.error("Invalid number for Price!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid number for Price!");
            }
        }catch (NumberFormatException nfe){
            logger.error("Invalid number for Price!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid number for Price!");
        }

        product = new Product();
        product.setCategoryID(categoryID);
        product.setQuantity(productQuantity);
        product.setName(name);
        product.setPrice(productPrice);
        product.setMaxQuantity(200);
        productRepository.save(product);
        logger.info("Product " + name + " has been added to the database!");

        return ResponseEntity.status(HttpStatus.OK).body("Product " + name + " has been added to the database!");
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
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid number for Quantity!");
            }
        }catch (NumberFormatException nfe){
            logger.error("Invalid number for Quantity!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid number for Quantity!");
        }

        Product product = productRepository.findProductByName(name);
        if(product == null){
            logger.error("Product " + name + " does not exist!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product " + name + " does not exist!");
        }

        if(product.getQuantity() == product.getMaxQuantity()){
            logger.error("Cannot replenish product " + name + " because the stock is full");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot replenish product " + name + " because the stock is full");
        }else if(product.getQuantity() + productQuantity > product.getMaxQuantity()){
            logger.error("Cannot replenish product " + name + " because it will overcome the maximum stock");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot replenish product " + name + " because it will overcome the maximum stock");
        }else{
            logger.info(name + " replenished with " + quantity + " units.");
            product.setQuantity(product.getQuantity() + productQuantity);
            productRepository.save(product);
        }
        return ResponseEntity.status(HttpStatus.OK).body(name + " replenished with " + quantity + " units.");
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
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid number for Quantity!");
            }
        }catch (NumberFormatException nfe){
            logger.error("Invalid number for Quantity!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid number for Quantity!");
        }

        Product product = productRepository.findProductByName(name);
        if(product == null){
            logger.error("Product " + name + " does not exist!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product " + name + " does not exist!");
        }

        Client client = clientRepository.findClientByUsername(userName);
        if(client == null){
            logger.error("Client " + userName + " does not exist!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Client " + userName + " does not exist!");
        }

        if(product.getQuantity() < Integer.parseInt(quantity)){
            logger.error("User " + userName + " cannot buy " + quantity + " " + name + " because there is only " + product.getQuantity() + " " + name + " left.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User " + userName + " cannot buy " + quantity + " " + name + " because there is only " + product.getQuantity() + " " + name + " left.");
        }else{
            long balance;
            balance = client.getBalance();
            long amountToPay = product.getPrice() * Integer.parseInt(quantity);
            if(balance < amountToPay){
                logger.error("User " + userName + " cannot buy " + quantity + " " + name + " because his balance is " + balance + " and the total cost is " + amountToPay);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User " + userName + " cannot buy " + quantity + " " + name + " because his balance is " + balance + " and the total cost is " + amountToPay);
            }else{
                logger.info("User " + userName + " has bought " + quantity + " " + name + ".");
                long newQuantity = product.getQuantity() - Integer.parseInt(quantity);
                product.setQuantity(newQuantity);
                productRepository.save(product);
                client.setBalance(balance - amountToPay);
                clientRepository.save(client);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("User " + userName + " has bought " + quantity + " " + name + ".");
    }

    @DeleteMapping("/remove_product")
    public ResponseEntity removeProduct(@RequestParam String name){

        logger.info("REMOVE PRODUCT " + name);
        Product product = productRepository.findProductByName(name);
        if(product == null){
            logger.error("Product " + name + " does not exist!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product " + name + " does not exist!");
        }else{
            if(product.getQuantity() != 0){
                logger.error("Cannot remove " + name + " because quantity is not zero. Quantity is " + product.getQuantity());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot remove " + name + " because quantity is not zero. Quantity is " + product.getQuantity());
            }else {
                productRepository.delete(product);
                logger.info("Product " + name + " was removed from the database!");
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("Product " + name + " was removed from the database!");
    }

}