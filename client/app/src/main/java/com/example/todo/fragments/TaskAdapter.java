package com.example.todo.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.model.dto.TaskDto;

import java.text.SimpleDateFormat;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private LayoutInflater inflater;
    private List<TaskDto> taskList;
    private Context context;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    TaskFormFragment.setRefreshListener setRefreshListener;
    private static final String TAG = TaskAdapter.class.getSimpleName();
    private OnItemClickListener listener;
    private OnItemLongClickListener longListener;

    public TaskAdapter(Context context, List<TaskDto> taskList, TaskFormFragment.setRefreshListener setRefreshListener) {
        this.context = context;
        this.taskList = taskList;
        this.setRefreshListener = setRefreshListener;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longListener) {
        this.longListener = longListener;
    }

    public interface OnItemClickListener {
        void onItemClick(TaskDto item);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(TaskDto item, int position);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TaskDto task = taskList.get(position);
        holder.title.setText(task.getTitle());
        holder.description.setText(task.getDescription());
        holder.status.setText(task.getStatus());
        holder.start.setText(dateFormat.format(task.getStartDate()));
        holder.end.setText(dateFormat.format(task.getEndDate()));
    }

    private void removeAtPosition(int position) {
        taskList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, taskList.size());
    }

    private void updateStatusAtPosition(int position) {
        TaskDto taskDto = taskList.get(position);
        taskDto.setStatus("DONE");
        taskList.set(position, taskDto);
        notifyItemChanged(position);
        notifyItemRangeChanged(position, taskList.size());
    }

    @Override
    public int getItemCount() {
        if (taskList == null) {
            return 0;
        }
        return taskList.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView start, end, title, description, status;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            start = itemView.findViewById(R.id.start);
            end = itemView.findViewById(R.id.end);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            status = itemView.findViewById(R.id.status);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (listener != null) {
                int position = getAdapterPosition();
                TaskDto taskDto = taskList.get(position);
                Log.i(TAG, "onClick: " + position);
                listener.onItemClick(taskDto);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (longListener != null) {
                int position = getAdapterPosition();
                TaskDto taskDto = taskList.get(position);
                Log.i(TAG, "onLongClick: " + position);
                longListener.onItemLongClick(taskDto, position);
            }
            return true;
        }
    }
}
