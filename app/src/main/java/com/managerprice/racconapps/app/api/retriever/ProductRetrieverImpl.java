package com.managerprice.racconapps.app.api.retriever;

import com.managerprice.racconapps.app.api.model.ProductItem;
import com.managerprice.racconapps.app.api.model.Selector;
import com.managerprice.racconapps.app.api.model.Store;
import com.managerprice.racconapps.app.api.storage.StoreStorageJsonImpl;

import org.joda.time.LocalDateTime;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of {@link ProductRetriever} interface,
 * which implements functionality for downloading web-page content,
 * and retrieving its components, such ids or classes.
 */
public class ProductRetrieverImpl implements ProductRetriever {

    private String url;

    private int timeout = 10000;

    /**
     * Variable for filtering components,
     * which we want retrieve
     */
    private int maximumCharactersCount = 300;

    private Document document;

    private Selector selector = Selector.ID;

    public ProductRetrieverImpl(String url) {
        this.url = url;
    }

    private Store checkIfStoreExists(File storesFile) throws JSONException {
        StoreStorageJsonImpl storage = new StoreStorageJsonImpl(storesFile);
        List<Store> stores = storage.getItemsList();
        for (Store store : stores) {
            if (url.contains(store.getStoreName()))
                return store;
        }
        return null;
    }

    public ProductRetrieverImpl(String url, int timeout) {
        this(url);
        this.timeout = timeout;
    }

    public ProductRetrieverImpl(String url, int timeout, int maximumCharactersCount) {
        this(url, timeout);
        this.maximumCharactersCount = maximumCharactersCount;
    }

    @Override
    public void retrieveWebPage() {
        try {
            this.document = Jsoup.connect(url).timeout(timeout).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getBySelector(Selector selector) {
        Elements selectedElements = document.select(selector.getSelectorType());
        return fillBySelectorType(selector, selectedElements);
    }

    private String getBySelectorWithKey(Selector selector, String key) {
        Elements selectedElements = document.select(selector.getSelectorType());
        return fillBySelectorType(selector, selectedElements).get(key);
    }

    public ProductItem tryRetrieveExistingValues(File storesFile) throws JSONException {
        Store store = checkIfStoreExists(storesFile);
        if (store != null) {
            return new ProductItem(UUID.randomUUID(), store.getId(),
                    getBySelectorWithKey(selector(store.getSelectorStorage().getTitleSelector()), store.getSelectorStorage().getTitleSelectorValue()),
                    getBySelectorWithKey(selector(store.getSelectorStorage().getPriceSelector()), store.getSelectorStorage().getPriceSelectorValue()),
                    url, LocalDateTime.now());
        }
        return null;
    }

    private Selector selector(String type) {
        return type.equals(Selector.ID.getSelectorType()) ? Selector.ID : Selector.CLASS;
    }

    /**
     * @param selector - Type of elements, what we've got,
     * @param selectedElements - retrieved elements,
     * @return Returns {@link Map}, which contains id or class, and value, associated with it
     */
    private Map<String, String> fillBySelectorType(Selector selector, Elements selectedElements) {
        Map<String, String> result = new HashMap<String, String>();
        for (Element element : selectedElements) {
            if (!element.text().isEmpty() && element.text().length() < maximumCharactersCount)
                result.put(selector.equals(Selector.ID) ? element.id() : element.className(), element.text());
        }
        return result;
    }

    public String getUrl() {
        return url;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getMaximumCharactersCount() {
        return maximumCharactersCount;
    }

    public void setMaximumCharactersCount(int maximumCharactersCount) {
        this.maximumCharactersCount = maximumCharactersCount;
    }
}
