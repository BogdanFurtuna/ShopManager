package manager;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShopManagerApplication {

    private static Logger logger = LogManager.getLogger(ShopManagerApplication.class);

    public static void main(String[] args) {
        logger.info("Application started successfully!");
        SpringApplication.run(ShopManagerApplication.class, args);
        logger.info("Service is up and running!");
    }
}
