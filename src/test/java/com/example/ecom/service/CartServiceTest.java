package com.example.ecom.service;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class CartServiceTest {

    private CartService cart;

    @BeforeMethod
    public void setUp() {
        cart = new CartService();
    }

    @Test
    public void testAddItemSetsQuantity() {
        cart.addItem("p1", 2);
        assertEquals(cart.getQuantity("p1"), 2);
    }

    @Test
    public void testAddingSameProductAccumulates() {
        cart.addItem("p1", 1);
        cart.addItem("p1", 3);
        assertEquals(cart.getQuantity("p1"), 4);
    }

    @Test
    public void testRemoveItem() {
        cart.addItem("p1", 2);
        cart.removeItem("p1");
        assertEquals(cart.getQuantity("p1"), 0);
        assertTrue(cart.isEmpty());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddingNonPositiveQuantityThrows() {
        cart.addItem("p1", -1);
    }

    @Test
    public void testClearEmptiesCart() {
        cart.addItem("p1", 1);
        cart.addItem("p2", 2);
        cart.clear();
        assertTrue(cart.isEmpty());
    }
}
