package com.raccoonapps.pricemanager.app;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.raccoonapps.pricemanager.app.api.model.ProductItem;
import com.raccoonapps.pricemanager.app.api.model.Selector;
import com.raccoonapps.pricemanager.app.api.model.SimpleOperations;
import com.raccoonapps.pricemanager.app.api.model.Store;
import com.raccoonapps.pricemanager.app.api.retriever.ProductRetrieverImpl;
import com.raccoonapps.pricemanager.app.api.storage.ProductStorageJsonImpl;
import com.raccoonapps.pricemanager.app.api.storage.SelectorStorage;
import com.raccoonapps.pricemanager.app.api.storage.StoreStorageJsonImpl;
import com.raccoonapps.pricemanager.app.client.adapters.AdapterDialog;
import com.raccoonapps.pricemanager.app.client.adapters.AdapterPriceList;
import com.raccoonapps.pricemanager.app.client.model.Tag;
import com.raccoonapps.pricemanager.app.client.task.ProductRetrievingTask;
import com.raccoonapps.pricemanager.app.client.task.ProductsUpdateTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Switch;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("All")
public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "MADNESS";
    public static Bus bus = new Bus();

    public static final String STORE_FILE = "store.json";
    public static final String PRODUCTS_FILE = "products.json";

    private RecyclerView listProduct;
    private AdapterPriceList adapterPriceList;
    private LinearLayoutManager linearLayoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ProductRetrieverImpl retriever;

    private Switch aSwitch;

    private File storeFile;
    private File productsFile;

    private ProductRetrievingTask mainTask;

    private List<Tag> titleClass = new ArrayList<>();
    private List<Tag> titleId = new ArrayList<>();
    private List<Tag> priceClass = new ArrayList<>();
    private List<Tag> priceId = new ArrayList<>();

    private AdapterDialog adapterDialog;

    private UUID idForUpdate;

    private String currentUrl;

    private List<String> infoProduct = new ArrayList<>();

    public ProductItem product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LocalDate.now().compareTo(LocalDate.parse("19.01.2016", DateTimeFormat.forPattern("DD.MM.YYYY"))) < 0) {
            Log.d(TAG, "Now is " + LocalDate.now());
            setContentView(R.layout.activity_main);
            bus.register(MainActivity.this);

            //stopService(new Intent(getBaseContext(), ProductsUpdatingService.class));
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            initializeAppDirectory();
            List<ProductItem> loadedList = loadProductsListFromJSON();
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
            swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.green, R.color.yellow, R.color.red);


            listProduct = (RecyclerView) findViewById(R.id.list_product);

            adapterPriceList = new AdapterPriceList(loadedList, getApplicationContext());
            linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            listProduct.setLayoutManager(linearLayoutManager);
            listProduct.setAdapter(adapterPriceList);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialogAddURL();
                }
            });
        } else
            Log.d(TAG, "Date doesn't match");
    }

    private List<ProductItem> loadProductsListFromJSON() {
        ProductStorageJsonImpl productStorage = new ProductStorageJsonImpl(productsFile);
        return productStorage.getItemsList();
    }

    private void initializeAppDirectory() {
        File externalFilesDir = new File(getApplicationContext().getExternalFilesDir(null).getAbsolutePath());
        if (!externalFilesDir.exists())
            externalFilesDir.mkdir();
        storeFile = new File(externalFilesDir + "/" + STORE_FILE);
        productsFile = new File(externalFilesDir + "/" + PRODUCTS_FILE);
        try {
            if (!storeFile.exists()) {
                storeFile.createNewFile();
                SimpleOperations.INSTANCE.writeJSONToFile(new JSONObject().toString(), storeFile);
            }
            if (!productsFile.exists()) {
                productsFile.createNewFile();
                SimpleOperations.INSTANCE.writeJSONToFile(new JSONObject().toString(), productsFile);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showDialogAddURL() {
        infoProduct = new ArrayList<>();
        final MaterialEditText materialEditText = new MaterialEditText(this);
        materialEditText.setHint("URL");
        materialEditText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        materialEditText.setPrimaryColor(Color.parseColor("#2196F3"));
        materialEditText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set address URL");

        builder.setView(getLinerLayout(materialEditText));

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentUrl = materialEditText.getText().toString();
                if (!currentUrl.startsWith("http://")) {
                    currentUrl = "http://" + currentUrl;
                }
                retriever = new ProductRetrieverImpl(currentUrl);
                mainTask = new ProductRetrievingTask(MainActivity.this, retriever);
                mainTask.execute();
            }
        });

        builder.setNegativeButton("CANCEL", null);

        builder.show();
    }

    @Subscribe
    public void startDialog(Boolean value) {
        ProductRetrieverImpl retriever = mainTask.getRetriever();
        ProductItem itemTry = retriever.tryRetrieveExistingValues(storeFile);
        if (itemTry != null) {
            ProductStorageJsonImpl productStorage = new ProductStorageJsonImpl(productsFile);
            adapterPriceList.addItem(itemTry);
            productStorage.addItem(itemTry);
            SimpleOperations.INSTANCE.writeJSONToFile(productStorage.getProductsJSON().toString(), productsFile);
        } else {
            Map<String, String> mapClass = retriever.getBySelector(Selector.CLASS);
            Map<String, String> mapId = retriever.getBySelector(Selector.ID);
            titleClass = new ArrayList<>();
            titleId = new ArrayList<>();
            priceClass = new ArrayList<>();
            priceId = new ArrayList<>();

            fillLists(titleClass, priceClass, mapClass, false);
            fillLists(titleId, priceId, mapId, true);

            showDialogTitle(titleClass, "Set title", "titleClass");
        }
    }

    private void fillLists(List<Tag> list1, List<Tag> list2, Map<String, String> map, boolean cond) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            list1.add(new Tag(entry.getKey(), entry.getValue(), cond));
            list2.add(new Tag(entry.getKey(), entry.getValue(), cond));
        }
    }

    private void showDialogTitle(List<Tag> tags, String name, final String tag) {
        RecyclerView recyclerView = new RecyclerView(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterDialog = new AdapterDialog(tags, this);
        recyclerView.setAdapter(adapterDialog);
        recyclerView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        recyclerView.getItemAnimator().setChangeDuration(0);


        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout switchLinerLayout = (LinearLayout) inflater.inflate(R.layout.switch_layout, null, false);
        aSwitch = (Switch) switchLinerLayout.findViewById(R.id.mySwitch);
        aSwitch.setTag(tag);
        aSwitch.setOnCheckedChangeListener(onCheckedChangeListener);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(name);
        builder.setView(getLinerLayout(switchLinerLayout, recyclerView));
        builder.setPositiveButton("OK", null);

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog;

                if (tag.equals("priceClass")) {
                    wantToCloseDialog = getActive(priceId, priceClass);
                } else {
                    wantToCloseDialog = getActive(titleClass, titleId);
                }

                if (wantToCloseDialog) {
                    if (tag.equals("priceClass")) {
                        String priceSelector = getInfo(priceId, priceClass).get(2);
                        String priceSelectorValue = getInfo(priceId, priceClass).get(1);
                        String titleSelector = getInfo(titleId, titleClass).get(2);
                        String titleSelectorValue = getInfo(titleId, titleClass).get(1);
                        SelectorStorage selectorStorage = new SelectorStorage();
                        selectorStorage.setPriceSelector(priceSelector);
                        selectorStorage.setPriceSelectorValue(priceSelectorValue);
                        selectorStorage.setTitleSelector(titleSelector);
                        selectorStorage.setTitleSelectorValue(titleSelectorValue);
                        String urlValue = currentUrl.split("/")[2];
                        Store store = new Store(UUID.randomUUID(), urlValue, selectorStorage);
                        StoreStorageJsonImpl storeStorage = new StoreStorageJsonImpl(storeFile);
                        storeStorage.addItem(store);
                        SimpleOperations.INSTANCE.writeJSONToFile(storeStorage.getStoresJson().toString(), storeFile);
                        ProductItem productItem = new ProductItem(UUID.randomUUID(), store.getId(), getInfo(titleId, titleClass).get(0), getInfo(priceClass, priceId).get(0), currentUrl, LocalDateTime.now());
                        adapterPriceList.addItem(productItem);
                        ProductStorageJsonImpl productStorage = new ProductStorageJsonImpl(productsFile);
                        productStorage.addItem(productItem);
                        SimpleOperations.INSTANCE.writeJSONToFile(productStorage.getProductsJSON().toString(), productsFile);
                        resetList(titleClass, titleId, priceClass, priceId);
                    } else {
                        showDialogTitle(priceClass, "Set Price", "priceClass");
                    }

                    dialog.dismiss();
                }

            }
        });
    }


    Switch.OnCheckedChangeListener onCheckedChangeListener = new Switch.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(Switch view, boolean checked) {
            if (view.getTag().equals("titleClass")) {
                setList(checked, titleClass, titleId);
            } else {
                setList(checked, priceClass, priceId);
            }
        }
    };

    private LinearLayout getLinerLayout(View... views) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setPadding(20, 20, 20, 0);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        for (int i = 0; i < views.length; i++) {
            linearLayout.addView(views[i]);
        }
        return linearLayout;
    }

    private void setList(boolean checked, List<Tag> classList, List<Tag> idList) {
        if (checked) /* false = class; true = id */ {
            resetList(idList);
            adapterDialog.setStringMap(idList);
            adapterDialog.notifyDataSetChanged();
        } else {
            resetList(classList);
            adapterDialog.setStringMap(classList);
            adapterDialog.notifyDataSetChanged();
        }
    }

    private void resetList(List<Tag>... list) {
        for (List<Tag> tags : list) {
            for (Tag tag : tags) {
                tag.setIsActive(Color.parseColor("#ffffff"));
            }
        }
    }

    private List<String> getInfo(List<Tag>... list) {
        List<String> infoProduct = new ArrayList<>();
        for (List<Tag> tags : list) {
            for (Tag tag : tags) {
                if (tag.getIsActive() == Color.parseColor("#6816b5ff")) {
                    infoProduct.add(tag.getText());
                    infoProduct.add(tag.getId());
                    infoProduct.add(tag.isClassOrID() ? Selector.ID.getSelectorType() : Selector.CLASS.getSelectorType());
                }
            }
        }
        return infoProduct;
    }

    private boolean getActive(List<Tag>... list) {
        boolean isActive = false;
        for (List<Tag> tags : list) {
            for (Tag tag : tags) {
                if (tag.getIsActive() == Color.parseColor("#6816b5ff")) {
                    isActive = true;
                }
            }
        }
        return isActive;
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ProductStorageJsonImpl productStorage = new ProductStorageJsonImpl(productsFile);
        switch (item.getItemId()) {
            case 0:
                Toast.makeText(MainActivity.this, "Change", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(MainActivity.this, "Delete product " + product.getTitle(), Toast.LENGTH_SHORT).show();
                productStorage.deleteItem(product.getId());
                updatePriceListAdapter(productStorage.getItemsList());
                SimpleOperations.INSTANCE.writeJSONToFile(productStorage.getProductsJSON().toString(), productsFile);
                break;
            case 2:
                // TODO update selected product
                Toast.makeText(MainActivity.this, "Selected product: " + product.getTitle(), Toast.LENGTH_SHORT).show();
                idForUpdate = product.getId();
                List<ProductItem> itemsForUpdate = Arrays.asList(product);
                new ProductsUpdateTask(itemsForUpdate, productsFile, storeFile, getApplicationContext()).execute();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void updatePriceListAdapter(List<ProductItem> products) {
        adapterPriceList = new AdapterPriceList(products, getApplicationContext());
        adapterPriceList.notifyDataSetChanged();
        listProduct.setAdapter(adapterPriceList);
    }

    @Subscribe
    public void getProduct(ProductItem product) {
        this.product = product;
    }

    @Subscribe
    public void getSelectedItemUUID(UUID uuid) {
        openInBrowser(uuid);
    }


    private void openInBrowser(UUID uuid) {
        ProductItem product = new ProductStorageJsonImpl(productsFile).get(uuid);
        if (product != null)
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(product.getLink())));
    }

    @Override
    public void onRefresh() {
        if (SimpleOperations.INSTANCE.isNetworkAvailable(getApplicationContext())) {
            swipeRefreshLayout.setRefreshing(true);
            new ProductsUpdateTask(null, productsFile, storeFile, getApplicationContext()).execute();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(MainActivity.this, "No WiFi connection available", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void refreshList(ArrayList<ProductItem> items) {
        swipeRefreshLayout.setRefreshing(false);
        Log.d(TAG, "In refreshList");
        if (!items.isEmpty()) {
            ProductStorageJsonImpl productStorage = new ProductStorageJsonImpl(productsFile);
            if (items.size() > 1) {
                Log.d(TAG, "1");
                productStorage.updateItemsList(items);
                SimpleOperations.INSTANCE.writeJSONToFile(productStorage.getProductsJSON().toString(), productsFile);
                updatePriceListAdapter(items);
            } else if (items.size() == 1) {
                Log.d(TAG, "2");
                ProductItem updatedItem = items.get(0);
                updatedItem.setId(idForUpdate);
                productStorage.updateItem(idForUpdate, updatedItem);
                SimpleOperations.INSTANCE.writeJSONToFile(productStorage.getProductsJSON().toString(), productsFile);
                updatePriceListAdapter(productStorage.getItemsList());
                for (ProductItem item : productStorage.getItemsList())
                    Log.d(TAG, item.getLastUpdate() + "");
            }
        } else {
            Toast.makeText(MainActivity.this, "Error occurred while updating", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        bus.unregister(MainActivity.this);
        super.onDestroy();
    }

}
