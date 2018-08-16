package com.icsseseguridad.locationsecurity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.model.AppMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yornel on 13-aug-18.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // A menu item view type.
    private static final int ITEM_VIEW_ADMIN = 0;

    // The Native Express ad view type.
    private static final int ITEM_VIEW_GUARD = 1;

    private List<AppMessage> messages;
    private Context context;

    public ChatAdapter(Context context, List<AppMessage> messages) {
        this.messages = messages;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case ITEM_VIEW_ADMIN:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_admin, parent, false);
                return new ViewHolderAdmin(view);
            case ITEM_VIEW_GUARD:
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_guard, parent, false);
                return new ViewHolderGuard(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!messages.isEmpty()) {
            AppMessage message = messages.get(position);
            switch (holder.getItemViewType()) {
                case ITEM_VIEW_ADMIN:
                    ViewHolderAdmin holderA = (ViewHolderAdmin) holder;
                    holderA.name.setText(message.userName);
                    holderA.message.setText(message.message);
                    holderA.time.setText(DateUtils.getRelativeTimeSpanString(message.createDate.getTime()));
                    break;
                case ITEM_VIEW_GUARD:
                    default:
                    ViewHolderGuard holderG = (ViewHolderGuard) holder;
                    holderG.message.setText(message.message);
                    holderG.time.setText(DateUtils.getRelativeTimeSpanString(message.createDate.getTime()));
                    break;
            }
        }
    }

    public static class ViewHolderAdmin extends RecyclerView.ViewHolder {

        View row;

        ImageView image;
        TextView name;
        TextView message;
        TextView time;

        ViewHolderAdmin(View itemView) {
            super(itemView);
            row = itemView;
            image = row.findViewById(R.id.user_image);
            name = itemView.findViewById(R.id.user_name);
            message = itemView.findViewById(R.id.user_message);
            time = itemView.findViewById(R.id.message_time);
        }
    }

    public static class ViewHolderGuard extends RecyclerView.ViewHolder {

        View row;
        TextView message;
        TextView time;

        ViewHolderGuard(View itemView) {
            super(itemView);
            row = itemView;
            message = itemView.findViewById(R.id.user_message);
            time = itemView.findViewById(R.id.message_time);
        }

    }

    @Override
    public  int getItemViewType(int position) {
        if (messages.get(position).from == AppMessage.FROM.ANDROID) {
            return ITEM_VIEW_GUARD;
        } else {
            return ITEM_VIEW_ADMIN;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void replaceAll(List<AppMessage> items) {
        this.messages = new ArrayList<>(items);
        notifyDataSetChanged();
    }

    public Object removeItem(int position) {
        final Object model = messages.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItems(List<AppMessage> items) {
        this.messages.addAll(items);
        notifyDataSetChanged();
    }

    public void addItem(int position, AppMessage model) {
        messages.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final AppMessage model = messages.remove(fromPosition);
        messages.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public AppMessage getItem(int position) {
        return messages.get(position);
    }

}
