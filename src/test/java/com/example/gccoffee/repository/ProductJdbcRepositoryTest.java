package com.example.gccoffee.repository;

import com.example.gccoffee.GcCoffeeApplication;
import com.example.gccoffee.model.Category;
import com.example.gccoffee.model.Product;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductJdbcRepositoryTest {
    @Configuration
    @ComponentScan(basePackageClasses = GcCoffeeApplication.class)
    static class TempConfig {
        @Bean
        public DataSource dataSource() {
            return DataSourceBuilder.create()
                    .driverClassName("com.mysql.cj.jdbc.Driver")
                    .username("root")
                    .password("1234")
                    .type(HikariDataSource.class)
                    .url("jdbc:mysql://localhost:3306/temp")
                    .build();
        }

        @Bean
        public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("schema.sql"));

            DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();

            dataSourceInitializer.setDataSource(dataSource);
            dataSourceInitializer.setDatabasePopulator(populator);
            dataSourceInitializer.setEnabled(true);

            return dataSourceInitializer;
        }
    }

    @Autowired
    private ProductRepository productRepository;
    private Product product = new Product(UUID.randomUUID(), "new-product", Category.COFFEE_BEAN_PACKAGE, 1000L);

    @Test
    @Order(1)
    @DisplayName("상품을 추가할 수 있다")
    void testInsert() {
        productRepository.insert(product);
        var all = productRepository.findAll();

        assertThat(all.isEmpty(), is(false));
    }

    @Test
    @Order(2)
    @DisplayName("상품 이름으로 조회할 수 있다")
    void testFindByName() {
        Optional<Product> product1 = productRepository.findByName(product.getProductName());

        assertThat(product1.isEmpty(), is(false));
    }

    @Test
    @Order(3)
    @DisplayName("상품을 아이디로 조회할 수 있다")
    void testFindById() {
        Optional<Product> product1 = productRepository.findById(product.getProductId());

        assertThat(product1.isEmpty(), is(false));
    }

    @Test
    @Order(4)
    @DisplayName("상품을 카테고리로 조회할 수 있다")
    void testFindByCategory() {
        List<Product> products = productRepository.findByCategory(product.getCategory());

        assertThat(products.isEmpty(), is(false));
    }

    @Test
    @Order(6)
    @DisplayName("상품을 전부 제거할 수 있다")
    void testDeleteAll() {
        productRepository.deleteAll();

        List<Product> products = productRepository.findAll();

        assertThat(products.isEmpty(), is(true));
    }

    @Test
    @Order(5)
    @DisplayName("상품 수정이 가능하다")
    void testUpdate() {
        product.setProductName("update-product");
        productRepository.update(product);

        Optional<Product> product1 = productRepository.findById(product.getProductId());

        assertThat(product1.isEmpty(), is(false));
        assertThat(product1.get(), samePropertyValuesAs(product));
    }
}
