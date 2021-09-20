package ro.star.internship.bf.shop.view;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ViewConfiguration implements WebMvcConfigurer {

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/hello/admin").setViewName("helloAdmin");
        registry.addViewController("/hello/user").setViewName("helloUser");
        registry.addViewController("/login").setViewName("login");
    }
}
