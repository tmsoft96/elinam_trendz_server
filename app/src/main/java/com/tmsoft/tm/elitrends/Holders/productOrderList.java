package com.tmsoft.tm.elitrends.Holders;

public class productOrderList {
    private String productId, productPicture1, productName, productPrice, userId, userFullName,
            orderConfirm, deliverySuccess, orderDate, orderTime;

    public productOrderList() {
    }

    public productOrderList(String productId, String productPicture1, String productName, String productPrice, String userId, String userFullName, String orderConfirm, String deliverySuccess, String orderDate, String orderTime) {
        this.productId = productId;
        this.productPicture1 = productPicture1;
        this.productName = productName;
        this.productPrice = productPrice;
        this.userId = userId;
        this.userFullName = userFullName;
        this.orderConfirm = orderConfirm;
        this.deliverySuccess = deliverySuccess;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductPicture1() {
        return productPicture1;
    }

    public void setProductPicture1(String productPicture1) {
        this.productPicture1 = productPicture1;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getOrderConfirm() {
        return orderConfirm;
    }

    public void setOrderConfirm(String orderConfirm) {
        this.orderConfirm = orderConfirm;
    }

    public String getDeliverySuccess() {
        return deliverySuccess;
    }

    public void setDeliverySuccess(String deliverySuccess) {
        this.deliverySuccess = deliverySuccess;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }
}
