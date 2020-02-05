package com.tmsoft.tm.elitrends.Holders;

public class buyForMeClass {
    private String userId, userName, userProfilePicture, productName, productUrgent, productQuantity, orderDate, orderTime, message;

    public buyForMeClass() {
    }

    public buyForMeClass(String userId, String userName, String userProfilePicture, String productName, String productUrgent, String productQuantity, String orderDate, String orderTime, String message) {
        this.userId = userId;
        this.userName = userName;
        this.userProfilePicture = userProfilePicture;
        this.productName = productName;
        this.productUrgent = productUrgent;
        this.productQuantity = productQuantity;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(String userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductUrgent() {
        return productUrgent;
    }

    public void setProductUrgent(String productUrgent) {
        this.productUrgent = productUrgent;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
