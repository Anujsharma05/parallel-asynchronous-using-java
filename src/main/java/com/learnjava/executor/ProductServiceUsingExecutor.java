package com.learnjava.executor;

import static com.learnjava.util.CommonUtil.stopWatch;
import static com.learnjava.util.LoggerUtil.log;

import com.learnjava.domain.Product;
import com.learnjava.domain.ProductInfo;
import com.learnjava.domain.Review;
import com.learnjava.service.ProductInfoService;
import com.learnjava.service.ReviewService;
import com.learnjava.util.CPUCoresHelper;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ProductServiceUsingExecutor {

    static ExecutorService executorService = Executors.newFixedThreadPool(CPUCoresHelper.totalCores());

    private ProductInfoService productInfoService;
    private ReviewService reviewService;

    public ProductServiceUsingExecutor(ProductInfoService productInfoService, ReviewService reviewService) {
        this.productInfoService = productInfoService;
        this.reviewService = reviewService;
    }

    public Product retrieveProductDetails(String productId)
        throws ExecutionException, InterruptedException, TimeoutException {
        stopWatch.start();

        Future<ProductInfo> productInfoFuture = executorService.submit(() -> productInfoService.retrieveProductInfo(productId));
        Future<Review> reviewFuture = executorService.submit(() -> reviewService.retrieveReviews(productId));

        /**
         * Get() method blocks the execution until it gets the result from future or timeout occurs
         */
//        ProductInfo productInfo = productInfoFuture.get();
        ProductInfo productInfo = productInfoFuture.get(3, TimeUnit.SECONDS);
        /**
         * Review is blocked until productInfo future completes
         * Therefore it is a disadvange to block code
         */
        Review review = reviewFuture.get(); // blocking call

        stopWatch.stop();
        log("Total Time Taken : "+ stopWatch.getTime());
        return new Product(productId, productInfo, review);
    }

    public static void main(String[] args)
        throws ExecutionException, InterruptedException, TimeoutException {

        ProductInfoService productInfoService = new ProductInfoService();
        ReviewService reviewService = new ReviewService();
        ProductServiceUsingExecutor productService = new ProductServiceUsingExecutor(productInfoService, reviewService);
        String productId = "ABC123";
        Product product = productService.retrieveProductDetails(productId);
        log("Product is " + product);

        /**
         * Needs to be done explicitly or else the program will run indefinitely
         */
        executorService.shutdown();

    }
}
