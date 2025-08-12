package com.xunim.catalogo.repository;

import com.xunim.catalogo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    //Listar todos os produtos ativos para pagina inicial
    @Query("SELECT p FROM Product p WHERE p.id IN :ids AND p.active = true")
    List<Product> findActiveProductByIds(@Param("ids") List<Long> ids);

    //Listar produtos por nome e paginado  #TODO: Query
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
            "(:search IS NULL OR :search = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Product> findActiveProductsByName(@Param("search") String search, Pageable pageable);

    //Listar Top 3 produtos Mais vendidos
    @Query(value = """
        SELECT p.id, p.name, p.price, p.stock, p.active,
                COALESCE(SUM(oi.quantity), 0) as total_sold
        FROM products p
        LEFT JOIN order_items oi ON p.id = ou.product.id
        WHERE p.active = true
        GROUP BY p.id, p.name, p.price, p.stock, p.active
        ORDER BY total_sold DESC
        LIMIT 3
        """, nativeQuery = true)
    List<Object[]> findTop3MostSoldProducts();
}
