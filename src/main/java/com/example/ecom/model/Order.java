package com.example.ecom.model;

import java.util.ArrayList;
import java.util.List;

public class Order {

    public static class Line {
        private final String productId;
        private final int quantity;
        private final double unitPrice;

        public Line(String productId, int quantity, double unitPrice) {
            this.productId = productId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public String getProductId() { return productId; }
        public int getQuantity() { return quantity; }
        public double getUnitPrice() { return unitPrice; }
        public double getLineTotal() { return unitPrice * quantity; }
    }

    private final List<Line> lines = new ArrayList<>();
    private final double subtotal;
    private final double discount;
    private final double total;
    private final String status;

    public Order(List<Line> lines, double subtotal, double discount, double total, String status) {
        this.lines.addAll(lines);
        this.subtotal = subtotal;
        this.discount = discount;
        this.total = total;
        this.status = status;
    }

    public List<Line> getLines() { return lines; }
    public double getSubtotal() { return subtotal; }
    public double getDiscount() { return discount; }
    public double getTotal() { return total; }
    public String getStatus() { return status; }
}
