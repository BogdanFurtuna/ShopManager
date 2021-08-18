package ro.star.internship.bf.shop.controller;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ro.star.internship.bf.shop.ShopManagerApplication;
import ro.star.internship.bf.shop.model.*;

@RestController
public class CommandController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    private static Logger logger = LogManager.getLogger(CommandController.class);

    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @GetMapping("/PRINT_CATEGORIES")
    public ArrayList<String> printCategories(){
        logger.info("PRINT CATEGORIES");
        ArrayList<String> output = new ArrayList<>();
        LinkedHashSet<Category> categories = ShopManagerApplication.getCategoryList();
        for(Category category : categories){
            output.add(category.getName());
        }
        return output;
    }

    @GetMapping("/PRINT_PRODUCTS_ALL")
    public ArrayList<Product> printProductsAll(){
        logger.info("PRINT PRODUCTS ALL");
        return ShopManagerApplication.getProductList();
    }

    @GetMapping("/PRINT_PRODUCTS_CATEGORY")
    public ArrayList<Product> printProductsCategory(@RequestParam(value = "name", defaultValue = "null") String name){
        logger.info("PRINT PRODUCTS CATEGORY " + name);
        boolean found = false;
        LinkedHashSet<Category> categories = ShopManagerApplication.getCategoryList();
        ArrayList<Product> products = new ArrayList<>();
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

    @GetMapping("PRINT_PRODUCTS")
    public Product printProducts(@RequestParam(value = "name", defaultValue = "null") String name){
        logger.info("PRINT PRODUCT");
        boolean found = false;
        for(Product product : ShopManagerApplication.getProductList()){
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
}