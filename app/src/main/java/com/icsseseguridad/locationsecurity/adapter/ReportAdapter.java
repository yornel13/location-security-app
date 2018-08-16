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
import com.icsseseguridad.locationsecurity.SecurityApp;
import com.icsseseguridad.locationsecurity.events.OnClickReport;
import com.icsseseguridad.locationsecurity.model.Incidence;
import com.icsseseguridad.locationsecurity.model.SpecialReport;
import com.icsseseguridad.locationsecurity.util.AppPreferences;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.centerCropTransform;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    protected Context mContext;
    private List<SpecialReport> reports;

    public ReportAdapter(Context context, List<SpecialReport> items) {
        this.reports = items;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (!reports.isEmpty()) {
            final SpecialReport report = reports.get(position);
            ImageView image = holder.getViewById(R.id.image);
            TextView title = holder.getViewById(R.id.title);
            TextView subTitle = holder.getViewById(R.id.sub_title);
            TextView time = holder.getViewById(R.id.time);
            title.setText(report.title);
            subTitle.setText(report.observation);
            time.setText(DateUtils.getRelativeTimeSpanString(report.createDate.getTime()));
            if (report.image1 != null && !report.image1.isEmpty())
                Glide.with(mContext)
                        .load(report.image1)
                        .apply(centerCropTransform()
                                .placeholder(R.drawable.empty_image)
                                .error(R.drawable.empty_image))
                        .into(image);
            else
                image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.circle_report));
            holder.getBaseView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new OnClickReport(report));
                }
            });

            title.setTextColor(mContext.getResources().getColor(R.color.black));
            SecurityApp app = (SecurityApp) SecurityApp.getAppContext();
            if (app != null && app.incidences != null) {
                for (Incidence incidence: app.incidences.incidences) {
                    if (incidence.id.equals(report.incidenceId)) {
                        if (incidence.level > 1)
                            title.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                    }
                }
            }
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

    public List<SpecialReport> getItems() {
        return reports;
    }

    public void setItems(List<SpecialReport> objects) {
        this.reports = objects;
        notifyDataSetChanged();
    }

    public SpecialReport getItem(int position) {
        return reports.get(position);
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void replaceAll(List<SpecialReport> list) {
        this.reports = new ArrayList<>(list);
        notifyDataSetChanged();
    }
}
