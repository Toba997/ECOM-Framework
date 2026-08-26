package com.example.ecom.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class CartService {

    private final Map<String, Integer> items = new LinkedHashMap<>();

    public void addItem(String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        items.merge(productId, quantity, Integer::sum);
    }

    public void removeItem(String productId) {
        items.remove(productId);
    }

    public int getQuantity(String productId) {
        return items.getOrDefault(productId, 0);
    }

    public Map<String, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
    }
}
