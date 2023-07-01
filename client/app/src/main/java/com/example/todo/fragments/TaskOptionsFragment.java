package com.example.todo.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.example.todo.R;
import com.example.todo.common.GeneralException;
import com.example.todo.custom.CustomToast;
import com.example.todo.utils.HttpClientHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class TaskOptionsFragment extends BottomSheetDialogFragment {
    private HttpClientHelper httpClientHelper;
    private AppCompatButton btnEdit, btnCompleted, btnDelete, btnClose;
    private setRefreshListener setRefreshListener;
    private long taskId;
    private long organization;
    private Context context;
    private int position;
    private static final String TAG = TaskOptionsFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.httpClientHelper = HttpClientHelper.getInstance(this.context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_task_options, container, false);
        this.btnEdit = rootView.findViewById(R.id.btn_edit);
        this.btnCompleted = rootView.findViewById(R.id.btn_completed);
        this.btnDelete = rootView.findViewById(R.id.btn_delete);
        this.btnClose = rootView.findViewById(R.id.btn_close);
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                TaskFormFragment taskFormFragment = new TaskFormFragment();
                taskFormFragment.setTaskId(taskId, organization, true, new TaskFormFragment.setRefreshListener() {
                    @Override
                    public void refresh() {
                        setRefreshListener.refresh();
                    }
                }, context);
                taskFormFragment.show(getFragmentManager(), taskFormFragment.getTag());
            }
        });
        this.btnCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
                alertDialogBuilder.setTitle(R.string.confirmation)
                        .setMessage(R.string.sureToMarkAsComplete)
                        .setPositiveButton(R.string.yes, (dia, which) -> {
                            completeTask();
                        })
                        .setNegativeButton(R.string.no, (dia, which) -> dia.cancel()).show();
            }
        });
        this.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
                alertDialogBuilder.setTitle(R.string.delete_confirmation)
                        .setMessage(R.string.sureToDelete)
                        .setPositiveButton(R.string.yes, (dia, which) -> {
                            deleteTask();
                        })
                        .setNegativeButton(R.string.no, (dia, which) -> dia.cancel()).show();
            }
        });
        this.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setTaskId(long taskId, long organization, int position, TaskOptionsFragment.setRefreshListener setRefreshListener, Context context) {
        this.taskId = taskId;
        this.context = context;
        this.organization = organization;
        this.position = position;
        this.setRefreshListener = setRefreshListener;
    }

    private void completeTask() {
        class UpdateTask extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    httpClientHelper.put(httpClientHelper.buildUrl(String.format("/task/%d/status/%s", taskId, "DONE"), null), null, Object.class);
                    return null;
                } catch (Exception e) {
                    Log.e(TAG, "error: ", e);
                    if (e instanceof GeneralException) {
                        return ((GeneralException) e).getCode();
                    }
                    return "ERROR";
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                dismiss();
                if (result == null) {
                    setRefreshListener.refresh();
                } else {
                    CustomToast.makeText(context, result, CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                }
            }
        }

        UpdateTask updateTask = new UpdateTask();
        updateTask.execute();
    }

    private void deleteTask() {
        class DeleteTask extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    httpClientHelper.delete(httpClientHelper.buildUrl(String.format("/task/%d", taskId), null), Object.class);
                    return null;
                } catch (Exception e) {
                    Log.e(TAG, "error: ", e);
                    if (e instanceof GeneralException) {
                        return ((GeneralException) e).getCode();
                    }
                    return "ERROR";
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                dismiss();
                if (result == null) {
                    setRefreshListener.refresh();
                } else {
                    CustomToast.makeText(context, result, CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                }
            }
        }

        DeleteTask deleteTask = new DeleteTask();
        deleteTask.execute();
    }

    public interface setRefreshListener {
        void refresh();
    }
}
