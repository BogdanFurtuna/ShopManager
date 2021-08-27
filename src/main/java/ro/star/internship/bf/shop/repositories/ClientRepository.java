package ro.star.internship.bf.shop.repositories;

import org.springframework.data.repository.CrudRepository;
import ro.star.internship.bf.shop.model.Client;

public interface ClientRepository  extends CrudRepository<Client, Integer> {
}
