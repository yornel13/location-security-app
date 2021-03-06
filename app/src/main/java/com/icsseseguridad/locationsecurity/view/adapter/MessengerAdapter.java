package com.icsseseguridad.locationsecurity.view.adapter;

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
import com.icsseseguridad.locationsecurity.service.event.OnClickChannel;
import com.icsseseguridad.locationsecurity.service.event.OnClickChannelAdd;
import com.icsseseguridad.locationsecurity.service.event.OnClickChat;
import com.icsseseguridad.locationsecurity.service.entity.ChannelRegistered;
import com.icsseseguridad.locationsecurity.service.entity.Chat;
import com.icsseseguridad.locationsecurity.util.AppPreferences;
import com.icsseseguridad.locationsecurity.util.UTILITY;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MessengerAdapter extends RecyclerView.Adapter<MessengerAdapter.ViewHolder> {

    protected Context mContext;
    private List<Object> list;

    private static final int ITEM_VIEW_CHAT = 0;
    private static final int ITEM_VIEW_CHANNEL = 1;

    public MessengerAdapter(Context context, List<Object> items) {
        this.list = items;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (!list.isEmpty()) {

            AppPreferences preferences = new AppPreferences(mContext);
            ImageView image = holder.getViewById(R.id.image);
            TextView title = holder.getViewById(R.id.title);
            TextView subTitle = holder.getViewById(R.id.sub_title);
            TextView time = holder.getViewById(R.id.time);
            TextView unread = holder.getViewById(R.id.unread);
            ImageView addUsers = holder.getViewById(R.id.add_users);

            if (getItemViewType(position) == ITEM_VIEW_CHAT) {
                addUsers.setVisibility(View.GONE);
                final Chat chat = (Chat) list.get(position);
                String userName = null;
                Chat.TYPE userType = null;
                if (chat.user1Id.equals(preferences.getGuard().id)
                        && chat.user1Type == Chat.TYPE.GUARD) {
                    userName = chat.user2Name;
                    userType = chat.user2Type;
                } else {
                    userName = chat.user1Name;
                    userType = chat.user1Type;
                }
                if (chat.unread != null && chat.unread > 0) {
                    unread.setVisibility(View.VISIBLE);
                    unread.setText(chat.unread.toString());
                } else {
                    unread.setVisibility(View.GONE);
                }

                title.setText(userName);
                time.setText(DateUtils.getRelativeTimeSpanString(UTILITY.stringToDate(chat.updateAt).getTime()));
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
            } else {
                addUsers.setVisibility(View.VISIBLE);
                unread.setVisibility(View.GONE);
                final ChannelRegistered registered = (ChannelRegistered) list.get(position);
                title.setText(registered.channel.name);
                time.setText(DateUtils.getRelativeTimeSpanString(UTILITY.stringToDate(registered.channel.updateAt).getTime()));
                image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.group_icon));
                holder.getBaseView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EventBus.getDefault().post(new OnClickChannel(registered));
                    }
                });
                addUsers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EventBus.getDefault().post(new OnClickChannelAdd(registered));
                    }
                });
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

    @Override
    public  int getItemViewType(int position) {
        if (list.get(position) instanceof Chat) {
            return ITEM_VIEW_CHAT;
        } else {
            return ITEM_VIEW_CHANNEL;
        }
    }

    public interface AdapterViewBinder<T> {
        void bind(ViewHolder holder, T item, int position);
    }

    public List<Object> getItems() {
        return list;
    }

    public void setItems(List<Object> objects) {
        this.list = objects;
        notifyDataSetChanged();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void replaceAll(List<Object> list) {
        this.list = new ArrayList<>(list);
        notifyDataSetChanged();
    }
}
