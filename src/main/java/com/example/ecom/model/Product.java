package com.example.ecom.model;

public class Product {
    private final String id;
    private final String name;
    private final double price;
    private int stock;

    public Product(String id, String name, double price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }

    public void decreaseStock(int amount) {
        if (amount > stock) {
            throw new IllegalArgumentException("Not enough stock for " + id);
        }
        this.stock -= amount;
    }
}
