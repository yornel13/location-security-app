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
import com.icsseseguridad.locationsecurity.model.Reply;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.RequestOptions.centerCropTransform;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {

    protected Context mContext;
    private List<Reply> replies;

    public ReplyAdapter(Context context, List<Reply> items) {
        this.replies = items;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (!replies.isEmpty()) {
            final Reply reply = replies.get(position);
            ImageView image = holder.getViewById(R.id.user_image);
            TextView name = holder.getViewById(R.id.user_name);
            TextView comment = holder.getViewById(R.id.user_comment);
            TextView time = holder.getViewById(R.id.comment_time);
            comment.setText(reply.text);
            time.setText(DateUtils.getRelativeTimeSpanString(reply.createDate.getTime()));
            name.setText(reply.userName);
            if (reply.adminId != null) {
                image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.admin_user));
                if (reply.admin != null) {
                    name.setText(reply.admin.getFullname());
                }
            } else {
                image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.policeman));
                if (reply.guard != null) {
                    name.setText(reply.guard.getFullname());
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

    public List<Reply> getItems() {
        return replies;
    }

    public void setItems(List<Reply> objects) {
        this.replies = objects;
        notifyDataSetChanged();
    }

    public Reply getItem(int position) {
        return replies.get(position);
    }

    @Override
    public int getItemCount() {
        return replies.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void replaceAll(List<Reply> list) {
        this.replies = new ArrayList<>(list);
        notifyDataSetChanged();
    }
}
