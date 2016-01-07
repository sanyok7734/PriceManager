package com.managerprice.racconapps.managerprice.model;

/**
 * Created by sanyok on 03.01.16.
 */
public class Product {
    private String title;
    private String price;

    private String titleTag;
    private String priceTag;


    public Product(String title, String price) {
        this.title = title;
        this.price = price;
    }

    public String getTitleTag() {
        return titleTag;
    }

    public void setTitleTag(String titleTag) {
        this.titleTag = titleTag;
    }

    public String getPriceTag() {
        return priceTag;
    }

    public void setPriceTag(String priceTag) {
        this.priceTag = priceTag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
