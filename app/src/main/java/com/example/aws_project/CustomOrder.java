package com.example.aws_project;

public class CustomOrder {
    private String orderID;
    private String userID;
    private String itemName;
    private String itemPrice;
    private String orderDate;

    public CustomOrder(String orderID, String userID, String itemName, String itemPrice, String orderDate) {
        this.orderID = orderID;
        this.userID = userID;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.orderDate = orderDate;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getUserID() {
        return userID;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public String getOrderDate() {
        return orderDate;
    }

}
