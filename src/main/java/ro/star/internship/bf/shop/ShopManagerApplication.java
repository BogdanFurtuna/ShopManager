package ro.star.internship.bf.shop;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class ShopManagerApplication {

    private static Logger logger = LogManager.getLogger(ShopManagerApplication.class);


    public static void main(String[] args) {
        logger.trace("Application started!");
        SpringApplication.run(ShopManagerApplication.class, args);
        logger.trace("Application ended!");
    }

}