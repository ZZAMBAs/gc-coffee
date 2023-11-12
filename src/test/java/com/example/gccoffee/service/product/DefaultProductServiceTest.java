package com.example.gccoffee.service.product;

import com.example.gccoffee.model.Category;
import com.example.gccoffee.model.Product;
import com.example.gccoffee.repository.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class DefaultProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private DefaultProductService productService;

    @Test
    @DisplayName("카테고리별 상품들을 전부 조회할 수 있다.")
    void testGetProductsByCategory() {
        var products = new ArrayList<Product>();
        products.add(new Product(UUID.randomUUID(), "temp", Category.COFFEE_BEAN_PACKAGE, 1000L));
        products.add(new Product(UUID.randomUUID(), "temp2", Category.COFFEE_BEAN_PACKAGE, 2000L));

        when(productRepository.findByCategory(Category.COFFEE_BEAN_PACKAGE))
                .thenReturn(products);

        var foundProducts = productService.getProductsByCategory(Category.COFFEE_BEAN_PACKAGE);

        assertThat(foundProducts.size()).isEqualTo(2);

        verify(productRepository).findByCategory(Category.COFFEE_BEAN_PACKAGE);
        verify(productRepository, never()).findByCategory(Category.COFFEE_TRASH);
    }

    @Test
    @DisplayName("id를 이용해 특정 상품을 조회할 수 있다.")
    void testGetProductById() {
        UUID productId = UUID.randomUUID();
        Product product = new Product(productId, "temp", Category.COFFEE_BEAN_PACKAGE, 1000L);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Optional<Product> foundProduct = productService.getProductById(productId);

        assertThat(foundProduct.isPresent()).isTrue();
        assertThat(foundProduct.get()).isEqualTo(product);

        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("전체 상품을 조회할 수 있다.")
    void getAllProducts() {
        ArrayList<Product> products = new ArrayList<>() {{
            add(new Product(UUID.randomUUID(), "test1", Category.COFFEE_BEAN_PACKAGE, 2000L));
            add(new Product(UUID.randomUUID(), "test2", Category.COFFEE_TRASH, 4000L));
        }};

        when(productRepository.findAll()).thenReturn(products);

        assertThat(productService.getAllProducts()).hasSize(2);

        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("id를 이용해 특정 상품을 없앨 수 있다.")
    void deleteProductById() {
        UUID deleteId = UUID.randomUUID();
        productService.deleteProductById(deleteId);

        verify(productRepository).deleteById(deleteId);
    }

    @Test
    @DisplayName("전체 상품을 삭제할 수 있다.")
    void deleteAllProducts() {
        productService.deleteAllProducts();

        verify(productRepository).deleteAll();
    }

    @Test
    @DisplayName("상품을 생성할 수 있다.")
    void createProduct() {
        when(productRepository.insert(any(Product.class)))
                .thenReturn(new Product(UUID.randomUUID(), "name", Category.COFFEE_BEAN_PACKAGE, 1200L, "desc"));

        Product product = productService.createProduct("name", Category.COFFEE_BEAN_PACKAGE, 1200L, "desc");

        assertThat(product.getProductName()).isEqualTo("name");
        assertThat(product.getDescription()).isEqualTo("desc");
        assertThat(product.getPrice()).isEqualTo(1200L);
        assertThat(product.getCategory()).isEqualTo(Category.COFFEE_BEAN_PACKAGE);

        verify(productRepository).insert(any());
    }

    @Test
    @DisplayName("존재하는 상품을 수정할 수 있다.")
    void updateProduct() {
        UUID productId = UUID.randomUUID();
        when(productRepository.update(any(Product.class)))
                .thenReturn(new Product(productId, "updated", Category.COFFEE_TRASH, 1000L, "updatedDesc"));

        Product updatedProduct =
                productService.updateProduct(productId, "updated", Category.COFFEE_TRASH, 1000L, "updatedDesc");

        assertThat(updatedProduct.getProductId()).isEqualTo(productId);
        assertThat(updatedProduct.getProductName()).isEqualTo("updated");
        assertThat(updatedProduct.getDescription()).isEqualTo("updatedDesc");

        verify(productRepository).update(any());
    }
}
