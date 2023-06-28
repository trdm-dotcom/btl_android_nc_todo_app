package com.example.todo.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.model.dto.TaskDto;
import com.example.todo.utils.HttpClientHelper;

import java.text.SimpleDateFormat;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private LayoutInflater inflater;
    private List<TaskDto> taskList;
    private Context context;
    private HttpClientHelper httpClientHelper;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public TaskAdapter(Context context, List<TaskDto> taskList) {
        this.context = context;
        this.taskList = taskList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public TaskAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder holder, int position) {
        TaskDto task = taskList.get(position);
        holder.title.setText(task.getTitle());
        holder.description.setText(task.getDescription());
        holder.status.setText(task.getStatus());
        holder.start.setText(dateFormat.format(task.getStartDate()));
        holder.end.setText(dateFormat.format(task.getEndDate()));
        holder.options.setOnClickListener(view -> showPopUpMenu(view, position));
    }

    private void showPopUpMenu(View view, int position) {
        final TaskDto task = taskList.get(position);
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_task_options);
        dialog.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btn_completed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
                alertDialogBuilder.setTitle(R.string.confirmation).setMessage(R.string.sureToMarkAsComplete).
                        setPositiveButton(R.string.yes, (dialog, which) -> {
                            completeTask(task, position);
                        })
                        .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
            }
        });
        dialog.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
                alertDialogBuilder.setTitle(R.string.delete_confirmation).setMessage(R.string.sureToDelete).
                        setPositiveButton(R.string.yes, (dialog, which) -> {
                            deleteTask(task, position);
                        })
                        .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
            }
        });
    }

    private void completeTask(TaskDto task, int position) {
        class UpdateTask extends AsyncTask<Void, Void, List<TaskDto>> {
            @Override
            protected List<TaskDto> doInBackground(Void... voids) {

                return taskList;
            }

            @Override
            protected void onPostExecute(List<TaskDto> tasks) {
                super.onPostExecute(tasks);
                updateAtPosition(position);
            }
        }

        UpdateTask updateTask = new UpdateTask();
        updateTask.execute();
    }

    private void deleteTask(TaskDto task, int position) {
        class DeleteTask extends AsyncTask<Void, Void, List<TaskDto>> {
            @Override
            protected List<TaskDto> doInBackground(Void... voids) {
                return taskList;
            }

            @Override
            protected void onPostExecute(List<TaskDto> tasks) {
                super.onPostExecute(tasks);
                removeAtPosition(position);
            }
        }

        DeleteTask deleteTask = new DeleteTask();
        deleteTask.execute();
    }

    private void removeAtPosition(int position) {
        taskList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, taskList.size());
    }

    private void updateAtPosition(int position) {
        TaskDto taskDto = taskList.get(position);
        taskDto.setStatus("DONE");
        taskList.set(position, taskDto);
        notifyItemChanged(position);
        notifyItemRangeChanged(position, taskList.size());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView start, end, title, description, status;
        ImageView options;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            start = itemView.findViewById(R.id.start);
            end = itemView.findViewById(R.id.end);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            status = itemView.findViewById(R.id.status);
            options = itemView.findViewById(R.id.options);
        }
    }
}
