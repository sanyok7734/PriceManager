package com.managerprice.racconapps.app;


import android.content.DialogInterface;
import android.graphics.Color;
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

import com.managerprice.racconapps.app.adapters.AdapterDialog;
import com.managerprice.racconapps.app.adapters.AdapterPriceList;
import com.managerprice.racconapps.app.api.retriever.ProductRetrieverImpl;
import com.managerprice.racconapps.app.model.Product;
import com.managerprice.racconapps.app.model.Tag;
import com.managerprice.racconapps.app.task.ProductRetrievingTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Switch;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static Bus bus = new Bus();

    public static final String STORE_FILE = "store.json";
    public static final String URL_ROZETKA = "http://soft.rozetka.com.ua/avast_pro_4820153970342/p641415/";

    private RecyclerView listProduct;
    private AdapterPriceList adapterPriceList;
    private LinearLayoutManager linearLayoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ProductRetrieverImpl retriever;

    private Switch aSwitch;

    private List<Tag> titleClass = new ArrayList<>();
    private List<Tag> titleId = new ArrayList<>();
    private List<Tag> priceClass = new ArrayList<>();
    private List<Tag> priceId = new ArrayList<>();

    private AdapterDialog adapterDialog;

    private List<String> infoProduct = new ArrayList<>();

    public Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bus.register(MainActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        // делаем повеселее
        swipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.green, R.color.yellow, R.color.red);


        listProduct = (RecyclerView) findViewById(R.id.list_product);

        adapterPriceList = new AdapterPriceList(new ArrayList<Product>(), getApplicationContext());
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

        for (int i = 0; i < 10; i++) {
            titleClass.add(new Tag("id" + i, "olollo" + i, false));
            titleId.add(new Tag("olo" + i, "bla-bla" + i, true));
            priceClass.add(new Tag("-" + i, "bla-" + i, false));
            priceId.add(new Tag("*" + i, "bla+" + i, true));
        }
    }

    private void showDialogAddURL() {
        infoProduct = new ArrayList<>();
        final File storeFile = new File(getApplicationContext().getExternalFilesDir(null).getAbsolutePath() + STORE_FILE);
        final MaterialEditText materialEditText = new MaterialEditText(this);
        materialEditText.setHint("URL");
        materialEditText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        materialEditText.setPrimaryColor(Color.parseColor("#2196F3"));
        materialEditText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        materialEditText.setText(URL_ROZETKA);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set address URL");

        builder.setView(getLinerLayout(materialEditText));

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (true) { // WTF???
                    retriever = new ProductRetrieverImpl(materialEditText.getText().toString());
                    ProductRetrievingTask task = new ProductRetrievingTask(builder.getContext(), retriever);
                    task.execute();
                    showDialogTitle(titleClass, "Set title", "titleClass");
                    Log.d("Madness", retriever.getUrl());
                } else {
                    Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("CANCEL", null);

        builder.show();
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
                Boolean wantToCloseDialog = false;

                if (tag.equals("priceClass")) {
                    wantToCloseDialog = getActive(priceId, priceClass);
                } else {
                    wantToCloseDialog = getActive(titleClass, titleId);
                }

                if (wantToCloseDialog) {
                    if (tag.equals("priceClass")) {
                        adapterPriceList.addItem(new Product(getInfo(titleId, titleClass).get(0), getInfo(priceClass, priceId).get(0)));
                        Toast.makeText(MainActivity.this, "" + getInfo(titleId, titleClass).get(2) + " ----- " + getInfo(priceClass, priceId).get(2), Toast.LENGTH_SHORT).show();
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
                //тут список тегов для titleClass
                //TODO тут меняеться список в зависимости от переключателя id / class,
                // первый список в аргументах это список класов второй список id
                // в else аналогично только для цены.
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
                    infoProduct.add("" + tag.isClassOrID());
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
        switch (item.getItemId()) {
            case 0:
                Toast.makeText(MainActivity.this, "change", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(MainActivity.this, "delete" , Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Subscribe
    public void getProduct(Product product) {
        this.product = product;
    }

    //TODO  priceClass refresh
    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        bus.unregister(MainActivity.this);
        super.onDestroy();
    }

}
