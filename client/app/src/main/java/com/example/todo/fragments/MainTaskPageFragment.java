package com.example.todo.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.common.GeneralException;
import com.example.todo.custom.CustomToast;
import com.example.todo.model.dto.TaskDto;
import com.example.todo.utils.HttpClientHelper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainTaskPageFragment extends Fragment implements OrganizationFormFragment.setRefreshListener {
    private NavController navController;
    private HttpClientHelper httpClientHelper;
    private ImageView calendar, back;
    private TaskAdapter taskAdapter;
    private List<TaskDto> tasks = new ArrayList<>();
    private RecyclerView taskRecycler;
    private TextView addTask;
    private Long organization;
    private static final String TAG = MainTaskPageFragment.class.getSimpleName();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.httpClientHelper = HttpClientHelper.getInstance(getActivity());
        this.organization = (getArguments().getLong("organization"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_task_page, container, false);
        this.calendar = rootView.findViewById(R.id.calendar);
        this.taskRecycler = rootView.findViewById(R.id.mainRecyclerView);
        this.addTask = rootView.findViewById(R.id.addTask);
        this.back = rootView.findViewById(R.id.back);
        this.getSavedTasks(new Date());
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.init(view);
        this.calendar.setOnClickListener(v -> {

        });
        this.addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskFormFragment taskFormFragment = new TaskFormFragment();
//                taskFormFragment.setTaskId(0, false, ,getActivity());
                taskFormFragment.show(getFragmentManager(), taskFormFragment.getTag());
            }
        });
        this.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_mainTaskPageFragment_to_homeFragment);
            }
        });
    }

    private void setUpAdapter() {
        this.taskAdapter = new TaskAdapter(getActivity(), tasks, this::refresh);
        this.taskRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.taskRecycler.setAdapter(this.taskAdapter);
    }

    private void getSavedTasks(Date date) {
        class GetSavedTasks extends AsyncTask<Date, Void, Object> {
            ProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = ProgressDialog.show(getActivity(), "", "");
            }

            @Override
            protected Object doInBackground(Date... dates) {
                try {
                    Map<String, Object> queryParams = new HashMap<>();
                    queryParams.put("date", dateFormat.format(dates[0]));
                    queryParams.put("organization", organization);
                    return httpClientHelper.get(httpClientHelper.buildUrl("/task", queryParams), new TypeReference<List<TaskDto>>() {
                    });
                } catch (Exception e) {
                    Log.e(TAG, "error: ", e);
                    if (e instanceof GeneralException) {
                        return ((GeneralException) e).getCode();
                    }
                    return "ERROR";
                }
            }

            @Override
            protected void onPostExecute(Object result) {
                super.onPostExecute(result);
                mProgressDialog.dismiss();
                if (result instanceof String) {
                    CustomToast.makeText(getActivity(), result.toString(), CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                } else {
                    tasks = (List<TaskDto>) result;
                }
                setUpAdapter();
            }
        }
        GetSavedTasks savedTasks = new GetSavedTasks();
        savedTasks.execute(date);
    }

    private void init(View view) {
        this.navController = Navigation.findNavController(view);
    }

    @Override
    public void refresh() {
        getSavedTasks(new Date());
    }
}
