package com.icsseseguridad.locationsecurity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.icsseseguridad.locationsecurity.R;
import com.icsseseguridad.locationsecurity.events.OnUserCheck;
import com.icsseseguridad.locationsecurity.model.Admin;
import com.icsseseguridad.locationsecurity.model.Guard;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.bumptech.glide.request.RequestOptions.centerCropTransform;

/**
 * Created by Yornel on 03/5/2018.
 */

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> users;
    private Context context;

    public UserAdapter(Context context, List<Object> users) {
        this.users = users;
        this.context = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final ViewHolder viewHolder = (ViewHolder) holder;
        final Object user = users.get(position);

        if (user instanceof Guard) {
            Guard guard = (Guard) user;
            viewHolder.title.setText(guard.getFullname());
            viewHolder.type.setText("Guardia");
            loadPhoto(guard.photo, viewHolder.icon);
            viewHolder.type.setTextColor(context.getResources().getColor(R.color.colorBlue));
            viewHolder.checkbox.setChecked(guard.check);
        } else {
            Admin admin = (Admin) user;
            viewHolder.title.setText(admin.getFullname());
            viewHolder.type.setText("Administrador");
            loadPhoto(admin.photo, viewHolder.icon);
            viewHolder.type.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            viewHolder.checkbox.setChecked(admin.check);
        }
    }

    private void loadPhoto(String photo, ImageView view) {
        try {
            if (photo == null) {
                view.setImageDrawable(context.getDrawable(R.drawable.user_empty));
            } else {
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.user_empty)
                        .error(R.drawable.user_empty)
                        .transform(new RoundedCorners(20));
                Glide.with(context)
                        .setDefaultRequestOptions(options)
                        .load(photo)
                        .into(view);
            }
        } catch (Exception e) {
            view.setImageDrawable(context.getDrawable(R.drawable.user_empty));
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements
            CompoundButton.OnCheckedChangeListener,
            View.OnClickListener {

        LinearLayout row;

        TextView title;
        TextView type;
        ImageView icon;
        CheckBox checkbox;
        RelativeLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            row = (LinearLayout) itemView;
            title = itemView.findViewById(R.id.title);
            type = itemView.findViewById(R.id.type);
            icon = itemView.findViewById(R.id.icon);
            item = itemView.findViewById(R.id.item);
            checkbox = itemView.findViewById(R.id.checkbox);
            checkbox.setOnCheckedChangeListener(this);
            row.setOnClickListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            EventBus.getDefault().post(new OnUserCheck(getAdapterPosition(), isChecked));
        }

        @Override
        public void onClick(View view) {
            System.out.println("click");
            checkbox.setChecked(!checkbox.isChecked());
            // EventBus.getDefault().post(new OnUserCheck(getAdapterPosition(), checkbox.isChecked()));
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void replaceAll(List<Object> list) {
        this.users = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    public Object removeItem(int position) {
        final Object model = users.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItems(List<Object> products) {
        this.users.addAll(products);
        notifyDataSetChanged();
    }

    public void addItem(int position, Object model) {
        users.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Object model = users.remove(fromPosition);
        users.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public Object getItem(int position) {
        return users.get(position);
    }

}
