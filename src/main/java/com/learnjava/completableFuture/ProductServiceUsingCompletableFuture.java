package com.learnjava.completableFuture;

import static com.learnjava.util.CommonUtil.stopWatch;
import static com.learnjava.util.LoggerUtil.log;

import com.learnjava.domain.Inventory;
import com.learnjava.domain.Product;
import com.learnjava.domain.ProductInfo;
import com.learnjava.domain.ProductOption;
import com.learnjava.domain.Review;
import com.learnjava.service.InventoryService;
import com.learnjava.service.ProductInfoService;
import com.learnjava.service.ReviewService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ProductServiceUsingCompletableFuture {

  private ProductInfoService productInfoService;
  private ReviewService reviewService;
  private InventoryService inventoryService;

  public ProductServiceUsingCompletableFuture(ProductInfoService productInfoService,
      ReviewService reviewService) {
    this.productInfoService = productInfoService;
    this.reviewService = reviewService;
  }

  public ProductServiceUsingCompletableFuture(ProductInfoService productInfoService,
      ReviewService reviewService, InventoryService inventoryService) {
    this.productInfoService = productInfoService;
    this.reviewService = reviewService;
    this.inventoryService = inventoryService;
  }

  public static void main(String[] args) {

    ProductInfoService productInfoService = new ProductInfoService();
    ReviewService reviewService = new ReviewService();
    ProductServiceUsingCompletableFuture productService = new ProductServiceUsingCompletableFuture(
        productInfoService, reviewService);
    String productId = "ABC123";
    Product product = productService.retrieveProductDetails(productId);
    log("Product is " + product);

  }

  /**
   * Apporach 1: This is for client based, as we are blocking the thread with join()
   */
  public Product retrieveProductDetails(String productId) {
    stopWatch.start();

//    ProductInfo productInfo = productInfoService.retrieveProductInfo(productId); // blocking call
//    Review review = reviewService.retrieveReviews(productId); // blocking call

    CompletableFuture<ProductInfo> productInfoCompletableFuture = CompletableFuture.supplyAsync(
        () -> productInfoService.retrieveProductInfo(productId));

    CompletableFuture<Review> reviewCompletableFuture = CompletableFuture.supplyAsync(
        () -> reviewService.retrieveReviews(productId));

    Product product = productInfoCompletableFuture.thenCombine(reviewCompletableFuture,
        (productResult, reviewResult) -> new Product(productId, productResult, reviewResult)).join();

    stopWatch.stop();
    log("Total Time Taken : " + stopWatch.getTime());
    return product;
  }

  /**
   * Apporach 2: This is for server based, as we will not block the thread execution
   * The caller method will take care of the join() operation, so we will just provide the completable future
   *
   * Called through test case
   */
  public CompletableFuture<Product> retrieveProductDetails_ServerBased(String productId) {
    stopWatch.start();

    CompletableFuture<ProductInfo> productInfoCompletableFuture = CompletableFuture.supplyAsync(
        () -> productInfoService.retrieveProductInfo(productId));

    CompletableFuture<Review> reviewCompletableFuture = CompletableFuture.supplyAsync(
        () -> reviewService.retrieveReviews(productId));

    CompletableFuture<Product> productCF = productInfoCompletableFuture.thenCombine(reviewCompletableFuture,
        (productResult, reviewResult) -> new Product(productId, productResult, reviewResult));

    stopWatch.stop();
    log("Total Time Taken : " + stopWatch.getTime());
    return productCF;
  }

  public Product retrieveProductDetailsWithInventory(String productId) {
    stopWatch.start();

    CompletableFuture<ProductInfo> productInfoCompletableFuture = CompletableFuture.supplyAsync(
        () -> productInfoService.retrieveProductInfo(productId))
        .thenApply(productInfo -> {
          updateInventory(productInfo);
          return productInfo;
        });

    CompletableFuture<Review> reviewCompletableFuture = CompletableFuture.supplyAsync(
        () -> reviewService.retrieveReviews(productId));

    Product product = productInfoCompletableFuture.thenCombine(reviewCompletableFuture,
        (productResult, reviewResult) -> new Product(productId, productResult, reviewResult)).join();

    stopWatch.stop();
    log("Total Time Taken : " + stopWatch.getTime());
    return product;
  }

  public void updateInventory(ProductInfo productInfo) {
    productInfo.getProductOptions().stream()
        .forEach(productOption -> {
          Inventory inventory = inventoryService.addInventory(productOption);
          productOption.setInventory(inventory);
        });
  }

  public Product retrieveProductDetailsWithInventory_WithCF(String productId) {
    stopWatch.start();

    CompletableFuture<ProductInfo> productInfoCompletableFuture = CompletableFuture.supplyAsync(
            () -> productInfoService.retrieveProductInfo(productId))
        .whenComplete((productInfo, ex) -> {
          log("WhenComplete exception: " + ex);
        })
        .thenApply(productInfo -> {
          updateInventoryWithCF(productInfo);
          return productInfo;
        });

    CompletableFuture<Review> reviewCompletableFuture = CompletableFuture.supplyAsync(
        () -> reviewService.retrieveReviews(productId))
        .exceptionally(ex -> {
          log("exception occurred: " + ex);
          return Review.builder().noOfReviews(0).overallRating(0.0).build();
        });

    Product product = productInfoCompletableFuture.thenCombine(reviewCompletableFuture,
        (productResult, reviewResult) -> new Product(productId, productResult, reviewResult)).join();

    stopWatch.stop();
    log("Total Time Taken : " + stopWatch.getTime());
    return product;
  }

  public void updateInventoryWithCF(ProductInfo productInfo) {

    List<CompletableFuture<Inventory>> inventoryCFList = productInfo.getProductOptions().stream()
        .map(productOption -> CompletableFuture.supplyAsync(() -> inventoryService.addInventory(productOption))
            .exceptionally(ex -> {
              log("exception in inventory: " + ex);
              return Inventory.builder().count(1).build();
            })
            .thenApply(inventory -> {
              productOption.setInventory(inventory);
              return inventory;
            })
        ).collect(Collectors.toList());

    inventoryCFList.stream().forEach(inventoryCF -> inventoryCF.join());
  }
}
