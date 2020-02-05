package com.tmsoft.tm.elitrends.Holders;

public class cartViewOrder {
    String userId, userFullName, userProfilePicture, orderDate, orderTime, orderConfirm, paymentAmountPaid,paymentConfirm, deliverySuccess;

    public cartViewOrder() {
    }

    public cartViewOrder(String userId, String userFullName, String userProfilePicture, String orderDate, String orderTime, String orderConfirm, String paymentAmountPaid, String paymentConfirm, String deliverySuccess) {
        this.userId = userId;
        this.userFullName = userFullName;
        this.userProfilePicture = userProfilePicture;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.orderConfirm = orderConfirm;
        this.paymentAmountPaid = paymentAmountPaid;
        this.paymentConfirm = paymentConfirm;
        this.deliverySuccess = deliverySuccess;
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

    public String getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(String userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
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

    public String getOrderConfirm() {
        return orderConfirm;
    }

    public void setOrderConfirm(String orderConfirm) {
        this.orderConfirm = orderConfirm;
    }

    public String getPaymentAmountPaid() {
        return paymentAmountPaid;
    }

    public void setPaymentAmountPaid(String paymentAmountPaid) {
        this.paymentAmountPaid = paymentAmountPaid;
    }

    public String getPaymentConfirm() {
        return paymentConfirm;
    }

    public void setPaymentConfirm(String paymentConfirm) {
        this.paymentConfirm = paymentConfirm;
    }

    public String getDeliverySuccess() {
        return deliverySuccess;
    }

    public void setDeliverySuccess(String deliverySuccess) {
        this.deliverySuccess = deliverySuccess;
    }
}
