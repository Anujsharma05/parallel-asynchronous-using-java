package com.learnjava.service;

import static com.learnjava.util.CommonUtil.startTimer;
import static com.learnjava.util.CommonUtil.stopWatch;
import static com.learnjava.util.CommonUtil.timeTaken;

import com.learnjava.domain.checkout.Cart;
import com.learnjava.domain.checkout.CartItem;
import com.learnjava.domain.checkout.CheckoutResponse;
import com.learnjava.domain.checkout.CheckoutStatus;
import java.util.List;
import java.util.stream.Collectors;

public class CheckoutService {

  private PriceValidatorService priceValidatorService;

  public CheckoutService(PriceValidatorService priceValidatorService) {
    this.priceValidatorService = priceValidatorService;
  }

  public CheckoutResponse checkout(Cart cart) {

    startTimer();

    List<CartItem> invalidCartItems = cart.getCartItemList().stream()
        .map(cartItem -> {
          boolean isCartItemInvalid = priceValidatorService.isCartItemInvalid(cartItem);
          cartItem.setExpired(isCartItemInvalid);
          return cartItem;
        }).parallel().collect(Collectors.toList());

    timeTaken();

    if(!invalidCartItems.isEmpty()) {
      return new CheckoutResponse(CheckoutStatus.FAILURE, invalidCartItems);
    }
    return new CheckoutResponse(CheckoutStatus.SUCCESS);
  }
}
