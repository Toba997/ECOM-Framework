package com.example.ecom.service;

import com.example.ecom.exception.InsufficientStockException;
import com.example.ecom.model.Order;
import com.example.ecom.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckoutService {

    public Order placeOrder(CartService cart, Map<String, Product> catalog, double discountRate) {
        if (cart.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }
        if (discountRate < 0 || discountRate > 1) {
            throw new IllegalArgumentException("Discount rate must be between 0 and 1");
        }

        List<Order.Line> lines = new ArrayList<>();
        double subtotal = 0.0;

        // Validate everything first, so a failed order never mutates stock.
        for (Map.Entry<String, Integer> entry : cart.getItems().entrySet()) {
            String productId = entry.getKey();
            int quantity = entry.getValue();
            Product product = catalog.get(productId);
            if (product == null) {
                throw new IllegalArgumentException("Unknown product: " + productId);
            }
            if (product.getStock() < quantity) {
                throw new InsufficientStockException("Not enough stock for " + product.getName());
            }
            lines.add(new Order.Line(productId, quantity, product.getPrice()));
            subtotal += product.getPrice() * quantity;
        }

        double discount = subtotal * discountRate;
        double total = subtotal - discount;

        // Only after all validation passed, decrement stock and clear the cart.
        for (Order.Line line : lines) {
            catalog.get(line.getProductId()).decreaseStock(line.getQuantity());
        }
        cart.clear();

        return new Order(lines, subtotal, discount, total, "PLACED");
    }
}
