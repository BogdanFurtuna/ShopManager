package ro.star.internship.bf.shop.repositories;

import org.springframework.data.repository.CrudRepository;
import ro.star.internship.bf.shop.model.Category;

public interface CategoryRepository extends CrudRepository<Category, Integer> {
    Category findCategoryByCategoryname(String categoryname);
}
