package com.tmsoft.tm.elitrends.Holders;

public class SpecialProductClass {
    private String ProductName, ProductPrice, ProductActualPrice, ProductPicture1, ProductKey, ProductCategory;

    public SpecialProductClass() {
    }

    public SpecialProductClass(String productName, String productPrice, String productActualPrice, String productPicture1, String productKey, String productCategory) {
        ProductName = productName;
        ProductPrice = productPrice;
        ProductActualPrice = productActualPrice;
        ProductPicture1 = productPicture1;
        ProductKey = productKey;
        ProductCategory = productCategory;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(String productPrice) {
        ProductPrice = productPrice;
    }

    public String getProductActualPrice() {
        return ProductActualPrice;
    }

    public void setProductActualPrice(String productActualPrice) {
        ProductActualPrice = productActualPrice;
    }

    public String getProductPicture1() {
        return ProductPicture1;
    }

    public void setProductPicture1(String productPicture1) {
        ProductPicture1 = productPicture1;
    }

    public String getProductKey() {
        return ProductKey;
    }

    public void setProductKey(String productKey) {
        ProductKey = productKey;
    }

    public String getProductCategory() {
        return ProductCategory;
    }

    public void setProductCategory(String productCategory) {
        ProductCategory = productCategory;
    }
}
