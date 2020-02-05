package com.tmsoft.tm.elitrends.Holders;

public class favoriteProducts {
    private String pProductName, pProductPrice, pProductDescription, pProductPicture1, pProductPicture2, pProductPicture3, UserId;

    public favoriteProducts() {
    }

    public favoriteProducts(String userId, String pProductName, String pProductPrice, String pProductDescription, String pProductPicture1, String pProductPicture2, String pProductPicture3) {
        this.UserId = userId;
        this.pProductName = pProductName;
        this.pProductPrice = pProductPrice;
        this.pProductDescription = pProductDescription;
        this.pProductPicture1 = pProductPicture1;
        this.pProductPicture2 = pProductPicture2;
        this.pProductPicture3 = pProductPicture3;
    }

    public String getpProductName() {
        return pProductName;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public void setpProductName(String pProductName) {
        this.pProductName = pProductName;
    }

    public String getpProductPrice() {
        return pProductPrice;
    }

    public void setpProductPrice(String pProductPrice) {
        this.pProductPrice = pProductPrice;
    }

    public String getpProductDescription() {
        return pProductDescription;
    }

    public void setpProductDescription(String pProductDescription) {
        this.pProductDescription = pProductDescription;
    }

    public String getpProductPicture1() {
        return pProductPicture1;
    }

    public void setpProductPicture1(String pProductPicture1) {
        this.pProductPicture1 = pProductPicture1;
    }

    public String getpProductPicture2() {
        return pProductPicture2;
    }

    public void setpProductPicture2(String pProductPicture2) {
        this.pProductPicture2 = pProductPicture2;
    }

    public String getpProductPicture3() {
        return pProductPicture3;
    }

    public void setpProductPicture3(String pProductPicture3) {
        this.pProductPicture3 = pProductPicture3;
    }
}
