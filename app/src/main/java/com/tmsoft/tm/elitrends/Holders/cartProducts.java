package com.tmsoft.tm.elitrends.Holders;

public class cartProducts {
    String postKey, quantity, category;

    public cartProducts() {
    }

    public cartProducts(String postKey, String quantity, String category) {
        this.postKey = postKey;
        this.quantity = quantity;
        this.category = category;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
