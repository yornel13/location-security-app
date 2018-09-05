package com.icsseseguridad.locationsecurity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.model.Chat;
import com.icsseseguridad.locationsecurity.model.ChatLine;
import com.icsseseguridad.locationsecurity.util.AppPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yornel on 13-aug-18.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_VIEW_OTHER = 0;
    private static final int ITEM_VIEW_LOCAL = 1;

    private List<ChatLine> messages;
    private Context context;

    private Long myId;

    public ChatAdapter(Context context, List<ChatLine> messages) {
        this.messages = messages;
        this.context = context;
        this.myId = new AppPreferences(context.getApplicationContext()).getGuard().id;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case ITEM_VIEW_OTHER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_other, parent, false);
                return new ViewHolderAdmin(view);
            case ITEM_VIEW_LOCAL:
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_mine, parent, false);
                return new ViewHolderGuard(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!messages.isEmpty()) {
            ChatLine message = messages.get(position);
            System.out.println(new Gson().toJson(message));
            switch (holder.getItemViewType()) {
                case ITEM_VIEW_OTHER:
                    ViewHolderAdmin holderA = (ViewHolderAdmin) holder;
                    holderA.name.setText(message.senderName);
                    holderA.message.setText(message.text);
                    if (message.senderType == Chat.TYPE.GUARD) {
                        holderA.image.setImageDrawable(context.getResources().getDrawable(R.drawable.policeman));
                    } else {
                        holderA.image.setImageDrawable(context.getResources().getDrawable(R.drawable.admin_user));
                    }
                    holderA.time.setText(DateUtils.getRelativeTimeSpanString(message.createAt.getTime()));
                    break;
                case ITEM_VIEW_LOCAL:
                    default:
                    ViewHolderGuard holderG = (ViewHolderGuard) holder;
                    holderG.message.setText(message.text);
                    holderG.time.setText(DateUtils.getRelativeTimeSpanString(message.createAt.getTime()));
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
        if (messages.get(position).senderType == Chat.TYPE.GUARD
                && messages.get(position).senderId.equals(myId)) {
            return ITEM_VIEW_LOCAL;
        } else {
            return ITEM_VIEW_OTHER;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void replaceAll(List<ChatLine> items) {
        this.messages = new ArrayList<>(items);
        notifyDataSetChanged();
    }

    public Object removeItem(int position) {
        final Object model = messages.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItems(List<ChatLine> items) {
        this.messages.addAll(items);
        notifyDataSetChanged();
    }

    public void addItem(int position, ChatLine model) {
        messages.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final ChatLine model = messages.remove(fromPosition);
        messages.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public ChatLine getItem(int position) {
        return messages.get(position);
    }

}
