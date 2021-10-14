package ro.star.internship.bf.shop.view;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ViewConfiguration implements WebMvcConfigurer {

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("login");
        registry.addViewController("/menu/admin").setViewName("adminMenu");
        registry.addViewController("/menu/user").setViewName("userMenu");
        registry.addViewController("/login").setViewName("login");
    }
}
