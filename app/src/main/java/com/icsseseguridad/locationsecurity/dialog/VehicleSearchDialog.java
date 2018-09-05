package com.icsseseguridad.locationsecurity.dialog;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.TextView;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.adapter.VehicleModelAdapter;
import com.icsseseguridad.locationsecurity.model.Visitor;
import com.icsseseguridad.locationsecurity.model.VisitorVehicle;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.FilterResultListener;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;

public class VehicleSearchDialog<T extends Searchable> extends BaseSearchDialogCompat<T> {

    private String mTitle;
    private String mSearchHint;
    private SearchResultListener<T> mSearchResultListener;

    public VehicleSearchDialog(Context context, String title, String searchHint,
                               @Nullable Filter filter, ArrayList<T> items,
                               SearchResultListener<T> searchResultListener) {
        super(context, items, filter, null,null);
        init(title, searchHint, searchResultListener);
    }

    private void init(String title, String searchHint,
                      SearchResultListener<T> searchResultListener){
        mTitle = title;
        mSearchHint = searchHint;
        mSearchResultListener = searchResultListener;
    }

    @Override
    protected void getView(View view) {
        setContentView(view);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(true);
        TextView txtTitle = view.findViewById(ir.mirrajabi.searchdialog.R.id.txt_title);
        final EditText searchBox = view.findViewById(getSearchBoxId());
        txtTitle.setText(mTitle);
        searchBox.setHint(mSearchHint);
        searchBox.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });
        view.findViewById(ir.mirrajabi.searchdialog.R.id.dummy_background)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
        final VehicleModelAdapter adapter = new VehicleModelAdapter<>(getContext(),
                R.layout.item_search, getItems());
        adapter.setSearchResultListener(mSearchResultListener);
        adapter.setSearchDialog(this);
        setFilterResultListener(new FilterResultListener<T>() {
            @Override
            public void onFilter(ArrayList<T> items) {
                System.out.println("on filter");
                ((VehicleModelAdapter) getAdapter()).setSearchTag(searchBox.getText().toString()).setItems(customFilter((searchBox.getText().toString())));
            }
        });
        setAdapter(adapter);
    }

    public VehicleSearchDialog setTitle(String title) {
        mTitle = title;
        return this;
    }

    public VehicleSearchDialog setSearchHint(String searchHint) {
        mSearchHint = searchHint;
        return this;
    }

    public VehicleSearchDialog setSearchResultListener(
            SearchResultListener<T> searchResultListener) {
        mSearchResultListener = searchResultListener;
        return this;
    }

    @LayoutRes
    @Override
    protected int getLayoutResId() {
        return ir.mirrajabi.searchdialog.R.layout.search_dialog_compat;
    }

    @IdRes
    @Override
    protected int getSearchBoxId() {
        return ir.mirrajabi.searchdialog.R.id.txt_search;
    }

    @IdRes
    @Override
    protected int getRecyclerViewId() {
        return ir.mirrajabi.searchdialog.R.id.rv_items;
    }

    public ArrayList<T> customFilter(String text) {
        ArrayList<T> filteredList = new ArrayList<>();
        for (T item : getItems()) {
            if (normalize(((VisitorVehicle) item).plate).contains(normalize(text))
                    || ((VisitorVehicle) item).id == null){
                filteredList.add(item);
            }
        }
        System.out.println("tamaño: "+ filteredList.size());
        return filteredList;
    }

    private String normalize(String input) {
        if (input == null) { return ""; }
        return input.replaceAll("[^a-zA-Z0-9]+","").toLowerCase();
    }
}
