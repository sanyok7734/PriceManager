package com.raccoonapps.pricemanager.app.api.storage;

/**
 * Class, which stores selectors for certain shop {@link com.raccoonapps.pricemanager.app.api.model.Selector}
 * */
public class SelectorStorage {

    private String titleSelector;

    private String priceSelector;

    private String titleSelectorValue;

    private String priceSelectorValue;

    public String getTitleSelector() {
        return titleSelector;
    }

    public void setTitleSelector(String titleSelector) {
        this.titleSelector = titleSelector;
    }

    public String getPriceSelector() {
        return priceSelector;
    }

    public void setPriceSelector(String priceSelector) {
        this.priceSelector = priceSelector;
    }

    public String getTitleSelectorValue() {
        return titleSelectorValue;
    }

    public void setTitleSelectorValue(String titleSelectorValue) {
        this.titleSelectorValue = titleSelectorValue;
    }

    public String getPriceSelectorValue() {
        return priceSelectorValue;
    }

    public void setPriceSelectorValue(String priceSelectorValue) {
        this.priceSelectorValue = priceSelectorValue;
    }
}
