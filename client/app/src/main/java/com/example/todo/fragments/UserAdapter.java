package com.example.todo.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.model.dto.UserData;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {

    private List<UserData> userList;
    private Context context;
    private LayoutInflater inflater;
    private OnItemClickListener listener;
    private OnItemLongClickListener longListener;

    public UserAdapter(Context context, List<UserData> userList) {
        this.context = context;
        this.userList = userList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_user, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        UserData user = userList.get(position);
        holder.name.setText(user.getName());
        holder.mail.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        if (this.userList == null) {
            return 0;
        }
        return this.userList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(UserData item, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(UserData item, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longListener = listener;
    }

    public class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView name, mail;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.userLayout_name);
            mail = itemView.findViewById(R.id.userLayout_email);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position > RecyclerView.NO_POSITION) {
                    listener.onItemClick(userList.get(position), position);
                }
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (longListener != null) {
                int position = getAdapterPosition();
                if (position > RecyclerView.NO_POSITION) {
                    longListener.onItemLongClick(userList.get(position), position);
                }
            }
            return true;
        }
    }
}
