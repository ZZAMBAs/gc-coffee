package com.example.gccoffee.repository.product;

import com.example.gccoffee.model.Category;
import com.example.gccoffee.model.Product;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.example.gccoffee.JdbcUtils.toLocalDateTime;
import static com.example.gccoffee.JdbcUtils.toUUID;

@Repository
public class ProductJdbcRepository implements ProductRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ProductJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Product> findAll() {
        return jdbcTemplate.query("SELECT * FROM products", productRowMapper);
    }

    @Override
    public Product insert(Product product) {
        jdbcTemplate.update("INSERT INTO products VALUES (UUID_TO_BIN(:productId), :productName, :category, :price, :description, :createdAt, :updatedAt)",
                toParamMap(product));
        return product;
    }

    @Override
    public Product update(Product product) {
        int updateCnt = jdbcTemplate.update("UPDATE products SET product_name = :productName, " +
                "category = :category, price = :price, description = :description, updated_at = :updatedAt " +
                "WHERE product_id = UUID_TO_BIN(:productId)", toParamMap(product));

        if (updateCnt != 1) {
            throw new RuntimeException("업데이트 된 값이 없습니다.");
        }

        return product;
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM products WHERE product_id = UUID_TO_BIN(:productId)",
                    Collections.singletonMap("productId", productId.toString().getBytes()), productRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Product> findByName(String productName) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM products WHERE product_name = :productName",
                    Collections.singletonMap("productName", productName), productRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Product> findByCategory(Category category) {
        return jdbcTemplate.query("SELECT * FROM products WHERE category = :category",
                Collections.singletonMap("category", category.toString()), productRowMapper);
    }

    @Override
    public void deleteById(UUID productId) {
        jdbcTemplate.update("DELETE FROM products WHERE product_id = UUID_TO_BIN(:productId)",
                Collections.singletonMap("productId", productId.toString().getBytes()));
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM products", Collections.emptyMap());
    }

    private static final RowMapper<Product> productRowMapper = (rs, rowNum) -> {
        UUID productId = toUUID(rs.getBytes("product_id"));
        String productName = rs.getString("product_name");
        var category = Category.valueOf(rs.getString("category"));
        var price = rs.getLong("price");
        var description = rs.getString("description");
        var createdAt = toLocalDateTime(rs.getTimestamp("created_at"));
        var updatedAt = toLocalDateTime(rs.getTimestamp("updated_at"));

        return new Product(productId, productName, category, price, description, createdAt, updatedAt);
    };

    private static Map<String, Object> toParamMap(Product product){
        var paramMap = new HashMap<String, Object>();
        paramMap.put("productId", product.getProductId().toString().getBytes());
        paramMap.put("productName", product.getProductName());
        paramMap.put("category", product.getCategory().toString());
        paramMap.put("price", product.getPrice());
        paramMap.put("description", product.getDescription());
        paramMap.put("createdAt", product.getCreatedAt());
        paramMap.put("updatedAt", product.getUpdatedAt());

        return paramMap;
    }
}
