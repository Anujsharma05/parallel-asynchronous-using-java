package com.learnjava.completableFuture;

import static com.learnjava.util.LoggerUtil.log;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.learnjava.domain.Product;
import com.learnjava.service.InventoryService;
import com.learnjava.service.ProductInfoService;
import com.learnjava.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceUsingCompletableFutureExceptionTest {

  @Mock
  private ProductInfoService productInfoService = Mockito.mock(ProductInfoService.class);
  @Mock
  private ReviewService reviewService = Mockito.mock(ReviewService.class);
  @Mock
  private InventoryService inventoryService = Mockito.mock(InventoryService.class);

  @InjectMocks
  ProductServiceUsingCompletableFuture psucf;

  @Test
  void retrieveProductDetails_ExceptionInReview() {
    //when
    String productId = "ABC123";
    when(productInfoService.retrieveProductInfo(any())).thenCallRealMethod();
    when(inventoryService.addInventory(any())).thenCallRealMethod();
    when(reviewService.retrieveReviews(any())).thenThrow(new RuntimeException("Failed while fetching reviews"));

    //then
    Product product = psucf.retrieveProductDetailsWithInventory_WithCF(productId);
    log("product: " + product);

    assertNotNull(product.getReview());
    assertEquals(0, product.getReview().getNoOfReviews());
  }

  @Test
  void retrieveProductDetails_ExceptionInInventory() {
    //when
    String productId = "ABC123";
    when(productInfoService.retrieveProductInfo(any())).thenCallRealMethod();
    when(inventoryService.addInventory(any())).thenThrow(new RuntimeException("Failed while fetching inventory"));
    when(reviewService.retrieveReviews(any())).thenCallRealMethod();

    //then
    Product product = psucf.retrieveProductDetailsWithInventory_WithCF(productId);
    log("product: " + product);

    product.getProductInfo().getProductOptions().stream().forEach(productOption -> {
      assertNotNull(productOption.getInventory());
      assertEquals(1, productOption.getInventory().getCount());
    });
  }

  @Test
  void failWithoutRecover_ExceptionInProductInfo() {
    //when
    String productId = "ABC123";
    when(productInfoService.retrieveProductInfo(any())).thenThrow(new RuntimeException("Failed while fetching product info"));
//    when(inventoryService.addInventory(any())).thenCallRealMethod();
//    when(reviewService.retrieveReviews(any())).thenThrow(new RuntimeException("Failed while fetching reviews"));

    //then
    assertThrows(RuntimeException.class, () -> psucf.retrieveProductDetailsWithInventory_WithCF(productId));
  }
}