package com.icsseseguridad.locationsecurity.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.events.OnClickVisit;
import com.icsseseguridad.locationsecurity.model.ControlVisit;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.centerCropTransform;

public class VisitAdapter extends RecyclerView.Adapter<VisitAdapter.ViewHolder> {

    protected Context mContext;
    private List<ControlVisit> visits;

    public VisitAdapter(Context context, List<ControlVisit> items) {
        this.visits = items;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_visit, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (!visits.isEmpty()) {
            final ControlVisit visit = visits.get(position);
            ImageView image = holder.getViewById(R.id.image);
            TextView title = holder.getViewById(R.id.title);
            TextView subTitle = holder.getViewById(R.id.sub_title);
            title.setText(visit.visitor.getFullname());
            subTitle.setText(DateUtils.getRelativeTimeSpanString(visit.createDate.getTime()));
            if (visit.vehicle != null && visit.vehicle.photo != null)
                Glide.with(mContext)
                        .load(visit.vehicle.photo)
                        .apply(centerCropTransform()
                                .placeholder(R.drawable.empty_image)
                                .error(R.drawable.empty_image))
                        .into(image);
            else
                if (visit.visitor != null && visit.visitor.photo != null)
                    Glide.with(mContext)
                            .load(visit.visitor.photo)
                            .apply(centerCropTransform()
                                    .placeholder(R.drawable.empty_image)
                                    .error(R.drawable.empty_image))
                            .into(image);
            holder.getBaseView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new OnClickVisit(visit));
                }
            });
        }
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
            return (T)mBaseView.findViewById(id);
        }

        public void clearAnimation(@IdRes int id) {
            mBaseView.findViewById(id).clearAnimation();
        }
    }

    public interface AdapterViewBinder<T> {
        void bind(ViewHolder holder, T item, int position);
    }

    public List<ControlVisit> getItems() {
        return visits;
    }

    public void setItems(List<ControlVisit> objects) {
        this.visits = objects;
        notifyDataSetChanged();
    }

    public ControlVisit getItem(int position) {
        return visits.get(position);
    }

    @Override
    public int getItemCount() {
        return visits.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void replaceAll(List<ControlVisit> list) {
        this.visits = new ArrayList<>(list);
        notifyDataSetChanged();
    }
}
