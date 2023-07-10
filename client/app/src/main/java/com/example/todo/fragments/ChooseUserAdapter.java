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

public class ChooseUserAdapter extends RecyclerView.Adapter<ChooseUserAdapter.ChooseUserHolder> {
    private List<UserData> userList;
    private Context context;
    private LayoutInflater inflater;
    private OnItemClickListener listener;

    public ChooseUserAdapter(Context context, List<UserData> userList) {
        this.context = context;
        this.userList = userList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ChooseUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_user_choose, parent, false);
        return new ChooseUserAdapter.ChooseUserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChooseUserHolder holder, int position) {
        UserData user = userList.get(position);
        holder.name.setText(user.getName());
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

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class ChooseUserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name;

        public ChooseUserHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvChooseName);
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
    }
}
