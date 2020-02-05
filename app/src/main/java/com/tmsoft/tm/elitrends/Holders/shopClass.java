package com.tmsoft.tm.elitrends.Holders;

public class shopClass {
    String shopName, shopLogo, shopNumber;

    public shopClass() {
    }

    public shopClass(String shopName, String shopLogo, String shopNumber) {
        this.shopName = shopName;
        this.shopLogo = shopLogo;
        this.shopNumber = shopNumber;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopLogo() {
        return shopLogo;
    }

    public void setShopLogo(String shopLogo) {
        this.shopLogo = shopLogo;
    }

    public String getShopNumber() {
        return shopNumber;
    }

    public void setShopNumber(String shopNumber) {
        this.shopNumber = shopNumber;
    }
}
