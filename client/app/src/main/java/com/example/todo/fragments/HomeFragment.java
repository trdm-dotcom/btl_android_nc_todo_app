package com.example.todo.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
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

public class HomeFragment extends Fragment implements OrganizationFormFragment.setRefreshListener {
    private NavController navController;
    private HttpClientHelper httpClientHelper;
    ImageView calendar;
    TaskAdapter taskAdapter;
    List<TaskDto> tasks = new ArrayList<>();
    RecyclerView taskRecycler;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.httpClientHelper = HttpClientHelper.getInstance(getActivity());
        setUpAdapter();
        getSavedTasks(new Date());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        this.calendar = rootView.findViewById(R.id.calendar);
        this.taskRecycler = rootView.findViewById(R.id.mainRecyclerView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.calendar.setOnClickListener(v -> {
            navController = Navigation.findNavController(v);
        });
    }

    public void setUpAdapter() {
        taskAdapter = new TaskAdapter(getActivity(), tasks);
        taskRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        taskRecycler.setAdapter(taskAdapter);
    }

    private void getSavedTasks(Date date) {
        class GetSavedTasks extends AsyncTask<Date, Void, List<TaskDto>> {
            @Override
            protected List<TaskDto> doInBackground(Date... dates) {
                try {
                    Map<String, Object> queryParams = new HashMap<>();
                    queryParams.put("date", dates);
                    return httpClientHelper.get(httpClientHelper.buildUrl("task", queryParams), new TypeReference<List<TaskDto>>() {
                    });
                } catch (GeneralException e) {
                    CustomToast.makeText(getActivity(), e.getMessage(), CustomToast.LENGTH_LONG, CustomToast.ERROR).show();
                    return new ArrayList<>();
                }
            }

            @Override
            protected void onPostExecute(List<TaskDto> taskDtos) {
                super.onPostExecute(taskDtos);
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
