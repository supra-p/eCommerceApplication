package com.ecommerce.repositories;

import com.ecommerce.models.Category;
import com.ecommerce.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByCategory(Category category, Pageable pageable);

    @Query("Select p from Product p where UPPER(p.productName) LIKE CONCAT('%',UPPER(:keyword),'%')" +
            "or UPPER(p.description)  LIKE CONCAT('%',UPPER(:keyword),'%')")
    List<Product> findByProductNameIgnoreCaseOrDescriptionIgnoreCase(@Param("keyword") String keyword);
}
