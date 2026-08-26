package com.example.ecom.service;

import com.example.ecom.exception.InsufficientStockException;
import com.example.ecom.model.Order;
import com.example.ecom.model.Product;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public class CheckoutServiceTest {

    private CheckoutService checkout;
    private Map<String, Product> catalog;

    @BeforeMethod
    public void setUp() {
        checkout = new CheckoutService();
        catalog = new HashMap<>();
        catalog.put("p1", new Product("p1", "Laptop", 1000.0, 5));
        catalog.put("p2", new Product("p2", "Mouse", 25.0, 10));
    }

    @Test
    public void testSuccessfulCheckoutComputesTotal() {
        CartService cart = new CartService();
        cart.addItem("p1", 1);
        cart.addItem("p2", 2);

        Order order = checkout.placeOrder(cart, catalog, 0.0);

        assertEquals(order.getSubtotal(), 1050.0, 0.001);
        assertEquals(order.getTotal(), 1050.0, 0.001);
        assertEquals(order.getStatus(), "PLACED");
        assertEquals(order.getLines().size(), 2);
    }

    @Test
    public void testDiscountIsApplied() {
        CartService cart = new CartService();
        cart.addItem("p1", 1); // 1000.0

        Order order = checkout.placeOrder(cart, catalog, 0.10);

        assertEquals(order.getDiscount(), 100.0, 0.001);
        assertEquals(order.getTotal(), 900.0, 0.001);
    }

    @Test(expectedExceptions = InsufficientStockException.class)
    public void testInsufficientStockThrows() {
        CartService cart = new CartService();
        cart.addItem("p1", 99); // only 5 in stock

        checkout.placeOrder(cart, catalog, 0.0);
    }

    @Test
    public void testStockIsDecrementedAfterOrder() {
        CartService cart = new CartService();
        cart.addItem("p1", 2);

        checkout.placeOrder(cart, catalog, 0.0);

        assertEquals(catalog.get("p1").getStock(), 3);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testEmptyCartThrows() {
        CartService cart = new CartService();
        checkout.placeOrder(cart, catalog, 0.0);
    }
}
