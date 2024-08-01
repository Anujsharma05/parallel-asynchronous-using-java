package com.learnjava.completableFuture;

import static com.learnjava.util.LoggerUtil.log;
import static org.junit.jupiter.api.Assertions.*;

import com.learnjava.domain.Product;
import com.learnjava.service.InventoryService;
import com.learnjava.service.ProductInfoService;
import com.learnjava.service.ReviewService;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

class ProductServiceUsingCompletableFutureTest {

  ProductInfoService productInfoService = new ProductInfoService();
  ReviewService reviewService = new ReviewService();
  InventoryService inventoryService = new InventoryService();
  ProductServiceUsingCompletableFuture productService = new ProductServiceUsingCompletableFuture(
      productInfoService, reviewService, inventoryService);

  @Test
  void retrieveProductDetails_ServerBased() {
    String productId = "ABC123";
    CompletableFuture<Product> productCF = productService.retrieveProductDetails_ServerBased(
        productId);
//    Product product = productCF.join();

    productCF.thenAccept(product -> {
      log("product: " + product);
      assertNotNull(product);
      assertEquals(200, product.getReview().getNoOfReviews());
    }).join();
  }

  @Test
  void retrieveProductDetailsWithInventory() {
    String productId = "ABC123";
    Product product = productService.retrieveProductDetailsWithInventory(productId);
    log("product: " + product);

    product.getProductInfo().getProductOptions().stream()
        .forEach(productOption -> assertNotNull(productOption.getInventory()));
  }

  @Test
  void retrieveProductDetailsWithInventory_WithCF() {
    String productId = "ABC123";
    Product product = productService.retrieveProductDetailsWithInventory_WithCF(productId);
    log("product: " + product);

    product.getProductInfo().getProductOptions().stream()
        .forEach(productOption -> assertNotNull(productOption.getInventory()));
  }
}