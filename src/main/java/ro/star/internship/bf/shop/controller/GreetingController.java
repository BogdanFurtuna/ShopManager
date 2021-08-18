package ro.star.internship.bf.shop.controller;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ro.star.internship.bf.shop.ShopManagerApplication;
import ro.star.internship.bf.shop.model.*;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @GetMapping("/print")
    public Greeting greet(@RequestParam(value = "name", defaultValue = "User") String name, @RequestParam(value = "id", defaultValue = "1") String id){
        return new Greeting(Long.parseLong(id), String.format(template, name));
    }

    @GetMapping("/PRINT_CATEGORIES")
    public ArrayList<String> printCategories(){
        ArrayList<String> output = new ArrayList<>();
        LinkedHashSet<Category> categories = ShopManagerApplication.getCategoryList();
        for(Category category : categories){
            output.add(category.getName());
        }
        return output;
    }

    @GetMapping("PRINT_PRODUCTS_ALL")
    public ArrayList<Product> printProductsAll(){
        return ShopManagerApplication.getProductList();
    }

    @GetMapping("PRINT_PRODUCTS_CATEGORY")
    public ArrayList<Product> printProductsCategory(@RequestParam(value = "name", defaultValue = "null") String name){
        LinkedHashSet<Category> categories = ShopManagerApplication.getCategoryList();
        return new ArrayList<>();
    }
}