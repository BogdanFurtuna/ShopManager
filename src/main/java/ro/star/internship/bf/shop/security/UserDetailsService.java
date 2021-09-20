package ro.star.internship.bf.shop.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ro.star.internship.bf.shop.model.User;
import ro.star.internship.bf.shop.repositories.UserRepository;

import java.util.List;

@Configuration
public class UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    public void globalConfiguration(AuthenticationManagerBuilder auth) throws Exception {
        List<User> users = (List<User>) userRepository.findAll();
        User user1 = users.get(0);
        User user2 = users.get(1);
        auth.inMemoryAuthentication().withUser(user1.getUsername()).password(passwordEncoder.encode(user1.getPassword())).roles(user1.getRole())
                .and().withUser(user2.getUsername()).password(passwordEncoder.encode(user2.getPassword())).roles(user2.getRole());
    }
}
