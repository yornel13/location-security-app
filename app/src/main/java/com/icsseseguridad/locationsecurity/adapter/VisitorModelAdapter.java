package com.icsseseguridad.locationsecurity.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.model.Visitor;

import java.util.ArrayList;
import java.util.List;

import ir.mirrajabi.searchdialog.StringsHelper;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;

public class VisitorModelAdapter<T extends Searchable>
        extends RecyclerView.Adapter<VisitorModelAdapter.ViewHolder> {

    protected Context mContext;
    private List<T> mItems = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private int mLayout;
    private SearchResultListener mSearchResultListener;
    private AdapterViewBinder<T> mViewBinder;
    private String mSearchTag;
    private boolean mHighlightPartsInCommon = true;
    private String mHighlightColor = "#FFED2E47";
    private BaseSearchDialogCompat mSearchDialog;

    public VisitorModelAdapter(Context context, @LayoutRes int layout, List<T> items) {
        this(context,layout,null, items);
    }

    public VisitorModelAdapter(Context context, AdapterViewBinder<T> viewBinder,
                               @LayoutRes int layout, List<T> items) {
        this(context,layout,viewBinder, items);
    }

    public VisitorModelAdapter(Context context, @LayoutRes int layout,
                               @Nullable AdapterViewBinder<T> viewBinder,
                               List<T> items) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mItems = items;
        this.mLayout = layout;
        this.mViewBinder = viewBinder;
    }

    public List<T> getItems() {
        return mItems;
    }

    public void setItems(List<T> objects) {
        this.mItems = objects;
        notifyDataSetChanged();
    }

    public T getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public VisitorModelAdapter<T> setViewBinder(AdapterViewBinder<T> viewBinder) {
        this.mViewBinder = viewBinder;
        return this;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = mLayoutInflater.inflate(mLayout, parent, false);
        convertView.setTag(new ViewHolder(convertView));
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VisitorModelAdapter.ViewHolder holder, int position) {
        initializeViews(getItem(position), holder, position);
    }

    private void initializeViews(final T object, final VisitorModelAdapter.ViewHolder holder,
                                 final int position) {
        if(mViewBinder != null)
            mViewBinder.bind(holder, object, position);

        TextView text = holder.getViewById(R.id.text);
        TextView subText = holder.getViewById(R.id.sub_text);
        ImageView image = holder.getViewById(R.id.image);

        subText.setText(((Visitor) object).getFullname());
        subText.setVisibility(View.VISIBLE);
        if (((Visitor) object).id == null) {
            subText.setVisibility(View.GONE);
            image.setImageDrawable(mContext.getDrawable(R.drawable.ic_plus_color));
        } else {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.user_empty)
                    .error(R.drawable.user_empty)
                    .transform(new RoundedCorners(20));
            Glide.with(mContext)
                    .setDefaultRequestOptions(options)
                    .load(((Visitor) object).photo)
                    .into(image);
        }

        if(mSearchTag != null && mHighlightPartsInCommon)
            text.setText(StringsHelper.highlightLCS(object.getTitle(), getSearchTag(),
                    Color.parseColor(mHighlightColor)));
        else text.setText(object.getTitle());

        if (mSearchResultListener != null)
            holder.getBaseView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSearchResultListener.onSelected(mSearchDialog, object, position);
                }
            });
    }

    public SearchResultListener getSearchResultListener(){
        return mSearchResultListener;
    }
    public void setSearchResultListener(SearchResultListener searchResultListener){
        this.mSearchResultListener = searchResultListener;
    }

    public VisitorModelAdapter setSearchTag(String searchTag) {
        mSearchTag = searchTag;
        return this;
    }

    public String getSearchTag() {
        return mSearchTag;
    }

    public VisitorModelAdapter setHighlightPartsInCommon(boolean highlightPartsInCommon) {
        mHighlightPartsInCommon = highlightPartsInCommon;
        return this;
    }

    public boolean isHighlightPartsInCommon() {
        return mHighlightPartsInCommon;
    }

    public VisitorModelAdapter setHighlightColor(String highlightColor) {
        mHighlightColor = highlightColor;
        return this;
    }

    public VisitorModelAdapter setSearchDialog(BaseSearchDialogCompat searchDialog) {
        mSearchDialog = searchDialog;
        return this;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View mBaseView;

        public ViewHolder(View view) {
            super(view);
            mBaseView = view;
        }

        public View getBaseView() {
            return mBaseView;
        }

        public <T> T getViewById(@IdRes int id){
            return (T) mBaseView.findViewById(id);
        }

        public void clearAnimation(@IdRes int id) {
            mBaseView.findViewById(id).clearAnimation();
        }
    }

    public interface AdapterViewBinder<T> {
        void bind(ViewHolder holder, T item, int position);
    }
}
