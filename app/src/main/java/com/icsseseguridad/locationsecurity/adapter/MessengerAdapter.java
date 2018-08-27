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

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.events.OnClickChat;
import com.icsseseguridad.locationsecurity.model.Chat;
import com.icsseseguridad.locationsecurity.util.AppPreferences;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MessengerAdapter extends RecyclerView.Adapter<MessengerAdapter.ViewHolder> {

    protected Context mContext;
    private List<Chat> reports;

    public MessengerAdapter(Context context, List<Chat> items) {
        this.reports = items;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (!reports.isEmpty()) {
            final Chat chat = reports.get(position);
            String userName = null;
            Chat.TYPE userType = null;
            AppPreferences preferences = new AppPreferences(mContext);
            if (chat.user1Id.equals(preferences.getGuard().id)) {
                userName = chat.user2Name;
                userType = chat.user2Type;
            } else {
                userName = chat.user1Name;
                userType = chat.user1Type;
            }

            ImageView image = holder.getViewById(R.id.image);
            TextView title = holder.getViewById(R.id.title);
            TextView subTitle = holder.getViewById(R.id.sub_title);
            TextView time = holder.getViewById(R.id.time);
            title.setText(userName);
            time.setText(DateUtils.getRelativeTimeSpanString(chat.createAt.getTime()));
            if (userType == Chat.TYPE.GUARD)
                image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.policeman));
            else
                image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.admin_user));

            holder.getBaseView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new OnClickChat(chat));
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

    public List<Chat> getItems() {
        return reports;
    }

    public void setItems(List<Chat> objects) {
        this.reports = objects;
        notifyDataSetChanged();
    }

    public Chat getItem(int position) {
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

    public void replaceAll(List<Chat> list) {
        this.reports = new ArrayList<>(list);
        notifyDataSetChanged();
    }
}
