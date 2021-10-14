package ro.star.internship.bf.shop.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import ro.star.internship.bf.shop.repositories.UserRepository;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    public WebSecurityConfiguration(AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers( "/").permitAll()
                .antMatchers(HttpMethod.GET, "/print_categories").permitAll()
                .antMatchers(HttpMethod.GET, "/print_products_all").permitAll()
                .antMatchers(HttpMethod.GET, "/print_products_category").permitAll()
                .antMatchers(HttpMethod.GET, "/print_products").permitAll()
                .antMatchers(HttpMethod.POST, "/add_new_category").permitAll()
                .antMatchers(HttpMethod.POST, "/add_new_product").permitAll()
                .antMatchers(HttpMethod.PATCH, "/replenish").permitAll()
                .antMatchers(HttpMethod.PATCH, "/buy").permitAll()
                .antMatchers(HttpMethod.DELETE, "/remove_product").permitAll()
                .antMatchers("/menu/admin").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .successHandler(authenticationSuccessHandler)
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .and().csrf().disable();
    }
}
