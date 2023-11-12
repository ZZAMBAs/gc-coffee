package com.example.gccoffee.repository.product;

import com.example.gccoffee.model.Category;
import com.example.gccoffee.model.Product;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@JdbcTest
@Rollback(false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ProductJdbcRepository.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class ProductJdbcRepositoryTest {

    @Autowired
    private ProductRepository productRepository;
    private Product product = new Product(UUID.randomUUID(), "new-product", Category.COFFEE_BEAN_PACKAGE, 1000L);

    @Test
    @Order(1)
    @DisplayName("상품을 추가할 수 있다")
    void testInsertProduct() {
        productRepository.insert(product);
        var all = productRepository.findAll();

        assertThat(all.isEmpty(), is(false));
    }

    @Test
    @Order(2)
    @DisplayName("상품을 전부 조회할 수 있다.")
    void testFindAllProducts() {
        var products = productRepository.findAll();

        assertThat(products, hasSize(1));
    }

    @Test
    @Order(3)
    @DisplayName("상품 이름으로 조회할 수 있다")
    void testFindByName() {
        Optional<Product> product1 = productRepository.findByName(product.getProductName());

        assertThat(product1.isEmpty(), is(false));
    }

    @Test
    @Order(4)
    @DisplayName("상품을 아이디로 조회할 수 있다")
    void testFindById() {
        Optional<Product> product1 = productRepository.findById(product.getProductId());

        assertThat(product1.isEmpty(), is(false));
    }

    @Test
    @Order(5)
    @DisplayName("상품을 카테고리로 조회할 수 있다")
    void testFindByCategory() {
        List<Product> products = productRepository.findByCategory(product.getCategory());

        assertThat(products.isEmpty(), is(false));
    }

    @Test
    @Order(6)
    @DisplayName("상품 수정이 가능하다")
    void testUpdateProduct() {
        product.setProductName("update-product");
        productRepository.update(product);

        Optional<Product> product1 = productRepository.findById(product.getProductId());

        assertThat(product1.isEmpty(), is(false));
        assertThat(product1.get(), samePropertyValuesAs(product));
    }

    @Test
    @Order(7)
    @DisplayName("상품을 일부 제거할 수 있다.")
    void testDeleteById() {
        UUID productId = UUID.randomUUID();

        productRepository.insert(new Product(productId, "test", Category.COFFEE_TRASH, 100));
        productRepository.deleteById(productId);

        assertThat(productRepository.findById(productId).isPresent(), is(false));
    }

    @Test
    @Order(8)
    @DisplayName("상품을 전부 제거할 수 있다")
    void testDeleteAll() {
        productRepository.deleteAll();

        List<Product> products = productRepository.findAll();

        assertThat(products.isEmpty(), is(true));
    }
}
