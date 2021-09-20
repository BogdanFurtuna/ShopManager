package ro.star.internship.bf.shop.repositories;

import org.springframework.data.jpa.repository.Query;import org.springframework.data.repository.CrudRepository;
import ro.star.internship.bf.shop.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {}
