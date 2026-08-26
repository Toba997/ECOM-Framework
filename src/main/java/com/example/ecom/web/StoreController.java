package com.example.ecom.web;

import com.example.ecom.persistence.ProductEntity;
import com.example.ecom.persistence.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/store")
public class StoreController {
    private static final String CART = "cart";
    private final ProductRepository products;

    public StoreController(ProductRepository products) {
        this.products = products;
    }

    @GetMapping("/products")
    public List<ProductEntity> products() {
        return products.findAll();
    }

    @GetMapping("/cart")
    public Map<String, Integer> cart(HttpSession session) {
        return getCart(session);
    }

    @PostMapping("/cart/{id}")
    public Map<String, Integer> add(@PathVariable String id, @RequestParam(defaultValue = "1") int quantity, HttpSession session) {
        if (quantity < 1) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be positive");
        ProductEntity product = products.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        Map<String, Integer> cart = getCart(session);
        int next = cart.getOrDefault(id, 0) + quantity;
        if (next > product.getStock()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Not enough stock");
        cart.put(id, next);
        return cart;
    }

    @DeleteMapping("/cart/{id}")
    public Map<String, Integer> remove(@PathVariable String id, HttpSession session) {
        getCart(session).remove(id);
        return getCart(session);
    }

    @PostMapping("/checkout")
    public Map<String, Object> checkout(HttpSession session) {
        Map<String, Integer> cart = getCart(session);
        if (cart.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        double total = 0;
        for (Map.Entry<String, Integer> item : cart.entrySet()) {
            ProductEntity product = products.findById(item.getKey()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
            if (item.getValue() > product.getStock()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Not enough stock");
            total += product.getPrice() * item.getValue();
        }
        for (Map.Entry<String, Integer> item : cart.entrySet()) {
            ProductEntity product = products.findById(item.getKey()).orElseThrow();
            product.setStock(product.getStock() - item.getValue());
            products.save(product);
        }
        int itemCount = cart.values().stream().mapToInt(Integer::intValue).sum();
        cart.clear();
        return new LinkedHashMap<>(Map.of("status", "PLACED", "items", itemCount, "total", total));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Integer> getCart(HttpSession session) {
        Object value = session.getAttribute(CART);
        if (value == null) {
            Map<String, Integer> cart = new LinkedHashMap<>();
            session.setAttribute(CART, cart);
            return cart;
        }
        return (Map<String, Integer>) value;
    }
}
