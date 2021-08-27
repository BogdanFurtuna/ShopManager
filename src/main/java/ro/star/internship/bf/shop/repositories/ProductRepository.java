package ro.star.internship.bf.shop.repositories;

import org.springframework.data.repository.CrudRepository;
import ro.star.internship.bf.shop.model.Product;

public interface ProductRepository extends CrudRepository<Product, Integer> {
}
