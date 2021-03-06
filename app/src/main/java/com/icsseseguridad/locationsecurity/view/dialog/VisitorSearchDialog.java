package com.icsseseguridad.locationsecurity.view.dialog;

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
import com.icsseseguridad.locationsecurity.view.adapter.VisitorModelAdapter;
import com.icsseseguridad.locationsecurity.service.entity.Visitor;

import java.text.Normalizer;
import java.util.ArrayList;

import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.FilterResultListener;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;

public class VisitorSearchDialog<T extends Searchable> extends BaseSearchDialogCompat<T> {

    private String mTitle;
    private String mSearchHint;
    private SearchResultListener<T> mSearchResultListener;

    public VisitorSearchDialog(Context context, String title, String searchHint,
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
        final VisitorModelAdapter adapter = new VisitorModelAdapter<>(getContext(),
                R.layout.item_search, getItems());
        adapter.setSearchResultListener(mSearchResultListener);
        adapter.setSearchDialog(this);
        setFilterResultListener(new FilterResultListener<T>() {
            @Override
            public void onFilter(ArrayList<T> items) {

                ((VisitorModelAdapter) getAdapter())
                        .setSearchTag(searchBox.getText().toString())
                        .setItems(customFilter((searchBox.getText().toString())));
            }
        });
        setAdapter(adapter);
    }

    public VisitorSearchDialog setTitle(String title) {
        mTitle = title;
        return this;
    }

    public VisitorSearchDialog setSearchHint(String searchHint) {
        mSearchHint = searchHint;
        return this;
    }

    public VisitorSearchDialog setSearchResultListener(
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
            if (normalize(((Visitor) item).dni).contains(normalize(text))
                    || ((Visitor) item).id == null || normalize(((Visitor) item).getFullname()).contains(normalize(text))){
                filteredList.add(item);
            }
        }
        return filteredList;
    }

    private String normalize(String input) {
        if (input == null) { return ""; }
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[^a-zA-Z0-9]+","").toLowerCase();
    }
}
